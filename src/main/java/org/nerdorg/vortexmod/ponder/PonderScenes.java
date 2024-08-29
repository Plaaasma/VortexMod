package org.nerdorg.vortexmod.ponder;

import com.simibubi.create.foundation.ponder.PonderPalette;
import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;

import com.simibubi.create.foundation.ponder.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeverBlock;
import org.nerdorg.vortexmod.blocks.space_circuit.SpaceCircuitBlock;
import org.nerdorg.vortexmod.blocks.time_rotor.TimeRotorBlock;
import org.nerdorg.vortexmod.index.VMBlocks;

public class PonderScenes {
    public static void time_rotor(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("time_rotor", "Powering the TARDIS with a time rotor!");
        scene.configureBasePlate(0, 0, 5);
        scene.world.showSection(util.select.layer(0), Direction.UP);

        BlockPos rotor1 = util.grid.at(2, 1, 2);
        BlockPos rotor2 = util.grid.at(2, 2, 2);
        BlockPos rotor3 = util.grid.at(2, 3, 2);

        for (int i = 0; i < 6; i++) {
            scene.idle(5);
            scene.world.showSection(util.select.position(i, 1, 2), Direction.DOWN);
            scene.world.showSection(util.select.position(i, 2, 2), Direction.DOWN);
        }

        scene.idle(10);
        scene.overlay.showText(50)
                .text("The Time Rotor is responsible for providing thrust and stabilizing the TARDIS.")
                .placeNearTarget()
                .pointAt(util.vector.topOf(rotor1));
        scene.idle(60);

        scene.overlay.showText(50)
                .text("It requires atleast 32 RPM to operate, though increasing the RPM makes it stronger.")
                .placeNearTarget()
                .pointAt(util.vector.topOf(rotor1));
        scene.idle(60);

        for (int i = 0; i < 6; i++) {
            scene.idle(5);
            scene.world.showSection(util.select.position(i, 3, 2), Direction.DOWN);
            scene.world.showSection(util.select.position(i, 4, 2), Direction.DOWN);
        }

        scene.overlay.showText(100)
                .text("You can have multiple of them in one TARDIS to increase the strength as well.")
                .placeNearTarget()
                .pointAt(util.vector.topOf(rotor2));
        scene.idle(60);
        scene.markAsFinished();
    }

    public static void flight_computer(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("flight_computer", "Managing the TARDIS with the Flight Computer!");
        scene.configureBasePlate(0, 0, 5);
        scene.world.showSection(util.select.layer(0), Direction.UP);

        BlockPos computer = util.grid.at(2, 1, 2);

        for (int i = 0; i < 6; i++) {
            scene.idle(5);
            scene.world.showSection(util.select.position(i, 1, 2), Direction.DOWN);
            scene.world.showSection(util.select.position(i, 2, 2), Direction.DOWN);
            scene.world.showSection(util.select.position(i, 3, 2), Direction.DOWN);
        }

        scene.idle(10);
        scene.overlay.showText(50)
                .text("The Flight Computer is essential for assembling and disassembling the TARDIS.")
                .placeNearTarget()
                .pointAt(util.vector.topOf(computer));
        scene.idle(60);

        scene.overlay.showText(50)
                .text("It displays and controls all the variables in the TARDIS such as the target location.")
                .placeNearTarget()
                .pointAt(util.vector.topOf(computer));
        scene.idle(60);

        scene.overlay.showText(50)
                .text("It requires atleast 32 RPM to operate.")
                .placeNearTarget()
                .pointAt(util.vector.topOf(computer));
        scene.idle(60);

        scene.markAsFinished();
    }

    public static void flight_controller(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("flight_controller", "Flying the TARDIS with the Flight Controller!");
        scene.configureBasePlate(0, 0, 5);
        scene.world.showSection(util.select.layer(0), Direction.UP);

        BlockPos controller = util.grid.at(2, 1, 2);

        for (int i = 0; i < 6; i++) {
            scene.idle(5);
            scene.world.showSection(util.select.position(i, 1, 2), Direction.DOWN);
            scene.world.showSection(util.select.position(i, 2, 2), Direction.DOWN);
        }

        scene.idle(10);
        scene.overlay.showText(50)
                .text("The Flight Controller is the actual control panel for the TARDIS.")
                .placeNearTarget()
                .pointAt(util.vector.topOf(controller));
        scene.idle(60);

        scene.overlay.showText(50)
                .text("Right clicking it will mount you onto the TARDIS where you can fly using your movement keys, your jump bind, and your descent bind (Different than crouch).")
                .placeNearTarget()
                .pointAt(util.vector.topOf(controller));
        scene.idle(120);

        scene.overlay.showText(50)
                .text("It requires atleast 32 RPM to operate.")
                .placeNearTarget()
                .pointAt(util.vector.topOf(controller));
        scene.idle(60);

        scene.markAsFinished();
    }

    public static void space_circuit(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("space_circuit", "How to fold the fabric of space!");
        scene.configureBasePlate(0, 0, 5);
        scene.world.showSection(util.select.layer(0), Direction.UP);

        BlockPos circuit = util.grid.at(2, 1, 2);
        BlockPos lever = util.grid.at(1, 1, 2);

        for (int i = 0; i < 6; i++) {
            scene.idle(5);
            scene.world.showSection(util.select.position(i, 1, 2), Direction.DOWN);
            scene.world.showSection(util.select.position(i, 2, 2), Direction.DOWN);
        }

        scene.idle(10);
        scene.overlay.showText(50)
                .text("The Space Circuit allows you to teleport to the target coords once you get 39.3 m/s (88 mph).")
                .placeNearTarget()
                .pointAt(util.vector.topOf(circuit));
        scene.idle(60);

        scene.world.cycleBlockProperty(lever, LeverBlock.POWERED);
        scene.world.cycleBlockProperty(circuit, SpaceCircuitBlock.POWERED);
        scene.overlay.showText(50)
                .text("You can toggle it on and off by powering it with a redstone signal.")
                .placeNearTarget()
                .pointAt(util.vector.topOf(circuit));
        scene.idle(60);

        scene.world.cycleBlockProperty(lever, LeverBlock.POWERED);
        scene.world.cycleBlockProperty(circuit, SpaceCircuitBlock.POWERED);
        scene.overlay.showText(50)
                .text("It requires atleast 32 RPM to operate.")
                .placeNearTarget()
                .pointAt(util.vector.topOf(circuit));
        scene.idle(60);

        scene.markAsFinished();
    }
}
