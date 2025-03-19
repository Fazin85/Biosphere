package net.fazin.biosphere;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CapsuleShape;
import net.fazin.biosphere.engine.*;
import net.fazin.biosphere.engine.component.DiscreteDynamicsWorldComponent;
import net.fazin.biosphere.engine.component.RenderComponent;
import net.fazin.biosphere.engine.component.RigidBodyComponent;
import net.fazin.biosphere.engine.graphics.Texture;
import net.fazin.biosphere.engine.graphics.TextureAtlas;
import net.fazin.biosphere.engine.graphics.TextureAtlasEntry;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.awt.*;
import java.util.Random;

public class TestScene extends Scene {
    public TestScene() {
        super("Test");
    }

    @Override
    public void load() {
        addPhysicsWorld();

        addPlayer();
        addFloor();
        addHeightmap();
    }

    private void addPhysicsWorld() {
        GameObject physicsWorld = new GameObject();
        DiscreteDynamicsWorldComponent dynamicsWorldComponent = new DiscreteDynamicsWorldComponent(new Vector3f(0.0f, -150.0f, 0.0f));
        physicsWorld.addComponent(dynamicsWorldComponent);

        addGameObject(physicsWorld);
    }

    private void addFloor() {
        GameObject floor = new GameObject();

        RigidBodyComponent rigidBodyComponent = new RigidBodyComponent(new BoxShape(new javax.vecmath.Vector3f(50.0f, 1.0f, 50.0f)), 0.0f);
        floor.addComponent(rigidBodyComponent);

        PlaneRendererColor planeRendererColor = new PlaneRendererColor(50.0f, 50.0f, Color.green);
        floor.addComponent(new RenderComponent(planeRendererColor));

        addGameObject(floor);
    }

    private void addPlayer() {
        GameObject cameraObject = new GameObject();
        Camera camera = new Camera(70);
        camera.setActive(true);
        cameraObject.addComponent(camera);

        GameObject playerObject = new GameObject();
        CharacterController characterController = new CharacterController(cameraObject);
        playerObject.addComponent(characterController);
        playerObject.addComponent(new BiospherePlayerController(cameraObject));

        playerObject.addChild(cameraObject);

        playerObject.getTransform().position().add(0.0f, 10.0f, 0.0f);

        CapsuleShape capsuleShape = new CapsuleShape(0.5f, 1.0f);
        capsuleShape.setMargin(0.01f);

        RigidBodyComponent playerCollider = new RigidBodyComponent(capsuleShape, 1.0f);
        //playerObject.addComponent(playerCollider);

        addGameObject(playerObject);
    }

    private void addHeightmap() {
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

        GameObject heightmapObject = new GameObject();
        heightmapObject.addComponent(new RenderComponent(heightmapRenderer));

        addGameObject(heightmapObject);
    }
}
