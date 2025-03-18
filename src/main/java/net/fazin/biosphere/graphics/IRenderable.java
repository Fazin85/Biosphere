package net.fazin.biosphere.graphics;

public interface IRenderable {
    void render();
    void destroy();

    boolean transparent();
}
