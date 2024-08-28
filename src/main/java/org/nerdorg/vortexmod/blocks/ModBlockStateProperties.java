package org.nerdorg.vortexmod.blocks;

import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class ModBlockStateProperties {
    public static final BooleanProperty ENABLED = (BooleanProperty) BooleanProperty.create("enabled").value(false).property();
}
