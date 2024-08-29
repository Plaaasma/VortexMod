package org.nerdorg.vortexmod.index;

import com.simibubi.create.AllTags;
import com.simibubi.create.content.kinetics.BlockStressDefaults;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import org.nerdorg.vortexmod.VortexMod;
import org.nerdorg.vortexmod.blocks.flight_computer.FlightComputerBlock;
import org.nerdorg.vortexmod.blocks.flight_controller.FlightControllerBlock;
import org.nerdorg.vortexmod.blocks.space_circuit.SpaceCircuitBlock;
import org.nerdorg.vortexmod.blocks.time_rotor.TimeRotorBlock;

import static com.simibubi.create.foundation.data.ModelGen.customItemModel;

public class VMBlocks {

    static {
        VortexMod.REGISTRATE.setCreativeTab(VMCreativeModeTabs.MAIN_TAB);
    }

    public static final BlockEntry<TimeRotorBlock> TIME_ROTOR = VortexMod.REGISTRATE.block("time_rotor", TimeRotorBlock::new)
            .initialProperties(SharedProperties::netheriteMetal)
            .transform(BlockStressDefaults.setImpact(1f))
            .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<FlightComputerBlock> FLIGHT_COMPUTER = VortexMod.REGISTRATE.block("flight_computer", FlightComputerBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .transform(BlockStressDefaults.setImpact(0.25f))
            .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<FlightControllerBlock> FLIGHT_CONTROLLER = VortexMod.REGISTRATE.block("flight_controller", FlightControllerBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .transform(BlockStressDefaults.setImpact(0.5f))
            .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<SpaceCircuitBlock> SPACE_CIRCUIT = VortexMod.REGISTRATE.block("space_circuit", SpaceCircuitBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .transform(BlockStressDefaults.setImpact(2f))
            .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
            .item()
            .transform(customItemModel())
            .register();

    public static void register() {

    }
}
