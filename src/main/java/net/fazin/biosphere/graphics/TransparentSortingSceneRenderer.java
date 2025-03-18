package net.fazin.biosphere.graphics;

import net.fazin.biosphere.engine.*;
import net.fazin.biosphere.engine.component.RenderComponent;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class TransparentSortingSceneRenderer extends SceneRendererBase {
    private static final Logger LOGGER = Logger.getLogger(TransparentSortingSceneRenderer.class.getName());
    private final List<TransparentRenderable> renderList = new ArrayList<>();

    @Override
    public void render(Scene scene) {
        List<GameObject> gameObjects = scene.getGameObjects();
        if (gameObjects.isEmpty()) {
            return;
        }

        super.render(scene);

        Camera camera = getActiveCamera();

        if (camera == null) {
            LOGGER.warning("No active camera in scene: " + scene.getName());
            return;
        }

        camera.prerender();

        renderList.clear();

        for (GameObject object : gameObjects) {
            RenderComponent renderComponent = object.getComponent(RenderComponent.class);
            if (renderComponent == null || !renderComponent.hasRenderable() || !(renderComponent.getRenderable() instanceof TransparentRenderable transparentRenderable)) {
                continue;
            }

            renderList.add(transparentRenderable);
        }

        renderList.sort((o1, o2) -> {
            Vector3f o1p = o1.position();
            Vector3f o2p = o2.position();
            float dist1 = Vector3f.distance(o1p.x, o1p.y, o1p.z, camera.getObject().getTransform().position().x, camera.getObject().getTransform().position().y, camera.getObject().getTransform().position().z);
            float dist2 = Vector3f.distance(o2p.x, o2p.y, o2p.z, camera.getObject().getTransform().position().x, camera.getObject().getTransform().position().y, camera.getObject().getTransform().position().z);

            return Float.compare(dist2, dist1);
        });

        for (TransparentRenderable renderer : renderList) {
            renderer.render();
        }
    }
}
