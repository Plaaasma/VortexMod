package org.nerdorg.vortexmod.blocks.flight_computer;

import com.jozufozu.flywheel.api.Instancer;
import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.core.materials.FlatLit;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.nerdorg.vortexmod.VortexMod;
import org.nerdorg.vortexmod.index.VMBlocks;

public class FlightComputerInstance extends KineticBlockEntityInstance<FlightComputerBlockEntity> {
    protected RotatingData rotatingModel1;
    protected RotatingData rotatingModel2;
    protected RotatingData rotatingModel3;

    public FlightComputerInstance(MaterialManager materialManager, FlightComputerBlockEntity blockEntity) {
        super(materialManager, blockEntity);
    }

    public void init() {
        this.rotatingModel1 = this.setup((RotatingData)this.getShaftModel().createInstance());
        this.rotatingModel2 = this.setup((RotatingData)this.getShaftModel2().createInstance());
        this.rotatingModel3 = this.setup((RotatingData)this.getHalfShaftModel().createInstance());

        Direction direction = blockState.getValue(FlightComputerBlock.FACING);

        Direction.Axis first_axis = direction == Direction.UP || direction == Direction.DOWN ?
                Direction.Axis.Z : (direction == Direction.EAST || direction == Direction.WEST ? Direction.Axis.Z : Direction.Axis.X);
        Direction.Axis second_axis = direction == Direction.UP || direction == Direction.DOWN ?
                Direction.Axis.X : Direction.Axis.Y;
        rotatingModel1.setRotationAxis(first_axis)
                .setRotationalSpeed(getBlockEntitySpeed())
                .setRotationOffset(-getRotationOffset(first_axis))
                .setColor(blockEntity)
                .setPosition(getInstancePosition());

        rotatingModel2.setRotationAxis(second_axis)
                .setRotationalSpeed(getBlockEntitySpeed())
                .setRotationOffset(-getRotationOffset(second_axis))
                .setColor(blockEntity)
                .setPosition(getInstancePosition());

        rotatingModel3.setRotationAxis(axis)
                .setRotationalSpeed(getBlockEntitySpeed())
                .setRotationOffset(-getRotationOffset(axis))
                .setColor(blockEntity)
                .setPosition(getInstancePosition());
    }

    public void update() {
        Direction direction = blockState.getValue(FlightComputerBlock.FACING);

        Direction.Axis first_axis = direction == Direction.UP || direction == Direction.DOWN ?
                Direction.Axis.Z : (direction == Direction.EAST || direction == Direction.WEST ? Direction.Axis.Z : Direction.Axis.X);
        Direction.Axis second_axis = direction == Direction.UP || direction == Direction.DOWN ?
                Direction.Axis.X : Direction.Axis.Y;

        this.updateRotation(this.rotatingModel1, first_axis);
        this.updateRotation(this.rotatingModel2, second_axis);
        this.updateRotation(this.rotatingModel3, axis);
    }

    public void updateLight() {
        this.relight(this.pos, new FlatLit[]{this.rotatingModel1, this.rotatingModel2, this.rotatingModel3});
    }

    public void remove() {
        this.rotatingModel1.delete();
        this.rotatingModel2.delete();
        this.rotatingModel3.delete();
    }

    protected BlockState getRenderedShaftBlockState() {
        Direction direction = blockState.getValue(FlightComputerBlock.FACING);
        Direction.Axis first_axis = direction == Direction.UP || direction == Direction.DOWN ?
                Direction.Axis.Z : (direction == Direction.EAST || direction == Direction.WEST ? Direction.Axis.Z : Direction.Axis.X);

        return AllBlocks.SHAFT.getDefaultState().setValue(ShaftBlock.AXIS, first_axis);
    }

    protected Instancer<RotatingData> getShaftModel() {
        return this.getRotatingMaterial().getModel(this.getRenderedShaftBlockState());
    }

    protected BlockState getRenderedShaftBlockState2() {
        Direction direction = blockState.getValue(FlightComputerBlock.FACING);
        Direction.Axis second_axis = direction == Direction.UP || direction == Direction.DOWN ?
                Direction.Axis.X : Direction.Axis.Y;

        return AllBlocks.SHAFT.getDefaultState().setValue(ShaftBlock.AXIS, second_axis);
    }

    protected Instancer<RotatingData> getShaftModel2() {
        return this.getRotatingMaterial().getModel(this.getRenderedShaftBlockState2());
    }

    protected Instancer<RotatingData> getHalfShaftModel() {
        Direction direction = blockState.getValue(FlightComputerBlock.FACING);
        return this.getRotatingMaterial().getModel(AllPartialModels.SHAFT_HALF, blockState, direction);
    }
}
