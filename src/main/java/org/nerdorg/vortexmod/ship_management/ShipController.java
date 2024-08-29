package org.nerdorg.vortexmod.ship_management;

import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.joml.*;
import org.joml.primitives.AABBdc;
import org.nerdorg.vortexmod.VortexMod;
import org.nerdorg.vortexmod.blocks.time_rotor.TimeRotorBlock;
import org.nerdorg.vortexmod.blocks.time_rotor.TimeRotorBlockEntity;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.ServerTickListener;
import org.valkyrienskies.core.api.ships.ShipForcesInducer;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;
import org.valkyrienskies.core.impl.game.ShipTeleportDataImpl;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;
import org.valkyrienskies.mod.api.SeatedControllingPlayer;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.lang.Math;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;

public class ShipController implements ShipForcesInducer, ServerTickListener {
    public ServerShip serverShip;
    public boolean aligning;
    public boolean disassembling;
    public boolean space_circuit;
    public boolean antigrav;
    public boolean stabilizer;
    public double cspeed;

    private float extraForceLinear = 0;
    private float extraForceAngular = 0;

    private float powerLinear = 0;
    private float powerAngular = 0;

    public List<TimeRotorBlockEntity> timeRotorBlocks = new ArrayList<>();

    private float GRAVITY = -10;

    private ControlData controlData;
    public Player seatedPlayer;

    private double oldSpeed;

    private float angleUntilAligned = 0;
    private Vector3dc positionUntilAligned = new Vector3d();

    public boolean canDisassemble() {
        return serverShip != null && disassembling && (abs(angleUntilAligned) < 0.0349066) && (positionUntilAligned.distanceSquared(this.serverShip.getTransform().getPositionInWorld()) < 4.0);
    }

    private static class ControlData {
        public final Direction seatInDirection;
        public float forwardImpulse;
        public float leftImpulse;
        public float upImpulse;
        public boolean sprintOn;

        public ControlData(Direction seatInDirection, float forwardImpulse, float leftImpulse, float upImpulse, boolean sprintOn) {
            this.seatInDirection = seatInDirection;
            this.forwardImpulse = forwardImpulse;
            this.leftImpulse = leftImpulse;
            this.upImpulse = upImpulse;
            this.sprintOn = sprintOn;
        }

        public static ControlData create(SeatedControllingPlayer player) {
            return new ControlData(
                    player.getSeatInDirection(),
                    player.getForwardImpulse(),
                    player.getLeftImpulse(),
                    player.getUpImpulse(),
                    player.getSprintOn()
            );
        }
    }


    @Override
    public void onServerTick() {
        extraForceLinear = powerLinear;
        powerLinear = 0;

        extraForceAngular = powerAngular;
        powerAngular = 0;
    }

    @Override
    public void applyForces(@NotNull PhysShip physShip) {
        PhysShipImpl physShipImpl = (PhysShipImpl) physShip;

        float mass = (float) physShipImpl.get_inertia().getShipMass();

        Matrix3dc moiTensor = physShipImpl.getInertia().getMomentOfInertiaTensor();

        Quaterniond invRotation = physShipImpl.getPoseVel().getRot().invert(new Quaterniond());
        AxisAngle4d invRotationAxisAngle = new AxisAngle4d(invRotation);

        // Floor makes a number 0 to 3, which corresponds to direction
        int alignTarget = (int) floor((invRotationAxisAngle.angle / (PI * 0.5)) + 4.5) % 4;
        angleUntilAligned = (float) ((alignTarget * (0.5 * PI)) - invRotationAxisAngle.angle);

        if (disassembling) {
            Vector3dc pos = this.serverShip.getTransform().getPositionInWorld();
            positionUntilAligned = pos.floor(new Vector3d());
            Vector3d direction = pos.sub(positionUntilAligned, new Vector3d());
            physShip.applyInvariantForce(direction);
        }

        if (aligning && abs(angleUntilAligned) > 0.0174533) {
            if (angleUntilAligned < 0.00872665 && angleUntilAligned > 0.0) {
                angleUntilAligned = 0.00872665F;
            }
            if (angleUntilAligned > -0.00872665 && angleUntilAligned < 0.0) {
                angleUntilAligned = (float) -0.00872665;
            }

            Vector3d idealOmega = new Vector3d(invRotationAxisAngle.x, invRotationAxisAngle.y, invRotationAxisAngle.z)
                    .mul(-angleUntilAligned)
                    .mul(100);

            Vector3d idealTorque = moiTensor.transform(idealOmega);

            physShip.applyInvariantTorque(idealTorque);
        }

        Vector3dc omega = physShipImpl.getPoseVel().getOmega();
        Vector3dc vel = physShipImpl.getPoseVel().getVel();

        if (stabilizer && !timeRotorBlocks.isEmpty()) {
            Stabilize.stabilize(
                    physShipImpl,
                    omega,
                    vel,
                    physShipImpl,
                    false,
                    true
            );
        }

        if (this.serverShip != null) {
            SeatedControllingPlayer controllingPlayer = this.serverShip.getAttachment(SeatedControllingPlayer.class);
            boolean validPlayer = controllingPlayer != null;

            Vector3dc idealUpwardVel = new Vector3d(0, 0, 0);

            if (validPlayer) {
                SeatedControllingPlayer player = controllingPlayer;
                controlData = ControlData.create(player);
            } else {
                controlData = null;
                oldSpeed = 0;
            }

            boolean shouldDoUpForce = false;

            if (controlData != null) {
                applyPlayerControl(controlData, physShipImpl);
                idealUpwardVel = getPlayerUpwardVel(controlData, mass);
                shouldDoUpForce = controlData.upImpulse != 0.0;
            }

            double idealUpwardForce = ((antigrav || shouldDoUpForce) && !timeRotorBlocks.isEmpty()) ? ((idealUpwardVel.y() - vel.y() - (GRAVITY)) * mass) : 0;

            physShip.applyInvariantForce(new Vector3d(0, max(idealUpwardForce, 0) + vel.y() * -mass, 0));

            // 39.33 m/s = 88 mph
            double exact_speed = physShipImpl.getPoseVel().getVel().length();
            this.cspeed = exact_speed;
            if (exact_speed >= 39.33 && this.space_circuit) {
                TardisInfo tardisInfo = this.serverShip.getAttachment(TardisInfo.class);
                if (tardisInfo != null) {
                    double distanceToTarget = this.serverShip.getTransform().getPositionInWorld().distance(tardisInfo.target_location.getX(), tardisInfo.target_location.getY(), tardisInfo.target_location.getZ());
                    if (distanceToTarget > 100) {
                        ShipTeleportDataImpl shipTeleportData = new ShipTeleportDataImpl(
                                (Vector3dc) new Vector3d(tardisInfo.target_location.getX(), tardisInfo.target_location.getY(), tardisInfo.target_location.getZ()),
                                JomlUtils.toQuaternion(tardisInfo.target_rotation),
                                this.serverShip.getVelocity(),
                                this.serverShip.getOmega(),
                                VSGameUtilsKt.getDimensionId(tardisInfo.target_level),
                                1.0
                        );

                        VSGameUtilsKt.getShipObjectWorld((ServerLevel) tardisInfo.current_level).teleportShip(serverShip, shipTeleportData);
                    }
                }
            }

            physShip.setStatic(false);
        }
        else {
            physShip.setStatic(true);
        }
    }

    private double smoothing(double smoothing, double max, double x) {
        return max - smoothing / (x + (smoothing / max));
    }


    private Vector3d getPlayerUpwardVel(ControlData control, double mass) {
        if (control.upImpulse != 0.0f) {
            double multiplier = (control.upImpulse < 0.0f)
                    ? 8
                    : 4
                    + smoothing(2.0, 5.5, 4);

            return new Vector3d(0.0, 1.0, 0.0).mul(control.upImpulse).mul(multiplier);
        }

        return new Vector3d(0.0, 0.0, 0.0);
    }


    private void applyPlayerControl(ControlData control, PhysShipImpl physShip) {
        ServerShip ship = this.serverShip; // Assuming 'ship' is a field in your class
        if (ship == null) {
            return;
        }

        ShipTransform transform = physShip.getTransform();
        AABBdc aabb = ship.getWorldAABB();
        Vector3dc center = transform.getPositionInWorld();

        // Player controlled rotation
        Matrix3dc moiTensor = physShip.getInertia().getMomentOfInertiaTensor();
        Vector3dc omega = physShip.getPoseVel().getOmega();

        // Calculate largest distance for turn speed penalty
        double dist = center.distance(aabb.minX(), center.y(), aabb.minZ());
        dist = max(dist, center.distance(aabb.minX(), center.y(), aabb.maxZ()));
        dist = max(dist, center.distance(aabb.maxX(), center.y(), aabb.minZ()));
        dist = max(dist, center.distance(aabb.maxX(), center.y(), aabb.maxZ()));

        double largestDistance = max(0.5, min(dist, 4));

        double maxLinearAcceleration = 50;
        double maxLinearSpeed = 100 + extraForceAngular;

        // acceleration = alpha * r, therefore: maxAlpha = maxAcceleration / r
        double maxOmegaY = maxLinearSpeed / largestDistance;
        double maxAlphaY = maxLinearAcceleration / largestDistance;

        boolean isBelowMaxTurnSpeed = abs(omega.y()) < maxOmegaY;

        double normalizedAlphaYMultiplier = (isBelowMaxTurnSpeed && control.leftImpulse != 0.0f)
                ? control.leftImpulse
                : -min(1.0, max(-1.0, omega.y()));

        double idealAlphaY = normalizedAlphaYMultiplier * maxAlphaY;

        physShip.applyInvariantTorque(moiTensor.transform(new Vector3d(0.0, idealAlphaY, 0.0)));

        physShip.applyInvariantTorque(getPlayerControlledBanking(control, physShip, moiTensor, -idealAlphaY));

        physShip.applyInvariantForce(getPlayerForwardVel(control, physShip));
    }

    private Vector3d getPlayerControlledBanking(ControlData control, PhysShipImpl physShip, Matrix3dc moiTensor, double strength) {
        Vec3i rotationVectorInt = control.seatInDirection.getNormal();
        Vector3d rotationVector = new Vector3d(rotationVectorInt.getX(), rotationVectorInt.getY(), rotationVectorInt.getZ());
        physShip.getPoseVel().transformDirection(rotationVector);
        rotationVector.y = 0;
        rotationVector.mul(strength * 0.1);

        rotationVector = physShip.getPoseVel().getRot().transform(
                moiTensor.transform(
                        physShip.getPoseVel().getRot().transformInverse(rotationVector)
                )
        );

        return rotationVector;
    }

    private double smoothingATan(double smoothing, double x) {
        return atan(x * smoothing) / smoothing;
    }

    // Limit x to max using ATan
    private double smoothingATanMax(double max, double x) {
        return smoothingATan(1 / (max * 0.638), x);
    }


    private Vector3d getPlayerForwardVel(ControlData control, PhysShipImpl physShip) {
        double linearMaxMass = 10000;
        double linearMassScaling = 2.0E-4;
        double linearBaseMass = 50;
        double baseSpeed = 0;
        double maxBaseSpeed = 60;
        double maxCasualSpeed = 100;

        List<TimeRotorBlockEntity> blocksToRemove = new ArrayList<>();

        for (TimeRotorBlockEntity timeRotorBlockEntity : timeRotorBlocks) {
            if (timeRotorBlockEntity.isRemoved()) {
                blocksToRemove.add(timeRotorBlockEntity);
            }
        }

        for (TimeRotorBlockEntity timeRotorBlockEntity : blocksToRemove) {
            timeRotorBlocks.remove(timeRotorBlockEntity);
        }

        for (TimeRotorBlockEntity timeRotorBlockEntity : timeRotorBlocks) {
            baseSpeed += 13.2 * (abs(timeRotorBlockEntity.getSpeed()) / 64);
        }

        baseSpeed = min(baseSpeed, maxBaseSpeed);

        double scaledMass = physShip.getInertia().getShipMass() * 5;
        Vector3dc vel = physShip.getPoseVel().getVel();

        // Player controlled forward and backward thrust
        Vec3i forwardVectorInt = control.seatInDirection.getNormal();
        Vector3d forwardVector = new Vector3d(forwardVectorInt.getX(), forwardVectorInt.getY(), forwardVectorInt.getZ());

        physShip.getPoseVel().getRot().transform(forwardVector);
        forwardVector.normalize();

        double s = 1 / smoothingATanMax(
                linearMaxMass,
                physShip.getInertia().getShipMass() * linearMassScaling + linearBaseMass
        );

        double maxSpeed = 50;
        oldSpeed = max(min(oldSpeed * (1 - s) + control.forwardImpulse * s, maxSpeed), -maxSpeed);
        forwardVector.mul(oldSpeed);

        Vector3d playerUpDirection = physShip.getPoseVel().transformDirection(new Vector3d(0.0, 1.0, 0.0));
        Vector3d velOrthogonalToPlayerUp = vel.sub(playerUpDirection.mul(playerUpDirection.dot(vel)), new Vector3d());

        // This is the speed that the ship is always allowed to go out, without engines
        Vector3d baseForwardVel = new Vector3d(forwardVector).mul(baseSpeed);
        Vector3d forwardForce = new Vector3d(baseForwardVel).sub(velOrthogonalToPlayerUp).mul(scaledMass);

        if (extraForceLinear != 0.0) {
            // Boost
            double boost = max((extraForceLinear - 500000 * 2.5) * 0.2, 0.0);
            extraForceLinear += (float) (boost + boost * boost * 1.0E-6);

            // This is the maximum speed we want to go in any scenario (when not sprinting)
            Vector3d idealForwardVel = new Vector3d(forwardVector).mul(maxCasualSpeed);
            Vector3d idealForwardForce = new Vector3d(idealForwardVel).sub(velOrthogonalToPlayerUp).mul(scaledMass);

            Vector3d extraForceNeeded = new Vector3d(idealForwardForce).sub(forwardForce);
            forwardForce.fma(min(extraForceLinear / extraForceNeeded.length(), 1.0), extraForceNeeded);
        }

        return forwardForce;
    }
}
