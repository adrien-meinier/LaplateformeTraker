package com.example.view;

import com.example.view.LoginView;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * App — point d'entrée JavaFX de Student Manager.
 *
 * Lance la fenêtre de connexion ; toute la navigation se fait ensuite
 * de vue en vue sans rechargement de Stage.
 */
public class App extends Application {

    public static final String APP_TITLE = "Student Manager";

    @Override
    public void start(Stage stage) {
        stage.setTitle(APP_TITLE);
        stage.setResizable(false);
        // Ouvre la vue de connexion
        new LoginView(stage).show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}