package com.example;

import java.sql.SQLException;
import com.example.model.DatabaseInitializer;
import com.example.view.LoginView;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        try {
            // Initialise la base + seed (déjà inclus dans initialize)
            DatabaseInitializer.initialize();

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'initialisation de la base : " + e.getMessage());
            e.printStackTrace();
        }

        // Lancement de JavaFX
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            LoginView loginView = new LoginView(primaryStage);
            loginView.show();

        } catch (Exception e) {
            System.err.println("Erreur au démarrage de l'interface : " + e.getMessage());
            e.printStackTrace();
        }
    }
}