package org.nerdorg.vortexmod.blocks.flight_controller;

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

public class FlightControllerInstance extends KineticBlockEntityInstance<FlightControllerBlockEntity> {
    protected RotatingData rotatingModel1;

    public FlightControllerInstance(MaterialManager materialManager, FlightControllerBlockEntity blockEntity) {
        super(materialManager, blockEntity);
    }

    public void init() {
        this.rotatingModel1 = this.setup((RotatingData)this.getHalfShaftModel().createInstance());

        rotatingModel1.setRotationAxis(Direction.Axis.Y)
                .setRotationalSpeed(getBlockEntitySpeed())
                .setRotationOffset(-getRotationOffset(Direction.Axis.Y))
                .setColor(blockEntity)
                .setPosition(getInstancePosition());
    }

    public void update() {
        this.updateRotation(this.rotatingModel1, Direction.Axis.Y);
    }

    public void updateLight() {
        this.relight(this.pos, new FlatLit[]{this.rotatingModel1});
    }

    public void remove() {
        this.rotatingModel1.delete();
    }

    protected Instancer<RotatingData> getHalfShaftModel() {
        return this.getRotatingMaterial().getModel(AllPartialModels.SHAFT_HALF, blockState, Direction.DOWN);
    }
}
