package net.fazin.biosphere.engine;

import org.lwjgl.BufferUtils;

import java.awt.*;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;

public class PlaneRendererColor extends PlaneRendererBase {
    public PlaneRendererColor(float widthX, float widthZ, Color color) {
        super(widthX, widthZ);

        FloatBuffer vertices = createVertexBuffer();
        FloatBuffer colors = createColorBuffer(color);

        displayList.begin();
        {
            glCullFace(GL_FRONT);
            glEnableClientState(GL_VERTEX_ARRAY);
            glEnableClientState(GL_COLOR_ARRAY);

            glVertexPointer(3, GL_FLOAT, 0, vertices);
            glColorPointer(4, GL_FLOAT, 0, colors);

            glDrawArrays(GL_TRIANGLES, 0, 6);

            glCullFace(GL_BACK);
            glDisableClientState(GL_VERTEX_ARRAY);
            glDisableClientState(GL_COLOR_ARRAY);
        }
        displayList.end();
    }

    private FloatBuffer createColorBuffer(Color color) {
        float[] vertexColors = new float[24];

        for (int i = 0; i < 6; i++) {
            vertexColors[i * 4] = color.getRed();
            vertexColors[(i * 4) + 1] = color.getGreen();
            vertexColors[(i * 4) + 2] = color.getBlue();
            vertexColors[(i * 4) + 3] = color.getAlpha();
        }

        return BufferUtils.createFloatBuffer(vertexColors.length).put(vertexColors).flip();
    }
}
