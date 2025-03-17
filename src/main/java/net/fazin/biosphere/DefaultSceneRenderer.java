package net.fazin.biosphere;

import org.joml.Vector3f;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class DefaultSceneRenderer extends SceneRendererBase {
    @Override
    public void render(Scene scene) {
        super.render(scene);

        Camera camera = getActiveCamera();

        if (camera == null) {
            return;
        }

        camera.prerender();

        for (GameObject object : scene.getGameObjects()) {
            renderObject(object);
        }
    }

    private void renderObject(GameObject object) {
        List<GameObject> children = object.getChildren();
        if (!children.isEmpty()) {
            for (GameObject child : children) {
                glPushMatrix();
                {
                    renderObject(child);
                }
                glPopMatrix();
            }
        }

        RenderComponent renderComponent = object.getComponent(RenderComponent.class);
        if (renderComponent == null || !renderComponent.hasRenderable() || renderComponent.getRenderable().transparent()) {
            return;
        }

        glMatrixMode(GL_MODELVIEW);

        glPushMatrix();
        {
            Vector3f position = object.getTransform().position();
            glTranslatef(position.x, position.y, position.z);
            Vector3f rotation = object.getTransform().rotation();
            glRotatef(rotation.x, 1.0f, 0.0f, 0.0f);
            glRotatef(rotation.y, 0.0f, 1.0f, 0.0f);
            glRotatef(rotation.z, 0.0f, 0.0f, 1.0f);
            Vector3f scale = object.getTransform().scale();
            glScalef(scale.x, scale.y, scale.z);

            renderComponent.getRenderable().render();
        }
        glPopMatrix();
    }
}
