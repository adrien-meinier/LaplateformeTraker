package com.example;

import java.sql.SQLException;
import com.example.model.DatabaseInitializer;
import com.example.view.LoginView;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.example.controller.DatabaseBackupUtils;

public class Main extends Application {

    private static final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();

    public static void main(String[] args) {

        try {
            DatabaseInitializer.initialize();
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'initialisation : " + e.getMessage());
        }

        scheduler.scheduleAtFixedRate(() -> {
            try {
                DatabaseBackupUtils.createBackup();
                System.out.println("Backup effectué !");
            } catch (Exception e) {
                System.err.println("Erreur backup : " + e.getMessage());
            }
        }, 5, 120, TimeUnit.SECONDS);

        launch(args);
    }

    @Override
    public void stop() {
        scheduler.shutdown();
    }

    @Override
    public void start(Stage primaryStage) {
        new LoginView(primaryStage).show();
    }
}