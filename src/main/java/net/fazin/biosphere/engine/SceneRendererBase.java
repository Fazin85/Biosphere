package net.fazin.biosphere.engine;

import net.fazin.biosphere.engine.graphics.ISceneRenderer;

import java.util.Optional;

public class SceneRendererBase implements ISceneRenderer {
    private Camera activeCamera;

    private void tryGetActiveCamera(Scene scene) {
        for (GameObject object : scene.getGameObjects()) {
            tryGetCameraRecursive(object);
        }
    }

    private void tryGetCameraRecursive(GameObject gameObject) {
        Optional<Camera> camera;

        camera = gameObject.getComponent(Camera.class);

        if (camera.isPresent() && camera.get().isActive()) {
            activeCamera = camera.get();
        } else {
            for (GameObject child : gameObject.getChildren()) {
                tryGetCameraRecursive(child);
            }
        }
    }


    @Override
    public void render(Scene scene) {
        if (activeCamera == null) {
            tryGetActiveCamera(scene);
        }
    }

    protected Camera getActiveCamera() {
        return activeCamera;
    }
}
