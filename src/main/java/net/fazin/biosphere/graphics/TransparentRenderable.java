package net.fazin.biosphere.graphics;

import org.joml.Vector3f;

public record TransparentRenderable(Vector3f position) implements IRenderable {
    public TransparentRenderable(Vector3f position) {
        this.position = new Vector3f(position);
    }

    @Override
    public void render() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public boolean transparent() {
        return true;
    }
}
