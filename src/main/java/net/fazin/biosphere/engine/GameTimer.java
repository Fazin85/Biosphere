package net.fazin.biosphere.engine;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class GameTimer {
    private double lastTime;
    private float dt;

    public void update() {
        double currentTime = glfwGetTime();
        dt = (float) (currentTime - lastTime);
        lastTime = currentTime;
    }

    public float getDt() {
        return dt;
    }
}
