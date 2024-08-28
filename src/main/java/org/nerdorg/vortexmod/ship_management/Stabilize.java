package org.nerdorg.vortexmod.ship_management;

import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;

public final class Stabilize {
    public static void stabilize(@NotNull PhysShipImpl ship, @NotNull Vector3dc omega, @NotNull Vector3dc vel, @NotNull PhysShip forces, boolean linear, boolean yaw) {
        Intrinsics.checkNotNullParameter(ship, "ship");
        Intrinsics.checkNotNullParameter(omega, "omega");
        Intrinsics.checkNotNullParameter(vel, "vel");
        Intrinsics.checkNotNullParameter(forces, "forces");
        Vector3d shipUp = new Vector3d(0.0, 1.0, 0.0);
        Vector3d worldUp = new Vector3d(0.0, 1.0, 0.0);
        ship.getPoseVel().getRot().transform(shipUp);
        double angleBetween = shipUp.angle(worldUp);
        Vector3d idealAngularAcceleration = new Vector3d();
        Vector3d stabilizationTorque;
        if (angleBetween > 0.01) {
            stabilizationTorque = shipUp.cross(worldUp, new Vector3d()).normalize();
            idealAngularAcceleration.add(stabilizationTorque.mul(angleBetween, stabilizationTorque));
        }

        idealAngularAcceleration.sub(omega.x(), !yaw ? 0.0 : omega.y(), omega.z());
        stabilizationTorque = ship.getPoseVel().getRot().transform(ship.getInertia().getMomentOfInertiaTensor().transform(ship.getPoseVel().getRot().transformInverse(idealAngularAcceleration)));
        stabilizationTorque.mul(15.0);
        Intrinsics.checkNotNull(stabilizationTorque);
        forces.applyInvariantTorque(stabilizationTorque);
        if (linear) {
            Vector3d idealVelocity = (new Vector3d(vel)).negate();
            idealVelocity.y = 0.0;
            double s = ((double) 1 - (double) 1 / smoothingATanMax(10000.0, ship.getInertia().getShipMass() * 2.0E-4 + 1.0)) / 10.0;
            if (idealVelocity.lengthSquared() > s * s) {
                idealVelocity.normalize(s);
            }

            idealVelocity.mul(ship.getInertia().getShipMass() * 9.2);
            Intrinsics.checkNotNull(idealVelocity);
            forces.applyInvariantForce(idealVelocity);
        }

    }

    private static double smoothingATan(double smoothing, double x) {
        return Math.atan(x * smoothing) / smoothing;
    }

    private static double smoothingATanMax(double max, double x) {
        return smoothingATan((double) 1 / (max * 0.638), x);
    }
}
