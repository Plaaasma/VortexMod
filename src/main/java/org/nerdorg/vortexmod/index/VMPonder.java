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

        PonderRegistry.TAGS.forTag(AllPonderTags.KINETIC_APPLIANCES)
                .add(VMBlocks.TIME_ROTOR);
    }
}
