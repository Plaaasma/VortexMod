package org.nerdorg.vortexmod.ship_management;

import net.minecraft.world.level.ChunkPos;
import org.joml.Quaterniond;
import org.joml.Vector2i;
import org.joml.Vector3d;

public class JomlUtils {
    public static Vector2i toJOML(ChunkPos chunkPos) {
        return new Vector2i(chunkPos.x, chunkPos.z);
    }

    private static double degreesToRadians(double degrees) {
        return Math.toRadians(degrees);
    }

    private static double radiansToDegrees(double radians) {
        return Math.toDegrees(radians);
    }

    public static Vector3d toEulerAngles(Quaterniond q) {
        // Normalize the quaternion to ensure it is unit-length
        q = q.normalize();

        // Extract the components of the quaternion
        double w = q.w;
        double x = q.x;
        double y = q.y;
        double z = q.z;

        // Compute the Euler angles
        double roll, pitch, yaw;

        // Roll (x-axis rotation)
        double sinr_cosp = 2 * (w * x + y * z);
        double cosr_cosp = 1 - 2 * (x * x + y * y);
        roll = Math.atan2(sinr_cosp, cosr_cosp);

        // Pitch (y-axis rotation)
        double sinp = 2 * (w * y - z * x);
        if (Math.abs(sinp) >= 1)
            pitch = Math.copySign(Math.PI / 2, sinp); // Use 90 degrees if out of range
        else
            pitch = Math.asin(sinp);

        // Yaw (z-axis rotation)
        double siny_cosp = 2 * (w * z + x * y);
        double cosy_cosp = 1 - 2 * (y * y + z * z);
        yaw = Math.atan2(siny_cosp, cosy_cosp);

        // Convert the angles from radians to degrees
        roll = radiansToDegrees(roll);
        pitch = radiansToDegrees(pitch);
        yaw = radiansToDegrees(yaw);

        // Return the Euler angles as a Vector3d
        return new Vector3d(roll, pitch, yaw);
    }

    public static Quaterniond toQuaternion(Vector3d rotation) {
        // Convert rotation angles from degrees to radians
        double xRad = degreesToRadians(rotation.x);
        double yRad = degreesToRadians(rotation.y);
        double zRad = degreesToRadians(rotation.z);

        // Calculate half angles
        double cx = Math.cos(xRad / 2);
        double sx = Math.sin(xRad / 2);
        double cy = Math.cos(yRad / 2);
        double sy = Math.sin(yRad / 2);
        double cz = Math.cos(zRad / 2);
        double sz = Math.sin(zRad / 2);

        // Calculate quaternion components
        double w = cx * cy * cz + sx * sy * sz;
        double x = sx * cy * cz - cx * sy * sz;
        double y = cx * sy * cz + sx * cy * sz;
        double z = cx * cy * sz - sx * sy * cz;

        // Return the quaternion (normalized by default in JOML)
        return new Quaterniond(x, y, z, w).normalize();
    }
}
