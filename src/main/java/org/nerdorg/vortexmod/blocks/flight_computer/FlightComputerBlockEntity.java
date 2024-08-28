package org.nerdorg.vortexmod.blocks.flight_computer;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.nerdorg.vortexmod.blocks.time_rotor.TimeRotorBlock;
import org.nerdorg.vortexmod.gui.flight_computer.FlightComputerGuiMenu;
import org.nerdorg.vortexmod.index.VMBlocks;
import org.nerdorg.vortexmod.ship_management.ShipAssembler;
import org.nerdorg.vortexmod.ship_management.ShipController;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.assembly.ShipAssemblyKt;

import java.util.List;
import java.util.function.Predicate;

public class FlightComputerBlockEntity extends KineticBlockEntity implements MenuProvider {

    public final ContainerData data;
    private ServerShip serverShip;
    private ShipController control;
    private boolean shouldDisassembleWhenPossible;

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
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        tooltip.add(Component.literal(spacing).append(Lang.translateDirect("gui.goggles.at_current_speed").withStyle(ChatFormatting.DARK_GRAY)));
        return true;
    }

    @Override
    public float calculateStressApplied() {
        float impact = 16f;
        this.lastStressApplied = impact;
        return impact;
    }

    @Override
    public void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
    }

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
    }

    private boolean firstTickState = true;

    @Override
    public void tick() {
        super.tick();
        if(level.isClientSide()) return;
        if(firstTickState) firstTick();
        firstTickState = false;

        if (this.serverShip == null) {
            updateShipReference((ServerLevel) level, getBlockPos());
            if (Math.abs(getSpeed()) > 0 && isSpeedRequirementFulfilled()) {

            }
        }
        else {
            if (shouldDisassembleWhenPossible && this.control.canDisassemble()) {
                this.disassemble();
            }

            if (this.control != null) {
                this.control.serverShip = this.serverShip;
            }

            if (Math.abs(getSpeed()) > 0 && isSpeedRequirementFulfilled()) {

            }
        }
    }

    private void updateShipReference(ServerLevel serverLevel, BlockPos pos) {
        this.serverShip = VSGameUtilsKt.getShipObjectManagingPos(serverLevel, pos.getX(), pos.getY(), pos.getZ());
        if (this.serverShip != null) {
            this.control = this.serverShip.getAttachment(ShipController.class);

            if (this.control == null) {
                this.serverShip.saveAttachment(ShipController.class, new ShipController());
            }
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

    public void firstTick() {

    };

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
