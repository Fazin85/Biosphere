package net.fazin.biosphere.engine;

import net.fazin.biosphere.graphics.ISceneRenderer;

public class SceneRendererBase implements ISceneRenderer {
    private Camera activeCamera;

    private void tryGetActiveCamera(Scene scene) {
        for (GameObject object : scene.getGameObjects()) {
            tryGetCameraRecursive(object);
        }
    }

    private void tryGetCameraRecursive(GameObject gameObject) {
        Camera camera;

        camera = gameObject.getComponent(Camera.class);

        if (camera != null && camera.isActive()) {
            activeCamera = camera;
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
