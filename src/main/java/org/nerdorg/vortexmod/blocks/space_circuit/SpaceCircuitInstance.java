package org.nerdorg.vortexmod.blocks.space_circuit;

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

public class SpaceCircuitInstance extends KineticBlockEntityInstance<SpaceCircuitBlockEntity> {
    protected RotatingData rotatingModel1;

    public SpaceCircuitInstance(MaterialManager materialManager, SpaceCircuitBlockEntity blockEntity) {
        super(materialManager, blockEntity);
    }

    public void init() {
        this.rotatingModel1 = this.setup((RotatingData)this.getShaftModel().createInstance());

        rotatingModel1.setRotationAxis(axis)
                .setRotationalSpeed(getBlockEntitySpeed())
                .setRotationOffset(-getRotationOffset(axis))
                .setColor(blockEntity)
                .setPosition(getInstancePosition());
    }

    public void update() {
        this.updateRotation(this.rotatingModel1, axis);
    }

    public void updateLight() {
        this.relight(this.pos, new FlatLit[]{this.rotatingModel1});
    }

    public void remove() {
        this.rotatingModel1.delete();
    }

    protected BlockState getRenderedShaftBlockState() {
        return AllBlocks.SHAFT.getDefaultState().setValue(ShaftBlock.AXIS, axis);
    }

    protected Instancer<RotatingData> getShaftModel() {
        return this.getRotatingMaterial().getModel(this.getRenderedShaftBlockState());
    }
}
