package com.universe;

/**
 * Entry point không kế thừa JavaFX Application, dùng cho shaded executable JAR.
 */
public final class Launcher {

    private Launcher() {
    }

    public static void main(String[] args) {
        Main.main(args);
    }
}
