package com.example.view;

import com.example.model.PasswordHasher;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.util.Base64;
import java.security.SecureRandom;

public class RegisterView {

    private final Stage stage;
    private TextField tfEmail;
    private PasswordField pfPassword;
    private PasswordField pfConfirm;
    private Label lblError;

    public RegisterView(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        VBox card = new VBox(15);
        card.setPadding(new Insets(30, 40, 30, 40));
        card.setAlignment(Pos.TOP_CENTER);
        card.setStyle(StyleFactory.cardBg());
        card.setMaxWidth(380);

        Label title = new Label("Créer un compte");
        title.setFont(Font.font("System", FontWeight.BOLD, 22));
        title.setStyle("-fx-text-fill: " + StyleFactory.C_SUCCESS + ";");

        VBox form = new VBox(10);
        tfEmail = new TextField();
        tfEmail.setPromptText("votre@email.com");
        tfEmail.setStyle(StyleFactory.textFieldStyle());

        pfPassword = new PasswordField();
        pfPassword.setPromptText("8 car. + 1 Maj + 1 Spécial");
        pfPassword.setStyle(StyleFactory.textFieldStyle());

        pfConfirm = new PasswordField();
        pfConfirm.setPromptText("Confirmez le mot de passe");
        pfConfirm.setStyle(StyleFactory.textFieldStyle());

        lblError = new Label();
        lblError.setStyle("-fx-text-fill: " + StyleFactory.C_DANGER + "; -fx-font-size: 11px;");
        lblError.setWrapText(true);
        lblError.setVisible(false);

        Button btnRegister = StyleFactory.successBtn("S'inscrire");
        btnRegister.setMaxWidth(Double.MAX_VALUE);
        btnRegister.setPrefHeight(45);
        btnRegister.setOnAction(e -> doRegister());

        // Lien pour revenir au login
        Hyperlink linkLogin = new Hyperlink("Déjà un compte ? Se connecter");
        linkLogin.setOnAction(e -> new LoginView(stage).show());

        form.getChildren().addAll(
                new Label("Email"), tfEmail,
                new Label("Mot de passe"), pfPassword,
                new Label("Confirmation"), pfConfirm,
                lblError, btnRegister, linkLogin
        );

        card.getChildren().addAll(title, form);

        StackPane root = new StackPane(card);
        root.setStyle(StyleFactory.rootBg());
        stage.setScene(new Scene(root, 420, 600));
        stage.setTitle("Inscription");
        stage.show();
    }

    private void doRegister() {
        String email = tfEmail.getText().trim();
        String pass = pfPassword.getText();
        String confirm = pfConfirm.getText();

        // 1. Validation Regex (8 caractères, 1 Maj, 1 Spécial)
        String passRegex = "^(?=.*[A-Z])(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,}$";

        if (email.isEmpty() || pass.isEmpty()) {
            showError("Veuillez remplir tous les champs.");
        } else if (!pass.matches(passRegex)) {
            showError("Le mot de passe doit faire 8 caractères minimum, contenir une MAJUSCULE et un caractère spécial.");
        } else if (!pass.equals(confirm)) {
            showError("Les mots de passe ne correspondent pas.");
        } else {
            try {
                // Hachage sécurisé avec Salt
                byte[] saltBytes = new byte[16];
                new SecureRandom().nextBytes(saltBytes);
                String salt = Base64.getEncoder().encodeToString(saltBytes);
                String hashed = PasswordHasher.hashPassword(pass, salt);

                // ICI : Appelez votre DAO pour sauvegarder l'utilisateur
                System.out.println("Inscription réussie pour : " + email);
                
                // Retour au login après succès
                new LoginView(stage).show();
            } catch (Exception ex) {
                showError("Erreur lors du hachage.");
            }
        }
    }

    private void showError(String msg) {
        lblError.setText("⚠️ " + msg);
        lblError.setVisible(true);
    }
}