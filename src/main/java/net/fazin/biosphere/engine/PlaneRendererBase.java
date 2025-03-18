package net.fazin.biosphere.engine;

import net.fazin.biosphere.graphics.DisplayList;
import net.fazin.biosphere.graphics.IRenderable;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public class PlaneRendererBase implements IRenderable {
    protected final DisplayList displayList;
    protected float widthX;
    protected float widthZ;

    public PlaneRendererBase(float widthX, float widthZ) {
        this.widthX = widthX;
        this.widthZ = widthZ;

        displayList = new DisplayList();
    }

    protected FloatBuffer createVertexBuffer() {
        float[] vertices = new float[]{
                // First triangle
                -widthX, 0.0f, -widthZ,  // Bottom-left
                widthX, 0.0f, -widthZ,  // Bottom-right
                -widthX, 0.0f, widthZ,  // Top-left

                // Second triangle
                widthX, 0.0f, -widthZ,  // Bottom-right
                widthX, 0.0f, widthZ,  // Top-right
                -widthX, 0.0f, widthZ   // Top-left
        };

        return BufferUtils.createFloatBuffer(vertices.length).put(vertices).flip();
    }

    @Override
    public void render() {
        displayList.call();
    }

    @Override
    public void destroy() {
        displayList.delete();
    }

    @Override
    public boolean transparent() {
        return false;
    }
}
