package org.nerdorg.vortexmod.blocks.flight_computer;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.nerdorg.vortexmod.VortexMod;
import org.nerdorg.vortexmod.blocks.time_rotor.TimeRotorBlock;
import org.nerdorg.vortexmod.blocks.types.TardisComponentBlockEntity;
import org.nerdorg.vortexmod.gui.flight_computer.FlightComputerGuiMenu;
import org.nerdorg.vortexmod.index.VMBlocks;
import org.nerdorg.vortexmod.packets.s2c.SyncComputerInfoPacket;
import org.nerdorg.vortexmod.ship_management.JomlUtils;
import org.nerdorg.vortexmod.ship_management.ShipAssembler;
import org.nerdorg.vortexmod.ship_management.ShipController;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.assembly.ShipAssemblyKt;

import java.util.List;
import java.util.function.Predicate;

public class FlightComputerBlockEntity extends TardisComponentBlockEntity implements MenuProvider {

    public final ContainerData data;
    private boolean shouldDisassembleWhenPossible;
    public BlockPos currentPos = BlockPos.ZERO;
    public Vector3d currentRotation = new Vector3d(0, 0 ,0);
    public BlockPos targetPos = BlockPos.ZERO;
    public Vector3d targetRotation = new Vector3d(0, 0, 0);
    public double speed = 0;
    public float max_stress = 0;
    public float stress_amount = 0;
    public boolean assembled = false;
    public boolean stabilizers = false;
    public boolean antigrav = false;

    public FlightComputerBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        this.data = new SimpleContainerData(1);
//        this.data = new ContainerData() {
//            @Override
//            public int get(int pIndex) {
//                return switch (pIndex) {
//                    case 0 -> KeypadBlockEntity.this.is_active;
//                    default -> 0;
//                };
//            }
//
//            @Override
//            public void set(int pIndex, int pValue) {
//                switch (pIndex) {
//                    case 0 -> KeypadBlockEntity.this.is_active = pValue;
//                }
//            }
//
//            @Override
//            public int getCount() {
//                return 1;
//            }
//        };
    }

    @Override
    public float calculateStressApplied() {
        float impact = 0.25f;
        this.lastStressApplied = impact;
        return impact;
    }

    @Override
    public void tick() {
        super.tick();
        if(level.isClientSide()) return;

        if (this.serverShip != null) {
            if (shouldDisassembleWhenPossible && this.control.canDisassemble()) {
                this.disassemble();
            }
        }

        BlockPos currentPos1;
        Vector3d currentRotation1;
        if (this.serverShip != null) {
            Vector3dc shipPos = this.serverShip.getTransform().getPositionInWorld();
            currentPos1 = new BlockPos((int) shipPos.x(), (int) shipPos.y(), (int) shipPos.z());

            Quaterniondc shipRot = this.serverShip.getTransform().getShipToWorldRotation();
            currentRotation1 = JomlUtils.toEulerAngles((Quaterniond) shipRot);
        }
        else {
            currentPos1 = this.getBlockPos();
            currentRotation1 = new Vector3d(0, 0, 0);
        }

        BlockPos targetPos1;
        Vector3d targetRotation1;
        if (this.tardisInfo != null) {
            targetPos1 = this.tardisInfo.target_location;
            targetRotation1 = new Vector3d(this.tardisInfo.target_rotation.x(), this.tardisInfo.target_rotation.y(), this.tardisInfo.target_rotation.z());
        }
        else {
            targetPos1 = this.getBlockPos();
            targetRotation1 = new Vector3d(0, 0, 0);
        }

        double speed1 = 0;
        boolean stabilizers1 = false;
        boolean antigrav1 = false;
        if (this.control != null) {
            speed1 = this.control.cspeed;
            stabilizers1 = this.control.stabilizer;
            antigrav1 = this.control.antigrav;
        }

        if (this.level.getGameTime() % 10 == 0) {
            VortexMod.Network.send(PacketDistributor.ALL.noArg(),
                    new SyncComputerInfoPacket(
                            this.getBlockPos(),
                            currentPos1,
                            currentRotation1.x(),
                            currentRotation1.y(),
                            currentRotation1.z(),
                            targetPos1,
                            targetRotation1.x(),
                            targetRotation1.y(),
                            targetRotation1.z(),
                            speed1,
                            this.capacity,
                            this.stress,
                            this.serverShip != null,
                            stabilizers1,
                            antigrav1
                    ));
        }
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

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.vortexmod.flight_computer");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new FlightComputerGuiMenu(i, inventory, this, this.data);
    }

    public void assemble() {
        BlockState blockState = level.getBlockState(getBlockPos());
        if (!blockState.is(VMBlocks.FLIGHT_COMPUTER.get()) || this.serverShip != null)
            return;

        Predicate<BlockState> excludeBlocksPredicate = pBlockState ->
                !pBlockState.is(Blocks.WATER) && !pBlockState.is(Blocks.LAVA) && !pBlockState.is(Blocks.BEDROCK) && !pBlockState.is(Blocks.AIR) && !pBlockState.is(Blocks.CAVE_AIR) && !pBlockState.is(Blocks.VOID_AIR);

        ServerShip builtShip = ShipAssembler.collectBlocks((ServerLevel) level, getBlockPos(), excludeBlocksPredicate);
    }

    public void disassemble() {
        if (serverShip == null || level == null || control == null) {
            return;
        }

        if (!control.canDisassemble()) {
            shouldDisassembleWhenPossible = true;
            control.disassembling = true;
            control.aligning = true;
            return;
        }

        Vector3dc inWorld = serverShip.getShipToWorld().transformPosition(new Vector3d(this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ()));

        ShipAssembler.unfillShip(
                (ServerLevel) level,
                serverShip,
                this.getBlockPos(),
                new BlockPos((int) inWorld.x(), (int) inWorld.y(), (int) inWorld.z())
        );

        shouldDisassembleWhenPossible = false;
    }
}
