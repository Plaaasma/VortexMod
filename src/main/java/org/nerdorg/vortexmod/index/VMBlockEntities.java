package org.nerdorg.vortexmod.index;

import com.simibubi.create.content.kinetics.base.ShaftInstance;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import org.nerdorg.vortexmod.VortexMod;
import org.nerdorg.vortexmod.blocks.flight_computer.FlightComputerBlockEntity;
import org.nerdorg.vortexmod.blocks.flight_computer.FlightComputerInstance;
import org.nerdorg.vortexmod.blocks.flight_computer.FlightComputerRenderer;
import org.nerdorg.vortexmod.blocks.flight_controller.FlightControllerBlockEntity;
import org.nerdorg.vortexmod.blocks.flight_controller.FlightControllerInstance;
import org.nerdorg.vortexmod.blocks.flight_controller.FlightControllerRenderer;
import org.nerdorg.vortexmod.blocks.space_circuit.SpaceCircuitBlockEntity;
import org.nerdorg.vortexmod.blocks.space_circuit.SpaceCircuitInstance;
import org.nerdorg.vortexmod.blocks.space_circuit.SpaceCircuitRenderer;
import org.nerdorg.vortexmod.blocks.time_rotor.TimeRotorBlockEntity;
import org.nerdorg.vortexmod.blocks.time_rotor.TimeRotorInstance;
import org.nerdorg.vortexmod.blocks.time_rotor.TimeRotorRenderer;

public class VMBlockEntities {
    public static final BlockEntityEntry<TimeRotorBlockEntity> TIME_ROTOR = VortexMod.REGISTRATE
            .blockEntity("time_rotor", TimeRotorBlockEntity::new)
            .instance(() -> TimeRotorInstance::new)
            .validBlocks(VMBlocks.TIME_ROTOR)
            .renderer(() -> TimeRotorRenderer::new)
            .register();

    public static final BlockEntityEntry<FlightComputerBlockEntity> FLIGHT_COMPUTER = VortexMod.REGISTRATE
            .blockEntity("flight_computer", FlightComputerBlockEntity::new)
            .instance(() -> FlightComputerInstance::new)
            .validBlocks(VMBlocks.FLIGHT_COMPUTER)
            .renderer(() -> FlightComputerRenderer::new)
            .register();

    public static final BlockEntityEntry<FlightControllerBlockEntity> FLIGHT_CONTROLLER = VortexMod.REGISTRATE
            .blockEntity("flight_controller", FlightControllerBlockEntity::new)
            .instance(() -> FlightControllerInstance::new)
            .validBlocks(VMBlocks.FLIGHT_CONTROLLER)
            .renderer(() -> FlightControllerRenderer::new)
            .register();

    public static final BlockEntityEntry<SpaceCircuitBlockEntity> SPACE_CIRCUIT = VortexMod.REGISTRATE
            .blockEntity("space_circuit", SpaceCircuitBlockEntity::new)
            .instance(() -> SpaceCircuitInstance::new)
            .validBlocks(VMBlocks.SPACE_CIRCUIT)
            .renderer(() -> SpaceCircuitRenderer::new)
            .register();

    public static void register() {}
}
