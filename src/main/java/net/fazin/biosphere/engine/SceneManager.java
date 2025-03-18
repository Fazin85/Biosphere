package net.fazin.biosphere.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class SceneManager {
    private static final Logger LOGGER = Logger.getLogger(SceneManager.class.getName());
    private static final List<Scene> scenes = new ArrayList<>();
    private static int currentScene = -1;

    private SceneManager() {

    }

    public static void registerScene(Scene scene) {
        scenes.add(scene);
    }

    public static Optional<Scene> getCurrentScene() {
        if (currentScene == -1) {
            return Optional.empty();
        }

        Scene scene = scenes.get(currentScene);
        if (scene == null) {
            return Optional.empty();
        }

        return Optional.of(scene);
    }

    public static void loadScene(String name) {
        for (int i = 0; i < scenes.size(); i++) {
            Scene scene = scenes.get(i);

            if (scene.getName().equals(name)) {
                Optional<Scene> cScene = getCurrentScene();
                cScene.ifPresent(Scene::unload);

                currentScene = i;
                scene.load();
                scene.start();
                LOGGER.info("Loaded scene: " + scene.getName() + ", sceneIndex: " + i);
                break;
            }
        }
    }
}
