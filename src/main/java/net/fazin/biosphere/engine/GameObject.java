package net.fazin.biosphere.engine;

import net.fazin.biosphere.engine.component.Component;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GameObject {
    private final UUID id;
    private final List<Component> components = new ArrayList<>();
    private final Transform transform;
    private final List<GameObject> children;
    private GameObject parent;
    private String name;

    public GameObject() {
        this("GameObject", 0.0f, 0.0f, 0.0f);
    }

    public GameObject(String name, float x, float y, float z) {
        transform = new Transform(new Vector3f(x, y, z), new Vector3f(0.0f), new Vector3f(1.0f));
        id = UUID.randomUUID();
        this.name = name;

        children = new ArrayList<>();
    }

    public static <T extends Component> Optional<T> getComponentInChildren(Class<T> type, GameObject gameObject) {
        Optional<T> component = gameObject.getComponent(type);

        if (component.isPresent()) {
            return component;
        }

        for (GameObject gameObject1 : gameObject.children) {
            component = getComponentInChildren(type, gameObject1);

            if (component.isPresent()) {
                return component;
            }
        }

        return Optional.empty();
    }

    public <T extends Component> Optional<T> getComponentInChildren(Class<T> type) {
        return getComponentInChildren(type, this);
    }

    public GameObject getParent() {
        return parent;
    }

    public void setParent(GameObject gameObject) {
        parent = gameObject;
    }

    public void addChild(GameObject gameObject) {
        gameObject.parent = this;
        children.add(gameObject);
    }

    public List<GameObject> getChildren() {
        return children;
    }

    public void addComponent(Component component) {
        component.setObject(this);
        components.add(component);
    }

    public <T extends Component> Optional<T> getComponent(Class<T> type) {
        for (Component component : components) {
            if (type.isInstance(component)) {
                return Optional.of(type.cast(component));
            }
        }

        return Optional.empty();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Transform getTransform() {
        return transform;
    }

    public UUID getId() {
        return id;
    }

    public void created() {
        components.forEach(Component::created);
    }

    public void start() {
        components.forEach(Component::start);
    }

    public void update(float dt) {
        components.forEach(component -> component.update(dt));
    }

    public void fixedUpdate() {
        components.forEach(Component::fixedUpdate);
    }

    public void destroyed() {
        components.forEach(Component::destroyed);

        components.forEach(Component::postDestroyed);

        components.clear();

        if (!children.isEmpty()) {
            if (parent != null) {
                for (GameObject child : children) {
                    child.parent = parent;
                }
            } else {
                for (GameObject child : children) {
                    child.parent = null;
                }
            }
        }

        children.clear();
    }
}
