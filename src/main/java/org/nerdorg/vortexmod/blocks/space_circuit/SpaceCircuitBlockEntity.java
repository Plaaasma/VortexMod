package org.nerdorg.vortexmod.blocks.space_circuit;

import net.minecraft.core.BlockPos;
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
import org.nerdorg.vortexmod.blocks.types.TardisComponentBlockEntity;
import org.nerdorg.vortexmod.gui.flight_computer.FlightComputerGuiMenu;
import org.nerdorg.vortexmod.index.VMBlocks;
import org.nerdorg.vortexmod.ship_management.ShipAssembler;
import org.valkyrienskies.core.api.ships.ServerShip;

import java.util.function.Predicate;

public class SpaceCircuitBlockEntity extends TardisComponentBlockEntity {

    public SpaceCircuitBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public float calculateStressApplied() {
        float impact = 2f;
        this.lastStressApplied = impact;
        return impact;
    }

    @Override
    public void tick() {
        super.tick();
        if(level.isClientSide()) return;

        if (this.serverShip != null) {
            if (this.control != null) {
                if (this.getBlockState().getValue(SpaceCircuitBlock.POWERED) && (Math.abs(getSpeed()) > 0 && isSpeedRequirementFulfilled())) {
                    this.control.space_circuit = true;
                }
                else {
                    this.control.space_circuit = false;
                }
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
        if (this.serverShip != null) {
            if (this.control != null) {
                this.control.space_circuit = false;
            }
        }
        super.remove();
    }
}
