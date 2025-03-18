package net.fazin.biosphere.engine;

import net.fazin.biosphere.graphics.DisplayList;
import net.fazin.biosphere.graphics.IRenderable;
import net.fazin.biosphere.graphics.TextureAtlas;
import net.fazin.biosphere.graphics.TextureAtlasUVs;
import org.joml.Vector2i;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;

public class HeightmapRenderer implements IRenderable {
    private final float yScale;
    private final int width;
    private DisplayList displayList;

    public HeightmapRenderer(float yScale, int width) {
        this.yScale = yScale;
        this.width = width;
    }

    private void calculateVertices(float[][] heightmap, Vector2i[][] textureIndices, TextureAtlas textureAtlas) {
        int numVertices = (width - 1) * (width - 1) * 6; // 6 vertices per cell (2 triangles)
        float[] vertices = new float[numVertices * 3];    // 3 floats per vertex (x, y, z)
        float[] texCoords = new float[numVertices * 2];

        int index = 0;
        int texIndex = 0;

        for (int x = 0; x < width - 1; x++) {
            for (int y = 0; y < width - 1; y++) {
                Vector2i textureIndex = textureIndices[x][y];
                TextureAtlasUVs uvs = textureAtlas.getTextureCoordinates(textureIndex.x, textureIndex.y);

                vertices[index++] = x;
                vertices[index++] = heightmap[x][y] * yScale;
                vertices[index++] = y;
                texCoords[texIndex++] = uvs.uStart();
                texCoords[texIndex++] = uvs.vEnd();

                // Bottom-right
                vertices[index++] = x + 1.0f;
                vertices[index++] = heightmap[x + 1][y] * yScale;
                vertices[index++] = y;
                texCoords[texIndex++] = uvs.uEnd();
                texCoords[texIndex++] = uvs.vEnd();

                // Top-left
                vertices[index++] = x;
                vertices[index++] = heightmap[x][y + 1] * yScale;
                vertices[index++] = y + 1.0f;
                texCoords[texIndex++] = uvs.uStart();
                texCoords[texIndex++] = uvs.vStart();

                // Bottom-right
                vertices[index++] = x + 1.0f;
                vertices[index++] = heightmap[x + 1][y] * yScale;
                vertices[index++] = y;
                texCoords[texIndex++] = uvs.uEnd();
                texCoords[texIndex++] = uvs.vEnd();

                // Top-right
                vertices[index++] = x + 1.0f;
                vertices[index++] = heightmap[x + 1][y + 1] * yScale;
                vertices[index++] = y + 1.0f;
                texCoords[texIndex++] = uvs.uEnd();
                texCoords[texIndex++] = uvs.vStart();

                // Top-left
                vertices[index++] = x;
                vertices[index++] = heightmap[x][y + 1] * yScale;
                vertices[index++] = y + 1.0f;
                texCoords[texIndex++] = uvs.uStart();
                texCoords[texIndex++] = uvs.vStart();
            }
        }

        if (displayList != null) {
            displayList.delete();
        }

        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.length).put(vertices).flip();
        FloatBuffer texCoordBuffer = BufferUtils.createFloatBuffer(texCoords.length).put(texCoords).flip();

        displayList = new DisplayList();
        displayList.begin();
        {
            glEnable(GL_TEXTURE_2D);
            glColor4f(1.0f, 1.0f, 1.0f, 1.0f); // White with full opacity
            glCullFace(GL_FRONT);

            textureAtlas.bind();

            glEnableClientState(GL_VERTEX_ARRAY);
            glEnableClientState(GL_TEXTURE_COORD_ARRAY);

            glVertexPointer(3, GL_FLOAT, 0, vertexBuffer);
            glTexCoordPointer(2, GL_FLOAT, 0, texCoordBuffer);

            glDrawArrays(GL_TRIANGLES, 0, numVertices);

            glDisableClientState(GL_VERTEX_ARRAY);
            glDisableClientState(GL_TEXTURE_COORD_ARRAY);
            glDisable(GL_TEXTURE_2D);
            glCullFace(GL_BACK);
        }
        displayList.end();
    }

    public void setHeightmap(float[][] heightmap, Vector2i[][] textureIndices, TextureAtlas textureAtlas) {
        calculateVertices(heightmap, textureIndices, textureAtlas);
    }

    @Override
    public void render() {
        if (displayList != null) {
            displayList.call();
        }
    }

    @Override
    public void destroy() {
        displayList.delete();
        displayList = null;
    }

    @Override
    public boolean transparent() {
        return false;
    }
}
