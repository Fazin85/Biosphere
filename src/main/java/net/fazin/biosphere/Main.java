package net.fazin.biosphere;

import net.fazin.biosphere.engine.Engine;

public class Main {
    public static void main(String[] args) {
        Engine engine = new Engine();
        engine.setStartupScene(new TestScene());

        engine.run();
    }
}