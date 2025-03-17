package net.fazin.biosphere;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Scene {
    private final List<GameObject> gameObjects;
    private final String name;

    public Scene(String name) {
        gameObjects = new ArrayList<>();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addGameObject(GameObject object) {
        gameObjects.add(object);
        object.created();
    }

    public void update(float dt) {
        for (GameObject object : gameObjects) {
            object.update(dt);
        }
    }

    public void destroyGameObject(GameObject object) {
        object.destroyed();
        gameObjects.remove(object);
    }

    public GameObject findObject(UUID id) {
        for (GameObject object : gameObjects) {
            if (object.getId() == id) {
                return object;
            }
        }

        return null;
    }

    public GameObject findObject(String name) {
        for (GameObject object : gameObjects) {
            if (object.getName().equals(name)) {
                return object;
            }
        }

        return null;
    }

    public List<GameObject> getGameObjects() {
        return gameObjects;
    }
}
