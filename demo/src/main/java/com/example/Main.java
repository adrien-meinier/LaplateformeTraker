package com.example;

import java.sql.SQLException;
import com.example.model.DatabaseInitializer;
import com.example.model.StudentSeeder;
import com.example.view.LoginView; // On importe LoginView
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        // 1. Initialisation de la base de données
        try {
            DatabaseInitializer.initialize();
            // On peuple la base de données si elle est vide
            StudentSeeder.seed(DatabaseInitializer.getConnection());
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'initialisation de la base : " + e.getMessage());
            e.printStackTrace();
        }
        
        // 2. Lancement de JavaFX
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Au lieu de lancer le menu directement, on lance la vue de connexion
            // On passe le primaryStage à LoginView
            LoginView loginView = new LoginView(primaryStage);
            loginView.show();
            
        } catch (Exception e) {
            System.err.println("Erreur au démarrage de l'interface : " + e.getMessage());
            e.printStackTrace();
        }
    }
}