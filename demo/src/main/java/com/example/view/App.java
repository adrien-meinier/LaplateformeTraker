package com.example.view;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * App — version neutralisée.
 * 
 * Cette classe ne doit plus initialiser la base ni lancer l'application,
 * car Main.java est désormais le seul point d'entrée.
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {
        // Classe neutralisée : ne rien faire ici.
        // L'application démarre via Main.java.
    }

    public static void main(String[] args) {
        // Ne pas lancer l'application depuis ici.
        // Main.java est le point d'entrée unique.
    }
}
