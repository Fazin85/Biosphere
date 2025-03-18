package net.fazin.biosphere;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.Generic6DofConstraint;
import com.bulletphysics.linearmath.Transform;
import net.fazin.biosphere.engine.GameObject;
import net.fazin.biosphere.engine.Keyboard;
import net.fazin.biosphere.engine.Scene;
import net.fazin.biosphere.engine.SceneManager;
import net.fazin.biosphere.engine.component.Component;
import net.fazin.biosphere.engine.component.DiscreteDynamicsWorldComponent;
import net.fazin.biosphere.engine.component.RigidBodyComponent;
import net.fazin.biosphere.graphics.Display;
import org.joml.Vector2d;
import org.joml.Vector3f;

import java.util.Optional;
import java.util.logging.Logger;

import static org.lwjgl.glfw.GLFW.*;

public class FlyController extends Component {
    private static final Logger LOGGER = Logger.getLogger(FlyController.class.getName());
    private final GameObject cameraObject;
    private float yaw;
    private float pitch;
    private boolean handleMouseInput;

    public FlyController(GameObject cameraObject) {
        handleMouseInput = false;
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
        Optional<Scene> currentSceneOptional = SceneManager.getCurrentScene();
        if (currentSceneOptional.isEmpty()) {
            LOGGER.info("Failed to get current scene");
            return;
        }

        Scene currentScene = currentSceneOptional.get();
        DiscreteDynamicsWorldComponent physicsWorldComponent = currentScene.getComponent(DiscreteDynamicsWorldComponent.class);
        if (physicsWorldComponent == null) {
            LOGGER.severe("Failed to get physicsWorldComponent");
            return;
        }

        addPhysicsConstraint(physicsWorldComponent);

        RigidBodyComponent rigidBodyComponent = object.getComponent(RigidBodyComponent.class);
        rigidBodyComponent.getRigidBody().setActivationState(CollisionObject.DISABLE_DEACTIVATION);
    }

    private RigidBody createAnchorBody() {
        BoxShape anchorShape = new BoxShape(new javax.vecmath.Vector3f(0.1f, 0.1f, 0.1f));
        Transform anchorTransform = new Transform();
        anchorTransform.setIdentity();
        anchorTransform.origin.set(0.0f, 0.0f, 0.0f);
        RigidBodyConstructionInfo anchorInfo = new RigidBodyConstructionInfo(0.0f, null, anchorShape, new javax.vecmath.Vector3f(0.0f, 0.0f, 0.0f));

        return new RigidBody(anchorInfo);
    }

    private void addPhysicsConstraint(DiscreteDynamicsWorldComponent physicsWorldComponent) {
        RigidBody anchorBody = createAnchorBody();
        physicsWorldComponent.getWorld().addRigidBody(anchorBody);

        Transform anchorConstrainTransform = new Transform();
        Transform playerTransform = new Transform();
        anchorConstrainTransform.setIdentity();
        playerTransform.setIdentity();

        Generic6DofConstraint constraint = new Generic6DofConstraint(anchorBody, object.getComponent(RigidBodyComponent.class).getRigidBody(), anchorConstrainTransform, playerTransform, true);

        constraint.setAngularLowerLimit(new javax.vecmath.Vector3f(0.0f, 0.0f, 0.0f));
        constraint.setAngularUpperLimit(new javax.vecmath.Vector3f(0.0f, 0.0f, 0.0f));
        constraint.setLinearLowerLimit(new javax.vecmath.Vector3f(-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE));
        constraint.setLinearUpperLimit(new javax.vecmath.Vector3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE));

        physicsWorldComponent.getWorld().addConstraint(constraint);
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

        Vector3f velocity = new Vector3f();
        RigidBodyComponent rigidBodyComponent = object.getComponent(RigidBodyComponent.class);

        float speed = 5.0f;
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
        if (Keyboard.isKeyPressed(GLFW_KEY_SPACE)) {
            rigidBodyComponent.getRigidBody().applyCentralForce(new javax.vecmath.Vector3f(0.0f, 5f, 0.0f));
        }

        rigidBodyComponent.getRigidBody().setLinearVelocity(new javax.vecmath.Vector3f(velocity.x, velocity.y, velocity.z));
    }
}
