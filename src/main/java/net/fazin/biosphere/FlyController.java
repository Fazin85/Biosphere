package net.fazin.biosphere;

import org.joml.Vector2d;

import static org.lwjgl.glfw.GLFW.*;

public class FlyController extends Component {
    private float yaw;
    private float pitch;
    private boolean handleMouseInput;

    public FlyController() {
        handleMouseInput = false;
    }

    public void setHandleMouseInput(boolean handleMouseInput) {
        this.handleMouseInput = handleMouseInput;
    }

    public boolean handleMouseInput() {
        return handleMouseInput;
    }

    @Override
    public void update(float dt) {
        handleMouseInput = Display.isFocused();

        if (handleMouseInput) {
            Vector2d cursorPosition = Display.getCursorPosition();

            yaw += (float) (cursorPosition.x - (Display.getWidth() / 2.0f)) * 0.1f; // Adjust sensitivity
            pitch += (float) (cursorPosition.y - (Display.getHeight() / 2.0f)) * 0.1f;

            Display.setCursorPosition(new Vector2d(Display.getWidth() / 2.0, Display.getHeight() / 2.0));

            pitch = Math.clamp(pitch, -90.0f, 90.0f);

            object.getTransform().rotation().x = pitch;
            object.getTransform().rotation().y = yaw;
        }

        float speed = 5.0f * dt;
        if (Keyboard.isKeyPressed(GLFW_KEY_W)) {
            object.getTransform().position().x += (float) (Math.sin(Math.toRadians(yaw)) * speed);
            object.getTransform().position().z -= (float) (Math.cos(Math.toRadians(yaw)) * speed);
        }
        if (Keyboard.isKeyPressed(GLFW_KEY_S)) {
            object.getTransform().position().x -= (float) (Math.sin(Math.toRadians(yaw)) * speed);
            object.getTransform().position().z += (float) (Math.cos(Math.toRadians(yaw)) * speed);
        }
        if (Keyboard.isKeyPressed(GLFW_KEY_A)) {
            object.getTransform().position().x -= (float) (Math.cos(Math.toRadians(yaw)) * speed);
            object.getTransform().position().z -= (float) (Math.sin(Math.toRadians(yaw)) * speed);
        }
        if (Keyboard.isKeyPressed(GLFW_KEY_D)) {
            object.getTransform().position().x += (float) (Math.cos(Math.toRadians(yaw)) * speed);
            object.getTransform().position().z += (float) (Math.sin(Math.toRadians(yaw)) * speed);
        }
        if (Keyboard.isKeyPressed(GLFW_KEY_SPACE)) {
            object.getTransform().position().y += speed;
        }
        if (Keyboard.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
            object.getTransform().position().y -= speed;
        }
    }
}
