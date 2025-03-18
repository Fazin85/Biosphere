package net.fazin.biosphere.graphics;

import org.joml.Vector2d;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.glfw.GLFW.*;

public class Display {
    private static Window window;

    private Display() {

    }

    public static void setWindow(Window window) {
        Display.window = window;
    }

    public static int getWidth() {
        return window.getWidth();
    }

    public static int getHeight() {
        return window.getHeight();
    }

    public static boolean isFocused() {
        return glfwGetWindowAttrib(window.getId(), GLFW_FOCUSED) == GLFW.GLFW_TRUE;
    }

    public static Vector2d getCursorPosition() {
        double[] mouseX = new double[1];
        double[] mouseY = new double[1];
        glfwGetCursorPos(window.getId(), mouseX, mouseY);

        return new Vector2d(mouseX[0], mouseY[0]);
    }

    public static void setCursorPosition(Vector2d position) {
        glfwSetCursorPos(window.getId(), position.x, position.y);
    }
}
