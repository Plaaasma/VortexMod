package org.nerdorg.vortexmod.index;

import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;
import com.simibubi.create.foundation.ponder.PonderRegistry;
import com.simibubi.create.foundation.ponder.PonderTag;
import com.simibubi.create.infrastructure.ponder.AllPonderTags;
import net.minecraft.resources.ResourceLocation;
import org.nerdorg.vortexmod.VortexMod;
import org.nerdorg.vortexmod.ponder.PonderScenes;

public class VMPonder {
    static final PonderRegistrationHelper HELPER = new PonderRegistrationHelper(VortexMod.MODID);

    public static void register() {
        HELPER.addStoryBoard(VMBlocks.TIME_ROTOR, "time_rotor", PonderScenes::time_rotor);
        HELPER.addStoryBoard(VMBlocks.FLIGHT_COMPUTER, "flight_computer", PonderScenes::flight_computer);
        HELPER.addStoryBoard(VMBlocks.FLIGHT_CONTROLLER, "flight_controller", PonderScenes::flight_controller);
        HELPER.addStoryBoard(VMBlocks.SPACE_CIRCUIT, "space_circuit", PonderScenes::space_circuit);

        PonderRegistry.TAGS.forTag(AllPonderTags.KINETIC_APPLIANCES)
                .add(VMBlocks.TIME_ROTOR)
                .add(VMBlocks.FLIGHT_COMPUTER)
                .add(VMBlocks.FLIGHT_CONTROLLER)
                .add(VMBlocks.SPACE_CIRCUIT);
    }
}
