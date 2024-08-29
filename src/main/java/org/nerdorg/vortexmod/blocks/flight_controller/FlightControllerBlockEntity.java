package org.nerdorg.vortexmod.blocks.flight_controller;

import com.simibubi.create.content.kinetics.base.HorizontalAxisKineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.nerdorg.vortexmod.blocks.types.TardisComponentBlockEntity;
import org.nerdorg.vortexmod.gui.flight_computer.FlightComputerGuiMenu;
import org.nerdorg.vortexmod.index.VMBlocks;
import org.nerdorg.vortexmod.ship_management.ShipAssembler;
import org.nerdorg.vortexmod.ship_management.ShipController;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.api.SeatedControllingPlayer;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.ValkyrienSkiesMod;
import org.valkyrienskies.mod.common.entity.ShipMountingEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class FlightControllerBlockEntity extends TardisComponentBlockEntity {

    private List<ShipMountingEntity> seats = new ArrayList<ShipMountingEntity>();

    public FlightControllerBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public float calculateStressApplied() {
        float impact = 0.5f;
        this.lastStressApplied = impact;
        return impact;
    }

    @Override
    public void tick() {
        super.tick();
        if(level.isClientSide()) return;
    }

    public ShipMountingEntity spawnSeat(BlockPos blockPos, BlockState state, ServerLevel level) {
        // Get the direction the block is facing
        Direction facing = Direction.get(Direction.AxisDirection.POSITIVE, state.getValue(HorizontalAxisKineticBlock.HORIZONTAL_AXIS));
        BlockPos newPos = blockPos.relative(facing.getOpposite());

        BlockState newState = level.getBlockState(newPos);
        VoxelShape newShape = newState.getShape(level, newPos);
        Block newBlock = newState.getBlock();
        double height = 0.5;

        // Check if the new block is not air
        if (!newState.isAir()) {
            if (newBlock instanceof StairBlock) {
                if (!newState.hasProperty(StairBlock.HALF) || newState.getValue(StairBlock.HALF) == Half.BOTTOM) {
                    height = 0.5; // Valid StairBlock
                } else {
                    height = newShape.max(Direction.Axis.Y);
                }
            } else {
                height = newShape.max(Direction.Axis.Y);
            }
        }

        // Create the entity
        ShipMountingEntity entity = ValkyrienSkiesMod.SHIP_MOUNTING_ENTITY_TYPE.create(level);
        if (entity != null) {
            Vector3d seatEntityPos = new Vector3d(newPos.getX() + 0.5, (newPos.getY() - 0.5) + height, newPos.getZ() + 0.5);
            entity.moveTo(seatEntityPos.x(), seatEntityPos.y(), seatEntityPos.z());

            Vec3i normalVec = Direction.get(Direction.AxisDirection.POSITIVE, state.getValue(HorizontalAxisKineticBlock.HORIZONTAL_AXIS)).getNormal();

            entity.lookAt(EntityAnchorArgument.Anchor.EYES,
                    new Vec3(normalVec.getX(), normalVec.getY(), normalVec.getZ()).add(entity.position())
            );

            entity.setController(true);
            level.addFreshEntityWithPassengers(entity);
        }

        return entity;
    }

    public boolean startRiding(Player player, boolean force, BlockPos blockPos, BlockState state, ServerLevel level) {
        if (!(Math.abs(getSpeed()) > 0 && isSpeedRequirementFulfilled())) {
            player.displayClientMessage(Component.literal("You must input a speed of 32 rpm to use this.").withStyle(ChatFormatting.RED), true);
            return false;
        }

        for (int i = seats.size() - 1; i >= 0; i--) {
            ShipMountingEntity seat = seats.get(i);
            if (!seat.isVehicle()) {
                seat.kill();
                seats.remove(i);
            } else if (!seat.isAlive()) {
                seats.remove(i);
            }
        }

        ShipMountingEntity seat = spawnSeat(blockPos, state, level);
        boolean ride = player.startRiding(seat, force);

        if (ride) {
            if (control != null) {
                Direction facing = Direction.get(Direction.AxisDirection.POSITIVE, state.getValue(HorizontalAxisKineticBlock.HORIZONTAL_AXIS));

                control.seatedPlayer = player;
                this.serverShip.saveAttachment(SeatedControllingPlayer.class, new SeatedControllingPlayer(facing));
            }
            seats.add(seat);
        }

        return ride;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void tickAudio() {
        super.tickAudio();

        float componentSpeed = Math.abs(getSpeed());
        if (componentSpeed == 0 || !isSpeedRequirementFulfilled())
            return;
    }

    @Override
    protected Block getStressConfigKey() {
        return VMBlocks.FLIGHT_COMPUTER.get();
    }

    @Override
    public void remove() {
        super.remove();
    }
}
