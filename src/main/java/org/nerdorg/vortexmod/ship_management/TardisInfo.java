package org.nerdorg.vortexmod.ship_management;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ShipForcesInducer;

public class TardisInfo implements ShipForcesInducer {
    public BlockPos target_location = new BlockPos(0, 0, 0);
    public Vector3d target_rotation = new Vector3d(0, 0, 0);
    public Level target_level;
    public Level current_level;

    public TardisInfo() {}

    public TardisInfo(BlockPos target_location, Vector3d target_rotation, Level target_level, Level current_level) {
        this.target_location = target_location;
        this.target_rotation = target_rotation;
        this.target_level = target_level;
        this.current_level = current_level;
    }

    @Override
    public void applyForces(@NotNull PhysShip physShip) {

    }
}
