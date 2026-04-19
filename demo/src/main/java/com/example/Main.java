package com.example;

import java.sql.SQLException;

import com.example.controller.AutoBackupService;
import com.example.controller.DatabaseBackupUtils;
import com.example.model.DatabaseInitializer;
import com.example.view.LoginView;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        try {
            DatabaseInitializer.initialize();
        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
        }

        // Start periodic cache refresh + SQL dump (every 5 minutes)
        AutoBackupService.start(5);

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        new LoginView(primaryStage).show();
    }

    @Override
    public void stop() {
        AutoBackupService.stop();

        try {
            DatabaseBackupUtils.deleteAllBackups();
            System.out.println("Backups deleted on shutdown.");
        } catch (Exception e) {
            System.err.println("Failed to delete backups on shutdown: " + e.getMessage());
        }
    }
}