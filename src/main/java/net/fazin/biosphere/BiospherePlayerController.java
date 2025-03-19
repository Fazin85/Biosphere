package net.fazin.biosphere;

import net.fazin.biosphere.engine.GameObject;
import net.fazin.biosphere.engine.Keyboard;
import net.fazin.biosphere.engine.component.Component;
import net.fazin.biosphere.engine.graphics.Display;
import org.joml.Vector2d;

import javax.vecmath.Vector3f;
import java.util.Optional;
import java.util.logging.Logger;

import static org.lwjgl.glfw.GLFW.*;

public class BiospherePlayerController extends Component {
    private static final Logger LOGGER = Logger.getLogger(BiospherePlayerController.class.getName());
    private float jumpDelay;
    private GameObject cameraObject;
    private CharacterController characterController;
    private Vector3f velocity;
    private boolean handleMouseInput;
    private float yaw;
    private float pitch;

    public BiospherePlayerController(GameObject cameraObject) {
        velocity = new Vector3f();
        handleMouseInput = false;
        jumpDelay = 0.0f;
        this.cameraObject = cameraObject;
    }

    public void setHandleMouseInput(boolean handleMouseInput) {
        this.handleMouseInput = handleMouseInput;
    }

    public boolean handleMouseInput() {
        return handleMouseInput;
    }

    @Override
    public void start() {
        Optional<CharacterController> characterControllerOptional = object.getComponent(CharacterController.class);
        if (characterControllerOptional.isEmpty()) {
            LOGGER.severe("Failed to get CharacterController component");
            return;
        }

        characterController = characterControllerOptional.get();
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

            cameraObject.getTransform().rotation().x = pitch;
            cameraObject.getTransform().rotation().y = yaw;
        }
    }

    @Override
    public void fixedUpdate() {
        jumpDelay += 1.0f;

        float speed = 1.5f / 60.0f;
        if (Keyboard.isKeyPressed(GLFW_KEY_W)) {
            velocity.x += (float) (Math.sin(Math.toRadians(yaw)) * speed);
            velocity.z -= (float) (Math.cos(Math.toRadians(yaw)) * speed);
        }
        if (Keyboard.isKeyPressed(GLFW_KEY_S)) {
            velocity.x -= (float) (Math.sin(Math.toRadians(yaw)) * speed);
            velocity.z += (float) (Math.cos(Math.toRadians(yaw)) * speed);
        }
        if (Keyboard.isKeyPressed(GLFW_KEY_A)) {
            velocity.x -= (float) (Math.cos(Math.toRadians(yaw)) * speed);
            velocity.z -= (float) (Math.sin(Math.toRadians(yaw)) * speed);
        }
        if (Keyboard.isKeyPressed(GLFW_KEY_D)) {
            velocity.x += (float) (Math.cos(Math.toRadians(yaw)) * speed);
            velocity.z += (float) (Math.sin(Math.toRadians(yaw)) * speed);
        }
        if (Keyboard.isKeyPressed(GLFW_KEY_SPACE) && characterController.isGrounded() && jumpDelay > 10.0f) {
            velocity.y += 0.03f * 30f;
            jumpDelay = 0.0f;
        }

        characterController.setAcceleration(velocity.x, velocity.y, velocity.z);
        velocity.set(0.0f, 0.0f, 0.0f);
    }
}
