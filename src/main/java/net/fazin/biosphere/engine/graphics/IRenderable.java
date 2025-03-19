package net.fazin.biosphere.engine.graphics;

public interface IRenderable {
    void render();
    void destroy();

    boolean transparent();
}
