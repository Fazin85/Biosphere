package net.fazin.biosphere.engine;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_LAST;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class Keyboard {
    private static final boolean[] keys = new boolean[GLFW_KEY_LAST + 1];        // Current state of all keys
    private static final boolean[] lastKeys = new boolean[GLFW_KEY_LAST + 1];    // State of keys in the previous frame

    /**
     * Updates the state of the keyboard.
     * Call this once per frame after polling GLFW events.
     */
    public static void update() {
        // Copy current keys to lastKeys for the new frame
        System.arraycopy(keys, 0, lastKeys, 0, keys.length);
    }

    /**
     * GLFW Key Callback to handle keyboard events.
     *
     * @param window   The GLFW window ID
     * @param key      The key being pressed/released
     * @param scancode The platform-specific key scancode (not used here)
     * @param action   The action (GLFW_PRESS, GLFW_RELEASE, or GLFW_REPEAT)
     * @param mods     Modifier keys (not used here)
     */
    public static void keyCallback(long window, int key, int scancode, int action, int mods) {
        if (key >= 0 && key <= GLFW_KEY_LAST) {
            // Update key state based on the action
            keys[key] = action != GLFW_RELEASE;
        }
    }

    /**
     * Checks if a key is currently pressed.
     *
     * @param key The key to check (e.g., GLFW_KEY_A, GLFW_KEY_SPACE)
     * @return True if the key is currently pressed, false otherwise
     */
    public static boolean isKeyPressed(int key) {
        return keys[key];
    }

    /**
     * Checks if a key was pressed this frame but not in the last frame.
     * This is often called a "key just pressed" or "key down" event.
     *
     * @param key The key to check (e.g., GLFW_KEY_A, GLFW_KEY_SPACE)
     * @return True if the key was pressed this frame and not in the last frame
     */
    public static boolean isKeyJustPressed(int key) {
        return keys[key] && !lastKeys[key];
    }

    /**
     * Checks if a key was released this frame (pressed in the last frame, but not this frame).
     *
     * @param key The key to check (e.g., GLFW_KEY_A, GLFW_KEY_SPACE)
     * @return True if the key was released this frame
     */
    public static boolean isKeyJustReleased(int key) {
        return !keys[key] && lastKeys[key];
    }
}

