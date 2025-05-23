package net.fazin.biosphere.engine.component;

import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import org.joml.Vector3f;

public class DiscreteDynamicsWorldComponent extends Component {
    private final DiscreteDynamicsWorld world;

    public DiscreteDynamicsWorldComponent(Vector3f gravity) {
        DefaultCollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();

        world = new DiscreteDynamicsWorld(new CustomCollisionDispatcher(collisionConfiguration), new DbvtBroadphase(), new SequentialImpulseConstraintSolver(), collisionConfiguration);

        javax.vecmath.Vector3f g = new javax.vecmath.Vector3f(gravity.x, gravity.y, gravity.z);
        world.setGravity(g);
    }

    public void addRigidBody(RigidBodyComponent rigidBodyComponent) {
        world.addRigidBody(rigidBodyComponent.getRigidBody());
    }

    public void destroyRigidBody(RigidBodyComponent rigidBodyComponent) {
        world.removeRigidBody(rigidBodyComponent.getRigidBody());
    }

    public DiscreteDynamicsWorld getWorld() {
        return world;
    }

    @Override
    public void fixedUpdate() {
        world.stepSimulation(1.0f / 60.0f);
    }

    //we have to use postDestroyed or the world could be destroyed before removing all RigidBody's
    @Override
    public void postDestroyed() {
        world.destroy();
    }
}
