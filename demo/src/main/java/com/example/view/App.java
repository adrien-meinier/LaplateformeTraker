package com.example.view;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * App — main application class for JavaFX.
 * 
 * This class should not initialize the database or launch the application,
 * as Main.java is now the only entry point.
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {
        // Class neutralized: do nothing here.
        // The application starts via Main.java.
    }

    public static void main(String[] args) {
        // Do not launch the application from here.
        // Main.java is the only entry point.
    }
}
