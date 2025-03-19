package net.fazin.biosphere.engine;

import net.fazin.biosphere.engine.graphics.*;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Engine {
    private static final Logger LOGGER = Logger.getLogger(Engine.class.getName());
    private Window window;
    private GameTimer gameTimer;
    private ISceneRenderer defaultRenderer;
    private ISceneRenderer transparentRenderer;
    private Scene startupScene;

    public void run() {
        if (!init()) {
            return;
        }
        loop();
        onClose();

        glfwTerminate();

        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }

    public void setStartupScene(Scene scene) {
        this.startupScene = scene;
    }

    private boolean init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 1);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_ANY_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_FALSE);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        var win = Window.create(1280, 720);
        if (win.isEmpty()) {
            return false;
        }

        window = win.get();

        Display.setWindow(window);

        glfwSetKeyCallback(window.getId(), Keyboard::keyCallback);

        GL.createCapabilities();

        gameTimer = new GameTimer();

        defaultRenderer = new DefaultSceneRenderer();
        transparentRenderer = new TransparentSortingSceneRenderer();

        if (startupScene != null) {
            SceneManager.registerScene(startupScene);
            SceneManager.loadScene(startupScene.getName());
        } else {
            LOGGER.severe("No startup scene was set");
            return false;
        }

        return true;
    }

    private void loop() {
        float accumulator = 0.0f;

        while (!window.shouldClose()) {
            window.poll();

            Keyboard.update();

            gameTimer.update();

            accumulator += gameTimer.getDt();

            if (accumulator >= 0.016f) {
                Optional<Scene> currentScene = SceneManager.getCurrentScene();
                currentScene.ifPresent(Scene::fixedUpdate);
                accumulator = 0.0f;
            }

            try {
                Optional<Scene> currentScene = SceneManager.getCurrentScene();
                currentScene.ifPresent(scene -> scene.update(gameTimer.getDt()));
            } catch (Exception e) {
                LOGGER.severe(e.toString());
            }

            render();

            window.swapBuffers();
        }

        Optional<Scene> currentScene = SceneManager.getCurrentScene();
        currentScene.ifPresent(Scene::unload);
    }

    private void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glLoadIdentity();
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);

        Optional<Scene> currentScene = SceneManager.getCurrentScene();
        if (currentScene.isPresent()) {
            Scene scene = currentScene.get();

            defaultRenderer.render(scene);
            transparentRenderer.render(scene);
        }

        int error = glGetError();
        if (error != GL_NO_ERROR) {
            LOGGER.severe("OpenGL Error: " + error);
        }
    }

    private void onClose() {
        this.window.destroy();
    }
}
