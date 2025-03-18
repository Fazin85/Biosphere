package net.fazin.biosphere.engine;

import net.fazin.biosphere.graphics.DisplayList;
import net.fazin.biosphere.graphics.IRenderable;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;

public class CubeRenderer implements IRenderable {
    private final DisplayList displayList;

    public CubeRenderer() {
        float[] verts = new float[]{
                // Front face
                0.0f, 0.0f, 1.0f,
                1.0f, 0.0f, 1.0f,
                1.0f, 1.0f, 1.0f,
                0.0f, 1.0f, 1.0f,
                // Back face
                0.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                1.0f, 1.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                // Left face
                0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 1.0f,
                0.0f, 1.0f, 0.0f,
                // Right face
                1.0f, 0.0f, 0.0f,
                1.0f, 1.0f, 0.0f,
                1.0f, 1.0f, 1.0f,
                1.0f, 0.0f, 1.0f,
                // Top face
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 0.0f,
                // Bottom face
                0.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f
        };

        float[] cols = new float[]{
                // Front face (red)
                1.0f, 0.0f, 0.0f, 1.0f,
                1.0f, 0.0f, 0.0f, 1.0f,
                1.0f, 0.0f, 0.0f, 1.0f,
                1.0f, 0.0f, 0.0f, 1.0f,
                // Back face (green)
                0.0f, 1.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 0.0f, 1.0f,
                // Left face (blue)
                0.0f, 0.0f, 1.0f, 1.0f,
                0.0f, 0.0f, 1.0f, 1.0f,
                0.0f, 0.0f, 1.0f, 1.0f,
                0.0f, 0.0f, 1.0f, 1.0f,
                // Right face (yellow)
                1.0f, 1.0f, 0.0f, 1.0f,
                1.0f, 1.0f, 0.0f, 1.0f,
                1.0f, 1.0f, 0.0f, 1.0f,
                1.0f, 1.0f, 0.0f, 1.0f,
                // Top face (magenta)
                1.0f, 0.0f, 1.0f, 1.0f,
                1.0f, 0.0f, 1.0f, 1.0f,
                1.0f, 0.0f, 1.0f, 1.0f,
                1.0f, 0.0f, 1.0f, 1.0f,
                // Bottom face (cyan)
                0.0f, 1.0f, 1.0f, 1.0f,
                0.0f, 1.0f, 1.0f, 1.0f,
                0.0f, 1.0f, 1.0f, 1.0f,
                0.0f, 1.0f, 1.0f, 1.0f
        };

        FloatBuffer vertices = BufferUtils.createFloatBuffer(verts.length).put(verts).flip();
        FloatBuffer colors = BufferUtils.createFloatBuffer(cols.length).put(cols).flip();

        displayList = new DisplayList();
        displayList.begin();
        {
            glEnableClientState(GL_VERTEX_ARRAY);
            glEnableClientState(GL_COLOR_ARRAY);

            glVertexPointer(3, GL_FLOAT, 0, vertices);
            glColorPointer(4, GL_FLOAT, 0, colors);

            glDrawArrays(GL_QUADS, 0, 24);

            glDisableClientState(GL_VERTEX_ARRAY);
            glDisableClientState(GL_COLOR_ARRAY);
        }
        displayList.end();
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
