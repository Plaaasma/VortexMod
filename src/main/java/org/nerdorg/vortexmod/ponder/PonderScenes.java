package org.nerdorg.vortexmod.ponder;

import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;

import com.simibubi.create.foundation.ponder.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.nerdorg.vortexmod.blocks.time_rotor.TimeRotorBlock;
import org.nerdorg.vortexmod.index.VMBlocks;

public class PonderScenes {
    public static void time_rotor(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("time_rotor", "Powering the TARDIS with a time rotor!");
        scene.configureBasePlate(0, 0, 5);
        scene.world.showSection(util.select.layer(0), Direction.UP);

        BlockPos rotor = util.grid.at(2, 1, 3);

        for (int i = 0; i < 6; i++) {
            scene.idle(5);
            scene.world.showSection(util.select.position(i, 1, 2), Direction.DOWN);
            scene.world.showSection(util.select.position(i, 2, 2), Direction.DOWN);
            scene.world.showSection(util.select.position(i, 3, 2), Direction.DOWN);
        }

        scene.idle(10);
        scene.overlay.showText(50)
                .text("The Time Rotor is responsible for providing lift and communicating with the time vortex")
                .placeNearTarget()
                .pointAt(util.vector.topOf(rotor));
        scene.idle(60);

        scene.overlay.showText(50)
                .text("It requires atleast 32 RPM to operate")
                .placeNearTarget()
                .pointAt(util.vector.topOf(rotor));
        scene.idle(60);

        scene.overlay.showText(50)
                .text("The Time Rotors strength is determined by the RPM input into the system. You can have multiple of them in one TARDIS to multiply the strength as well.")
                .placeNearTarget()
                .pointAt(util.vector.topOf(rotor));
        scene.idle(120);
        scene.markAsFinished();
    }
}
