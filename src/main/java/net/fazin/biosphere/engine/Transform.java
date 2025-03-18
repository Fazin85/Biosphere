package net.fazin.biosphere.engine;

import org.joml.Vector3f;

import javax.vecmath.Matrix3f;

public record Transform(Vector3f position, Vector3f rotation, Vector3f scale) {
    public static com.bulletphysics.linearmath.Transform toBulletTransform(Transform transform) {
        com.bulletphysics.linearmath.Transform t = new com.bulletphysics.linearmath.Transform();
        t.setIdentity();
        t.origin.set(transform.position.x, transform.position.y, transform.position.z);

        Matrix3f rotationMatrix = eulerToMatrix(transform.rotation);
        t.basis.set(rotationMatrix);

        return t;
    }

    private static Matrix3f eulerToMatrix(Vector3f euler) {
        float pitch = euler.x; // Rotation around X
        float yaw = euler.y;   // Rotation around Y
        float roll = euler.z;  // Rotation around Z

        // Compute rotation matrices for each axis
        Matrix3f rx = new Matrix3f(1, 0, 0,
                0, (float) Math.cos(pitch), (float) -Math.sin(pitch),
                0, (float) Math.sin(pitch), (float) Math.cos(pitch));

        Matrix3f ry = new Matrix3f((float) Math.cos(yaw), 0, (float) Math.sin(yaw),
                0, 1, 0,
                (float) -Math.sin(yaw), 0, (float) Math.cos(yaw));

        Matrix3f rz = new Matrix3f((float) Math.cos(roll), (float) -Math.sin(roll), 0,
                (float) Math.sin(roll), (float) Math.cos(roll), 0,
                0, 0, 1);

        // Combine rotations: R = Rz * Ry * Rx
        Matrix3f result = new Matrix3f();
        result.setIdentity();
        result.mul(rz);
        result.mul(ry);
        result.mul(rx);

        return result;
    }

    public static Transform toTransform(com.bulletphysics.linearmath.Transform bulletTransform) {
        Vector3f position = new Vector3f(bulletTransform.origin.x, bulletTransform.origin.y, bulletTransform.origin.z);
        Vector3f rotation = matrixToEuler(bulletTransform.basis);
        Vector3f scale = new Vector3f(1f, 1f, 1f);

        return new Transform(position, rotation, scale);
    }

    private static Vector3f matrixToEuler(javax.vecmath.Matrix3f matrix) {
        Vector3f euler = new Vector3f();
        float sy = (float) Math.sqrt(matrix.m00 * matrix.m00 + matrix.m10 * matrix.m10);

        boolean singular = sy < 1e-6;

        if (!singular) {
            euler.x = (float) Math.atan2(matrix.m21, matrix.m22);  // Pitch
            euler.y = (float) Math.atan2(-matrix.m20, sy);        // Yaw
            euler.z = (float) Math.atan2(matrix.m10, matrix.m00); // Roll
        } else {
            euler.x = (float) Math.atan2(-matrix.m12, matrix.m11); // Pitch
            euler.y = (float) Math.atan2(-matrix.m20, sy);        // Yaw
            euler.z = 0;                                          // Roll
        }

        return euler;
    }
}
