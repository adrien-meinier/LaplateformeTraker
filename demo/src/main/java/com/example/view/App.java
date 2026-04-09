package com.example.view;

import com.example.model.DatabaseInitializer;
import com.example.model.StudentSeeder;
import com.example.controller.UserDAO;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class App extends Application {

    public static final String APP_TITLE = "Student Manager";

    @Override
    public void start(Stage stage) {

        // ── 1. Crée les tables (avec colonne salt) ────────────────────────
        try {
            DatabaseInitializer.initialize();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR,
                    "Impossible d'initialiser la base de données :\n" + e.getMessage(),
                    ButtonType.OK).showAndWait();
        }

        // ── 2. Crée le compte admin avec hash+salt si app_user est vide ──
        try (Connection conn = DatabaseInitializer.getConnection();
             Statement  stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM app_user");
            if (rs.next() && rs.getInt(1) == 0) {
                UserDAO userDAO = new UserDAO();
                userDAO.register("admin@admin.fr", "admin123", true);
                System.out.println("✅ Compte admin créé : admin@admin.fr / admin123");
            }
        } catch (Exception e) {
            System.err.println("Seed admin ignoré : " + e.getMessage());
        }

        // ── 3. Insère 15 étudiants fictifs si student est vide ────────────
        try (Connection conn = DatabaseInitializer.getConnection();
             Statement  stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM student");
            if (rs.next() && rs.getInt(1) == 0) {
                StudentSeeder.seed(conn);
            }
        } catch (Exception e) {
            System.err.println("Seed étudiants ignoré : " + e.getMessage());
        }

        // ── 4. Ouvre le login 
        stage.setTitle(APP_TITLE);
        stage.setResizable(false);
        new LoginView(stage).show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}