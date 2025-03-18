package net.fazin.biosphere.engine;

import javax.vecmath.Matrix3f;

public class Util {
    private Util() {

    }

    public static Matrix3f eulerToMatrix(float pitch, float yaw, float roll) {
        Matrix3f rx = new Matrix3f(1, 0, 0,
                0, (float) Math.cos(pitch), (float) -Math.sin(pitch),
                0, (float) Math.sin(pitch), (float) Math.cos(pitch));

        Matrix3f ry = new Matrix3f((float) Math.cos(yaw), 0, (float) Math.sin(yaw),
                0, 1, 0,
                (float) -Math.sin(yaw), 0, (float) Math.cos(yaw));

        Matrix3f rz = new Matrix3f((float) Math.cos(roll), (float) -Math.sin(roll), 0,
                (float) Math.sin(roll), (float) Math.cos(roll), 0,
                0, 0, 1);

        Matrix3f result = new Matrix3f();
        result.setIdentity();
        result.mul(rz);
        result.mul(ry);
        result.mul(rx);
        return result;
    }
}
