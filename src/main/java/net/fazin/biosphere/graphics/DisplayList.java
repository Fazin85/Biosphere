package net.fazin.biosphere.graphics;

import static org.lwjgl.opengl.GL11.*;

public class DisplayList {
    private final int id;

    public DisplayList() {
        id = glGenLists(1);
    }

    public void begin() {
        glNewList(id, GL_COMPILE);
    }

    public void end() {
        glEndList();
    }

    public void call() {
        glCallList(id);
    }

    public void delete() {
        glDeleteLists(id, 1);
    }
}
