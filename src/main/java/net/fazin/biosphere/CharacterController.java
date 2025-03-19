package net.fazin.biosphere;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.CollisionWorld;
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
import net.fazin.biosphere.engine.graphics.Display;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;
import org.joml.Vector3f;

import java.util.Optional;
import java.util.logging.Logger;

import static org.lwjgl.glfw.GLFW.*;

public class CharacterController extends Component {
    private static final Logger LOGGER = Logger.getLogger(CharacterController.class.getName());
    private final Transform tmpTransform;
    private final GameObject cameraObject;
    private RigidBody anchorBody;
    private RigidBodyComponent rigidBodyComponent;
    private DiscreteDynamicsWorldComponent physicsWorldComponent;
    private float yaw;
    private float pitch;
    private boolean handleMouseInput;
    private boolean isGrounded;

    public CharacterController(GameObject cameraObject) {
        tmpTransform = new Transform();
        handleMouseInput = false;
        this.cameraObject = cameraObject;
        isGrounded = false;
    }

    public void setHandleMouseInput(boolean handleMouseInput) {
        this.handleMouseInput = handleMouseInput;
    }

    public boolean handleMouseInput() {
        return handleMouseInput;
    }

    @Override
    public void start() {
        Optional<DiscreteDynamicsWorldComponent> physicsWorldComponentOptional = getDiscreteDynamicsWorldComponent();
        if (physicsWorldComponentOptional.isEmpty()) {
            return;
        }

        physicsWorldComponent = physicsWorldComponentOptional.get();

        Optional<RigidBodyComponent> rigidBodyComponentOptional = object.getComponent(RigidBodyComponent.class);
        if (rigidBodyComponentOptional.isEmpty()) {
            LOGGER.severe("Failed to get RigidBodyComponent");
            return;
        }

        rigidBodyComponent = rigidBodyComponentOptional.get();
        rigidBodyComponent.getRigidBody().setActivationState(CollisionObject.DISABLE_DEACTIVATION);

        addPhysicsConstraint(physicsWorldComponent);
    }

    @NotNull
    private RigidBody createAnchorBody() {
        BoxShape anchorShape = new BoxShape(new javax.vecmath.Vector3f(0.1f, 0.1f, 0.1f));
        RigidBodyConstructionInfo anchorInfo = new RigidBodyConstructionInfo(0.0f, null, anchorShape, new javax.vecmath.Vector3f(0.0f, 0.0f, 0.0f));

        return new RigidBody(anchorInfo);
    }

    private void addPhysicsConstraint(DiscreteDynamicsWorldComponent physicsWorldComponent) {
        anchorBody = createAnchorBody();
        physicsWorldComponent.getWorld().addRigidBody(anchorBody);

        Transform anchorConstrainTransform = new Transform();
        Transform playerTransform = new Transform();
        anchorConstrainTransform.setIdentity();
        playerTransform.setIdentity();

        Generic6DofConstraint constraint = getGeneric6DofConstraint(anchorBody, anchorConstrainTransform, playerTransform);

        physicsWorldComponent.getWorld().addConstraint(constraint);
    }

    @NotNull
    private Optional<DiscreteDynamicsWorldComponent> getDiscreteDynamicsWorldComponent() {
        Optional<Scene> currentSceneOptional = SceneManager.getCurrentScene();
        if (currentSceneOptional.isEmpty()) {
            LOGGER.info("Failed to get current scene");
            return Optional.empty();
        }

        Scene currentScene = currentSceneOptional.get();
        Optional<DiscreteDynamicsWorldComponent> physicsWorldComponentOptional = currentScene.getComponent(DiscreteDynamicsWorldComponent.class);
        if (physicsWorldComponentOptional.isEmpty()) {
            LOGGER.severe("Failed to get physicsWorldComponent");
        }

        return physicsWorldComponentOptional;
    }

    @NotNull
    private Generic6DofConstraint getGeneric6DofConstraint(RigidBody anchorBody, Transform anchorConstrainTransform, Transform playerTransform) {
        Generic6DofConstraint constraint = new Generic6DofConstraint(anchorBody, rigidBodyComponent.getRigidBody(), anchorConstrainTransform, playerTransform, true);

        constraint.setAngularLowerLimit(new javax.vecmath.Vector3f(0.0f, 0.0f, 0.0f));
        constraint.setAngularUpperLimit(new javax.vecmath.Vector3f(0.0f, 0.0f, 0.0f));
        constraint.setLinearLowerLimit(new javax.vecmath.Vector3f(-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE));
        constraint.setLinearUpperLimit(new javax.vecmath.Vector3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE));
        return constraint;
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

        groundRayCast(1f);

        Vector3f velocity = new Vector3f();

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
        if (Keyboard.isKeyPressed(GLFW_KEY_SPACE) && isGrounded) {

            //velocity.y += 50.0f;
        }

        rigidBodyComponent.getRigidBody().setLinearVelocity(new javax.vecmath.Vector3f(velocity.x, velocity.y, velocity.z));
    }

    private void groundRayCast(float rayCastDistance) {
        if (physicsWorldComponent == null) {
            return;
        }

        rigidBodyComponent.getRigidBody().getWorldTransform(tmpTransform);

        javax.vecmath.Vector3f from = tmpTransform.origin;
        javax.vecmath.Vector3f to = new javax.vecmath.Vector3f(from.x, from.y - rayCastDistance, from.z);

        CollisionWorld.ClosestRayResultCallback callback = new CollisionWorld.ClosestRayResultCallback(from, to);
        physicsWorldComponent.getWorld().rayTest(from, to, callback);

        isGrounded = callback.hasHit();
    }

    @Override
    public void destroyed() {
        if (physicsWorldComponent != null) {
            physicsWorldComponent.getWorld().removeRigidBody(anchorBody);
        }
    }
}
