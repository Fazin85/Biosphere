package net.fazin.biosphere.engine.graphics;

import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class Texture {
    private final int id;
    private final int width;
    private final int height;

    public Texture(String filePath, int minFilter, int magFilter) {
        try (MemoryStack stack = stackPush()) {
            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);
            IntBuffer channelsBuffer = stack.mallocInt(1);

            ByteBuffer imageData = stbi_load(filePath, widthBuffer, heightBuffer, channelsBuffer, 4);
            if (imageData == null) {
                throw new RuntimeException("Failed to load texture: " + stbi_failure_reason());
            }

            width = widthBuffer.get();
            height = heightBuffer.get();

            id = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, id);

            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0,
                    GL_RGBA, GL_UNSIGNED_BYTE, imageData);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, minFilter);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, magFilter);

            stbi_image_free(imageData);
        }
    }

    public Texture(String filePath) {
        this(filePath, GL_NEAREST, GL_NEAREST);
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getID() {
        return id;
    }
}

