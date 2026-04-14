package com.example;

import java.sql.SQLException;
import com.example.model.DatabaseInitializer;
import com.example.view.LoginView;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        try {
            // initialization of the database (creation of tables and sample data)
            DatabaseInitializer.initialize();

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'initialisation de la base : " + e.getMessage());
            e.printStackTrace();
        }

        // launch the JavaFX application (calls start() method)
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