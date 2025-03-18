package net.fazin.biosphere.engine.component;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import net.fazin.biosphere.engine.Scene;
import net.fazin.biosphere.engine.SceneManager;

import javax.vecmath.Vector3f;
import java.util.Optional;
import java.util.logging.Logger;

public class RigidBodyComponent extends Component {
    private static final Logger LOGGER = Logger.getLogger(RigidBodyComponent.class.getName());
    private final CollisionShape shape;
    private final Transform tmp = new Transform();
    private final float mass;
    private DiscreteDynamicsWorldComponent physicsWorld;
    private RigidBody rigidBody;

    public RigidBodyComponent(CollisionShape shape, float mass) {
        this.shape = shape;
        this.mass = mass;
    }

    @Override
    public void created() {
        Optional<Scene> currentSceneOptional = SceneManager.getCurrentScene();
        if (currentSceneOptional.isEmpty()) {
            LOGGER.warning("Failed to get currentScene");
            return;
        }

        Scene currentScene = currentSceneOptional.get();
        DiscreteDynamicsWorldComponent physicsWorldComponent = currentScene.getComponent(DiscreteDynamicsWorldComponent.class);
        if (physicsWorldComponent == null) {
            LOGGER.severe("Failed to get DiscreteDynamicsWorldComponent from scene: " + currentScene.getName());
            return;
        }
        physicsWorld = physicsWorldComponent;

        Vector3f inertia = new Vector3f(0.0f, 0.0f, 0.0f);
        if (mass > 0.0f) {
            shape.calculateLocalInertia(mass, inertia);
        }

        Transform transform = net.fazin.biosphere.engine.Transform.toBulletTransform(object.getTransform());

        DefaultMotionState motionState = new DefaultMotionState(transform);
        RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass, motionState, shape, inertia);

        rigidBody = new RigidBody(rbInfo);
        physicsWorldComponent.addRigidBody(this);
    }

    @Override
    public void update(float dt) {
        if (physicsWorld == null) {
            LOGGER.warning("Tried to update RigidBody without a physicsWorld");
            return;
        }

        rigidBody.getMotionState().getWorldTransform(tmp);

        net.fazin.biosphere.engine.Transform engineTransform = net.fazin.biosphere.engine.Transform.toTransform(tmp);
        object.getTransform().position().set(engineTransform.position());
        object.getTransform().rotation().set(engineTransform.rotation());
        object.getTransform().scale().set(engineTransform.scale());
    }

    public RigidBody getRigidBody() {
        return rigidBody;
    }

    @Override
    public void destroyed() {
        if (physicsWorld != null) {
            physicsWorld.destroyRigidBody(this);
        }
    }
}
