package net.fazin.biosphere.engine.component;

import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;

public class CustomCollisionDispatcher extends CollisionDispatcher {
    public CustomCollisionDispatcher(DefaultCollisionConfiguration configuration) {
        super(configuration);
    }

    @Override
    public boolean needsCollision(CollisionObject body0, CollisionObject body1) {
        if (body0.isKinematicObject() || body1.isKinematicObject()) {
            return true;
        }
        return super.needsCollision(body0, body1);
    }
}
