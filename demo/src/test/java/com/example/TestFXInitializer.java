package com.example;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;

/**
 * Initialise JavaFX une seule fois pour tous les tests.
 * Empêche l'erreur : "Toolkit already initialized".
 */
public abstract class TestFXInitializer {

    private static boolean initialized = false;

    @BeforeAll
    static void initJavaFX() throws Exception {
        if (!initialized) {
            initialized = true;

            final Object lock = new Object();
            synchronized (lock) {
                Platform.startup(() -> {
                    synchronized (lock) {
                        lock.notify();
                    }
                });
                lock.wait();
            }
        }
    }
}