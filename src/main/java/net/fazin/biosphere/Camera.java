package net.fazin.biosphere;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.*;

public class Camera extends Component {
    private final float fov;
    private final Matrix4f projectionMatrix;
    private final float[] tmp = new float[16];
    private boolean isActive;
    private Vector3f tmpPosition = new Vector3f();
    private Vector3f tmpRotation = new Vector3f();

    public Camera(float fov, Window window) {
        this.fov = fov;

        projectionMatrix = new Matrix4f().perspective((float) Math.toRadians(fov), (float) window.getWidth() / window.getHeight(), 0.1f, 1000.0f);
    }

    public float getFov() {
        return fov;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }


    public void prerender() {
        glLoadIdentity();

        glMatrixMode(GL_PROJECTION);
        glLoadMatrixf(projectionMatrix.get(tmp));

        tmpPosition.set(0.0f);
        tmpRotation.set(0.0f);
        getPositionRecursive(object, tmpPosition);
        getRotationRecursive(object, tmpRotation);

        glMatrixMode(GL_MODELVIEW);
        glRotatef(tmpRotation.x, 1.0f, 0.0f, 0.0f);
        glRotatef(tmpRotation.y, 0.0f, 1.0f, 0.0f);
        glRotatef(tmpRotation.z, 0.0f, 0.0f, 1.0f);

        glTranslatef(-tmpPosition.x, -tmpPosition.y, -tmpPosition.z);
    }

    private void getPositionRecursive(GameObject gameObject, Vector3f position) {
        GameObject parent = gameObject.getParent();
        position.add(gameObject.getTransform().position());

        if (parent != null) {
            getPositionRecursive(parent, position);
        }
    }

    private void getRotationRecursive(GameObject gameObject, Vector3f rotation) {
        GameObject parent = gameObject.getParent();
        rotation.add(gameObject.getTransform().rotation());

        if (parent != null) {
            getRotationRecursive(parent, rotation);
        }
    }
}
