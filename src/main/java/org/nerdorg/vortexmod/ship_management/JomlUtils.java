package org.nerdorg.vortexmod.ship_management;

import net.minecraft.world.level.ChunkPos;
import org.joml.Vector2i;

public class JomlUtils {
    public static Vector2i toJOML(ChunkPos chunkPos) {
        return new Vector2i(chunkPos.x, chunkPos.z);
    }
}
