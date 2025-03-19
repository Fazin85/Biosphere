package net.fazin.biosphere;

import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.dispatch.CollisionWorld;
import com.bulletphysics.collision.dispatch.PairCachingGhostObject;
import com.bulletphysics.collision.narrowphase.ManifoldPoint;
import com.bulletphysics.collision.narrowphase.PersistentManifold;
import com.bulletphysics.collision.shapes.CapsuleShape;
import com.bulletphysics.linearmath.Transform;
import net.fazin.biosphere.engine.GameObject;
import net.fazin.biosphere.engine.Scene;
import net.fazin.biosphere.engine.SceneManager;
import net.fazin.biosphere.engine.component.Component;
import net.fazin.biosphere.engine.component.DiscreteDynamicsWorldComponent;
import org.jetbrains.annotations.NotNull;

import javax.vecmath.Vector3f;
import java.util.Optional;
import java.util.logging.Logger;

public class CharacterController extends Component {
    private static final Logger LOGGER = Logger.getLogger(CharacterController.class.getName());
    private final Transform tmpTransform;
    private final GameObject cameraObject;
    private DiscreteDynamicsWorldComponent physicsWorldComponent;
    private PairCachingGhostObject ghostObject;
    private Vector3f velocity;
    private Vector3f acceleration;
    private boolean isGrounded;

    public CharacterController(GameObject cameraObject) {
        tmpTransform = new Transform();
        this.cameraObject = cameraObject;
        isGrounded = false;
        velocity = new Vector3f();
        acceleration = new Vector3f();
    }

    @Override
    public void start() {
        Optional<DiscreteDynamicsWorldComponent> physicsWorldComponentOptional = getDiscreteDynamicsWorldComponent();
        if (physicsWorldComponentOptional.isEmpty()) {
            return;
        }

        physicsWorldComponent = physicsWorldComponentOptional.get();

        ghostObject = new PairCachingGhostObject();
        ghostObject.setCollisionShape(new CapsuleShape(0.5f, 1.0f));
        ghostObject.setCollisionFlags(ghostObject.getCollisionFlags() | CollisionFlags.KINEMATIC_OBJECT);
        physicsWorldComponent.getWorld().addCollisionObject(ghostObject);
        Transform transform = net.fazin.biosphere.engine.Transform.toBulletTransform(object.getTransform());
        ghostObject.setWorldTransform(transform);
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

    public void setAcceleration(float x, float y, float z) {
        acceleration.set(x, y, z);
    }

    public boolean isGrounded() {
        return isGrounded;
    }

    @Override
    public void fixedUpdate() {
        groundRayCast(1.0f);

        velocity.add(acceleration);

        velocity.sub(new javax.vecmath.Vector3f(0.0f, 0.05f, 0.0f));

        acceleration.set(0.0f, 0.0f, 0.0f);

        velocity.scale(0.8f);

        Transform transform = new Transform();
        transform.setIdentity();
        ghostObject.getWorldTransform(transform);
        javax.vecmath.Vector3f velocity2 = new javax.vecmath.Vector3f(velocity.x, velocity.y, velocity.z);

        calculateSlidingMovement(velocity2);
        transform.origin.add(velocity2);

        ghostObject.setWorldTransform(transform);
        object.getTransform().position().set(transform.origin.x, transform.origin.y, transform.origin.z);
    }

    private void groundRayCast(float rayCastDistance) {
        if (physicsWorldComponent == null) {
            return;
        }

        ghostObject.getWorldTransform(tmpTransform);

        javax.vecmath.Vector3f from = tmpTransform.origin;
        javax.vecmath.Vector3f to = new javax.vecmath.Vector3f(from.x, from.y - rayCastDistance, from.z);

        CollisionWorld.ClosestRayResultCallback callback = new CollisionWorld.ClosestRayResultCallback(from, to);
        physicsWorldComponent.getWorld().rayTest(from, to, callback);

        isGrounded = callback.hasHit();
    }

    //desiredMovement should be scaled by dt
    private void calculateSlidingMovement(javax.vecmath.Vector3f desiredMovement) {
        for (int i = 0; i < physicsWorldComponent.getWorld().getDispatcher().getNumManifolds(); i++) {
            PersistentManifold manifold = physicsWorldComponent.getWorld().getDispatcher().getManifoldByIndexInternal(i);

            if (manifold.getBody0() == ghostObject || manifold.getBody1() == ghostObject) {
                for (int j = 0; j < manifold.getNumContacts(); j++) {
                    ManifoldPoint contactPoint = manifold.getContactPoint(j);

                    if (contactPoint.getDistance() < 0.0f) {
                        javax.vecmath.Vector3f collisionNormal = new javax.vecmath.Vector3f(contactPoint.normalWorldOnB);
                        if (manifold.getBody1() == ghostObject) {
                            collisionNormal.negate();
                        }

                        float dotProduct = desiredMovement.dot(collisionNormal);
                        if (dotProduct < 0.0f) {
                            javax.vecmath.Vector3f projection = new javax.vecmath.Vector3f(collisionNormal);
                            projection.scale(dotProduct);
                            desiredMovement.sub(projection);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void destroyed() {
        if (physicsWorldComponent != null) {
            physicsWorldComponent.getWorld().removeCollisionObject(ghostObject);
        }
    }
}
