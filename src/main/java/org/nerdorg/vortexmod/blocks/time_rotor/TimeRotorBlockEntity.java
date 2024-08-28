package org.nerdorg.vortexmod.blocks.time_rotor;

import java.util.List;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.utility.Lang;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.nerdorg.vortexmod.index.VMBlocks;

public class TimeRotorBlockEntity extends KineticBlockEntity {

    public TimeRotorBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        tooltip.add(Component.literal(spacing).append(Lang.translateDirect("gui.goggles.at_current_speed").withStyle(ChatFormatting.DARK_GRAY)));
        return true;
    }

    @Override
    public float calculateStressApplied() {
        float impact = 64f;
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

        if(Math.abs(getSpeed()) > 0 && isSpeedRequirementFulfilled()) {
            if (!this.getBlockState().getValue(TimeRotorBlock.ENABLED))
                level.setBlock(getBlockPos(), this.getBlockState().setValue(TimeRotorBlock.ENABLED, true), 3);
        }
        else {
            if (this.getBlockState().getValue(TimeRotorBlock.ENABLED))
                level.setBlock(getBlockPos(), this.getBlockState().setValue(TimeRotorBlock.ENABLED, false), 3);
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
        return VMBlocks.TIME_ROTOR.get();
    }

    @Override
    public void remove() {
        super.remove();
    }

    public void firstTick() {

    };
}
