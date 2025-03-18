package net.fazin.biosphere.graphics;

import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.Optional;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private final long id;
    private int width;
    private int height;

    protected Window(int width, int height) {
        this.width = width;
        this.height = height;

        id = glfwCreateWindow(width, height, "Biosphere", NULL, NULL);
    }

    public static Optional<Window> create(int width, int height) {
        Window window = new Window(width, height);
        if (window.id == NULL) {
            return Optional.empty();
        }

        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            glfwGetWindowSize(window.id, pWidth, pHeight);

            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window.id,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        }

        glfwMakeContextCurrent(window.id);
        // Enable v-sync
        //glfwSwapInterval(1);

        glfwShowWindow(window.id);

        return Optional.of(window);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public long getId() {
        return id;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;

        glfwSetWindowSize(id, width, height);
    }

    public void destroy() {
        glfwFreeCallbacks(id);
        glfwDestroyWindow(id);
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(id);
    }

    public void poll() {
        glfwPollEvents();
    }

    public void swapBuffers() {
        glfwSwapBuffers(id);
    }
}
