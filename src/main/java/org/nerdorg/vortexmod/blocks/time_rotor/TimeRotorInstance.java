package org.nerdorg.vortexmod.blocks.time_rotor;

import com.jozufozu.flywheel.api.Instancer;
import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.core.materials.FlatLit;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.nerdorg.vortexmod.index.VMBlocks;

public class TimeRotorInstance extends KineticBlockEntityInstance<TimeRotorBlockEntity> {
    protected RotatingData rotatingModel1;
    protected RotatingData rotatingModel2;

    public TimeRotorInstance(MaterialManager materialManager, TimeRotorBlockEntity blockEntity) {
        super(materialManager, blockEntity);
    }

    public void init() {
        this.rotatingModel1 = this.setup((RotatingData)this.getShaftModel().createInstance());
        this.rotatingModel2 = this.setup((RotatingData)this.getRotorModel().createInstance());

        rotatingModel1.setRotationAxis(axis)
                .setRotationalSpeed(getBlockEntitySpeed())
                .setRotationOffset(-getRotationOffset(axis))
                .setColor(blockEntity)
                .setPosition(getInstancePosition());

        rotatingModel2.setRotationAxis(axis)
                .setRotationalSpeed(-getBlockEntitySpeed() / 4)
                .setRotationOffset(-getRotationOffset(axis))
                .setColor(blockEntity)
                .setPosition(getInstancePosition());
    }

    public void update() {
        this.updateRotation(this.rotatingModel1);
        this.updateRotation(this.rotatingModel2);
        rotatingModel2.setRotationalSpeed(-getBlockEntitySpeed() / 4);
    }

    public void updateLight() {
        this.relight(this.pos, new FlatLit[]{this.rotatingModel1, this.rotatingModel2});
    }

    public void remove() {
        this.rotatingModel1.delete();
        this.rotatingModel2.delete();
    }

    protected BlockState getRenderedShaftBlockState() {
        return AllBlocks.SHAFT.getDefaultState().setValue(ShaftBlock.AXIS, blockState.getValue(TimeRotorBlock.FACING).getAxis());
    }

    protected Instancer<RotatingData> getShaftModel() {
        return this.getRotatingMaterial().getModel(this.getRenderedShaftBlockState());
    }

    protected BlockState getRenderedRotorBlockState() {
        return (Math.abs(blockEntity.getSpeed()) > 0 && blockEntity.isSpeedRequirementFulfilled()) ? VMBlocks.TIME_ROTOR.getDefaultState().setValue(TimeRotorBlock.ENABLED, false).setValue(TimeRotorBlock.FACING, blockState.getValue(TimeRotorBlock.FACING)) : VMBlocks.TIME_ROTOR.getDefaultState().setValue(TimeRotorBlock.ENABLED, true).setValue(TimeRotorBlock.FACING, blockState.getValue(TimeRotorBlock.FACING));
    }

    protected Instancer<RotatingData> getRotorModel() {
        return this.getRotatingMaterial().getModel(this.getRenderedRotorBlockState());
    }
}
