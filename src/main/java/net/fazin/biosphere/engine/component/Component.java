package net.fazin.biosphere.engine.component;

import net.fazin.biosphere.engine.GameObject;

public abstract class Component {
    protected GameObject object;

    public void setObject(GameObject object) {
        this.object = object;
    }

    public GameObject getObject() {
        return object;
    }

    public void created() {

    }

    public void start() {

    }

    public void update(float dt) {

    }

    public void fixedUpdate() {

    }

    public void destroyed() {

    }

    public void postDestroyed() {

    }
}
