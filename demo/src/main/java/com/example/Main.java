package com.example;

import java.sql.SQLException;

import com.example.model.DatabaseInitializer;
import com.example.model.StudentSeeder;
import com.example.view.MainMenuView;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        try {
            DatabaseInitializer.initialize();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            StudentSeeder.seed(DatabaseInitializer.getConnection());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        new MainMenuView(primaryStage).show();
    }
}