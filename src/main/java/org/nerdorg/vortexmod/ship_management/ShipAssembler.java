package org.nerdorg.vortexmod.ship_management;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import kotlin.Triple;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraftforge.network.NetworkDirection;
import org.joml.*;
import org.nerdorg.vortexmod.VortexMod;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.apigame.world.IPlayer;
import org.valkyrienskies.core.util.datastructures.DenseBlockPosSet;
import org.valkyrienskies.mod.common.assembly.ShipAssemblyKt;
import org.valkyrienskies.mod.common.networking.PacketRestartChunkUpdates;
import org.valkyrienskies.mod.common.networking.PacketStopChunkUpdates;
import oshi.util.tuples.Pair;

import java.lang.Math;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.valkyrienskies.mod.util.RelocationUtilKt.updateBlock;

public class ShipAssembler {
    public static ServerShip collectBlocks(ServerLevel level, BlockPos center, Predicate<BlockState> predicate) {
        DenseBlockPosSet blocks = new DenseBlockPosSet();

        blocks.add(center.getX(), center.getY(), center.getZ());
        boolean result = bfs(level, center, blocks, predicate);

        if (result) {
            return ShipAssemblyKt.createNewShipWithBlocks(center, blocks, level);
        }
        else {
            return null;
        }
    }

    private static double roundToNearestMultipleOf(double number, double multiple) {
        return multiple * Math.round(number / multiple);
    }

    // Modified from https://gamedev.stackexchange.com/questions/83601/from-3d-rotation-snap-to-nearest-90-directions
    private static AxisAngle4d snapRotation(AxisAngle4d direction) {
        double x = Math.abs(direction.x);
        double y = Math.abs(direction.y);
        double z = Math.abs(direction.z);
        double angle = roundToNearestMultipleOf(direction.angle, Math.PI / 2);

        if (x > y && x > z) {
            return direction.set(angle, Math.signum(direction.x), 0.0, 0.0);
        } else if (y > x && y > z) {
            return direction.set(angle, 0.0, Math.signum(direction.y), 0.0);
        } else {
            return direction.set(angle, 0.0, 0.0, Math.signum(direction.z));
        }
    }

    private static Rotation rotationFromAxisAngle(AxisAngle4d axis) {
        if (Math.abs(axis.y) < 0.1) {
            // If the axis isn't Y, either we're tilted up/down (which should not happen often)
            // or we haven't moved and it's along the z axis with a magnitude of 0 for some reason.
            // In these cases, we don't rotate.
            return Rotation.NONE;
        }

        // Normalize into counterclockwise rotation (i.e., positive y-axis, according to testing + right-hand rule)
        if (Math.signum(axis.y) < 0.0) {
            axis.y = 1.0;
            // The angle is always positive and < 2pi coming in
            axis.angle = 2.0 * Math.PI - axis.angle;
            axis.angle %= (2.0 * Math.PI);
        }

        double eps = 0.001;
        if (axis.angle < eps)
            return Rotation.NONE;
        else if (Math.abs(axis.angle - Math.PI / 2.0) < eps)
            return Rotation.COUNTERCLOCKWISE_90;
        else if (Math.abs(axis.angle - Math.PI) < eps)
            return Rotation.CLOCKWISE_180;
        else if (Math.abs(axis.angle - 3.0 * Math.PI / 2.0) < eps)
            return Rotation.CLOCKWISE_90;
        else {
            VortexMod.LOGGER.warn("Failed to convert " + axis + " into a rotation");
            return Rotation.NONE;
        }
    }

    public static void unfillShip(ServerLevel level, ServerShip ship, BlockPos shipCenter, BlockPos center) {
        ship.setStatic(true);

        Rotation rotation = rotationFromAxisAngle(
                snapRotation(new AxisAngle4d(ship.getTransform().getShipToWorldRotation()))
        );

        // Ship's rotation rounded to nearest 90 degrees
        Matrix4d shipToWorld = new Matrix4d()
                .translate(ship.getTransform().getPositionInWorld())
                .rotate(snapRotation(new AxisAngle4d(ship.getTransform().getShipToWorldRotation())))
                .scale(ship.getTransform().getShipToWorldScaling())
                .translate(-ship.getTransform().getPositionInShip().x(),
                        -ship.getTransform().getPositionInShip().y(),
                        -ship.getTransform().getPositionInShip().z());

        Vector3d alloc0 = new Vector3d();

        Map<ChunkPos, Pair<ChunkPos, ChunkPos>> chunksToBeUpdated = new HashMap<>();

        ship.getActiveChunksSet().forEach((chunkX, chunkZ) -> {
            chunksToBeUpdated.put(new ChunkPos(chunkX, chunkZ), new Pair<>(new ChunkPos(chunkX, chunkZ), new ChunkPos(chunkX, chunkZ)));
        });

        List<Pair<ChunkPos, ChunkPos>> chunkPairs = List.copyOf(chunksToBeUpdated.values());
        List<ChunkPos> chunkPoses = chunkPairs.stream().flatMap(pair -> Arrays.asList(pair.getA(), pair.getB()).stream()).toList();
        List<Vector2i> chunkPosesJOML = chunkPoses.stream().map(JomlUtils::toJOML).toList();

//        new PacketStopChunkUpdates(chunkPosesJOML).receivedByClient();

        Set<Triple<BlockPos, BlockPos, BlockState>> toUpdate = Sets.newHashSet();

        ship.getActiveChunksSet().forEach((chunkX, chunkZ) -> {
            LevelChunk chunk = level.getChunk(chunkX, chunkZ);
            LevelChunkSection[] sections = chunk.getSections();
            for (int sectionIndex = 0; sectionIndex < sections.length; sectionIndex++) {
                LevelChunkSection section = sections[sectionIndex];
                if (section == null || section.hasOnlyAir()) continue;
                for (int x = 0; x <= 15; x++) {
                    for (int y = 0; y <= 15; y++) {
                        for (int z = 0; z <= 15; z++) {
                            BlockState state = section.getBlockState(x, y, z);
                            if (state.isAir()) continue;

                            int realX = (chunkX << 4) + x;
                            int bottomY = sectionIndex * 16;
                            int realY = bottomY + y + level.dimensionType().minY();
                            int realZ = (chunkZ << 4) + z;

                            Vector3d inWorldPos = shipToWorld.transformPosition(alloc0.set(realX + 0.5, realY + 0.5, realZ + 0.5)).floor();

                            BlockPos inWorldBlockPos = new BlockPos((int) inWorldPos.x(), (int) inWorldPos.y(), (int) inWorldPos.z());
                            BlockPos inShipPos = new BlockPos(realX, realY, realZ);

                            BlockState rotatedState = rotateBlockState(state, rotation);

                            toUpdate.add(new Triple<>(inShipPos, inWorldBlockPos, state));
                            level.setBlock(inShipPos, Blocks.AIR.defaultBlockState(), 3 | 16);
                            level.setBlock(inWorldBlockPos, rotatedState, 3 | 16);
                        }
                    }
                }
            }
        });

        // Update the blocks after they're set to prevent blocks from breaking
        for (Triple<BlockPos, BlockPos, BlockState> triple : toUpdate) {
            updateBlock(level, triple.getFirst(), triple.getSecond(), triple.getThird());
        }

//        level.getServer().executeIfPossible(
//                // This condition will return true if all modified chunks have been both loaded AND
//                // chunk update packets were sent to players
//                () -> {
//                    // Once all the chunk updates are sent to players, we can tell them to restart chunk updates
//                    new PacketRestartChunkUpdates(chunkPosesJOML).receivedByClient();
//                }
//        );
    }

    private static BlockState rotateBlockState(BlockState state, Rotation rotation) {
        // Rotate block state based on the rotation applied to the ship
        if (state.hasProperty(BlockStateProperties.FACING)) {
            Direction facing = state.getValue(BlockStateProperties.FACING);
            Direction rotatedFacing = rotation.rotate(facing);
            state = state.setValue(BlockStateProperties.FACING, rotatedFacing);
        }
        // Add similar checks for other properties like BlockStateProperties.HORIZONTAL_FACING, etc.
        return state;
    }

    private static boolean bfs(ServerLevel level, BlockPos start, DenseBlockPosSet blocks, Predicate<BlockState> predicate) {

        DenseBlockPosSet blacklist = new DenseBlockPosSet();
        ObjectArrayList<BlockPos> stack = new ObjectArrayList<>();

        directions(start, stack::push);

        while (!stack.isEmpty()) {
            BlockPos pos = stack.pop();

            if (predicate.test(level.getBlockState(pos))) {
                blocks.add(pos.getX(), pos.getY(), pos.getZ());
                directions(pos, directionPos -> {
                    if (!blacklist.contains(directionPos.getX(), directionPos.getY(), directionPos.getZ())) {
                        blacklist.add(directionPos.getX(), directionPos.getY(), directionPos.getZ());
                        stack.push(directionPos);
                    }
                });
            }

            if (blocks.size() > 1024) {
                VortexMod.LOGGER.info("Stopped ship assembly due to too many blocks");
                return false;
            }
        }

        VortexMod.LOGGER.info("Assembled ship with " + blocks.size() + " blocks, out of " + 1024 + " allowed");

        return true;
    }

    private static void directions(BlockPos center, Consumer<BlockPos> lambda) {
        //if (!EurekaConfig.SERVER.diagonals) {
        for (Direction direction : Direction.values()) {
            lambda.accept(center.relative(direction));
        }
        //}

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x != 0 || y != 0 || z != 0) {
                        lambda.accept(center.offset(x, y, z));
                    }
                }
            }
        }
    }
}
