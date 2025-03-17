package net.fazin.biosphere;

import org.joml.Vector2i;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Biosphere {
    private static final Logger LOGGER = Logger.getLogger(Biosphere.class.getName());
    private Window window;
    private GameTimer gameTimer;
    private ISceneRenderer defaultRenderer;
    private ISceneRenderer transparentRenderer;
    private List<Scene> scenes;
    private int activeScene;
    private float xRot = 0.0f;

    private static String vector2iToString(Vector2i v) {
        return "(" + v.x + "," + v.y + ")";
    }

    public void run() {
        if (!init()) {
            return;
        }
        loop();
        onClose();

        glfwTerminate();
        glfwSetErrorCallback(null).free();
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

        scenes = new ArrayList<>();
        activeScene = -1;

        Scene testScene = new Scene("Test");

        GameObject playerObject = new GameObject(testScene);
        FlyController flyController = new FlyController();
        flyController.setHandleMouseInput(true);
        playerObject.addComponent(flyController);

        GameObject cameraObject = new GameObject(testScene);
        Camera camera = new Camera(70, window);
        camera.setActive(true);
        cameraObject.addComponent(camera);

        playerObject.addChild(cameraObject);

        testScene.addGameObject(playerObject);

        float[][] heightmap = new float[16][16];
        Random random = new Random();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                heightmap[x][z] = random.nextFloat();
            }
        }

        TextureAtlas textureAtlas = new TextureAtlas(new Texture("atlas.png"), 16);
        textureAtlas.setEntry(new TextureAtlasEntry("Gravel", 2, 15));
        textureAtlas.setEntry(new TextureAtlasEntry("Dirt", 1, 15));

        Vector2i[][] textureIndices = new Vector2i[16][16];
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                textureIndices[x][z] = random.nextBoolean() ? textureAtlas.indexOfEntry("Dirt") : textureAtlas.indexOfEntry("Gravel");
            }
        }

        HeightmapRenderer heightmapRenderer = new HeightmapRenderer(1, 16);
        heightmapRenderer.setHeightmap(heightmap, textureIndices, textureAtlas);

        GameObject heightmapObject = new GameObject(testScene);
        heightmapObject.addComponent(new HeightmapRendererComponent(heightmapRenderer));

        testScene.addGameObject(heightmapObject);

        scenes.add(testScene);
        activeScene = 0;

        return true;
    }

    private void loop() {
        while (!window.shouldClose()) {
            window.poll();

            Keyboard.update();

            gameTimer.update();

            if (activeScene != -1) {
                Scene scene = scenes.get(activeScene);

                scene.update(gameTimer.getDt());
            }

            render();

            window.swapBuffers();
        }
    }

    private void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glLoadIdentity();
        glEnable(GL_DEPTH_TEST);

        if (activeScene != -1) {
            Scene scene = scenes.get(activeScene);

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
