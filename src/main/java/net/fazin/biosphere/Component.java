package net.fazin.biosphere;

public abstract class Component {
    protected GameObject object;

    public void setObject(GameObject object) {
        this.object = object;
    }

    public void created() {

    }

    public void update(float dt) {

    }

    public void fixedUpdate() {

    }

    public void destroyed() {

    }
}
