package com.example.view;

import com.example.controller.StudentDAO; 
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
        VBox root = new VBox(20);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.CENTER);
        root.setStyle(StyleFactory.rootBg());

        VBox card = new VBox(15);
        card.setPadding(new Insets(30));
        card.setStyle(StyleFactory.cardBg());
        card.setMaxWidth(400);

        Label title = new Label("Créer un compte");
        title.setFont(Font.font("System", FontWeight.BOLD, 22));
        title.setStyle("-fx-text-fill: " + StyleFactory.C_PRIMARY + ";");

        tfEmail = new TextField();
        tfEmail.setPromptText("Email");
        tfEmail.setStyle(StyleFactory.textFieldStyle());

        pfPassword = new PasswordField();
        pfPassword.setPromptText("Mot de passe");
        pfPassword.setStyle(StyleFactory.textFieldStyle());

        pfConfirm = new PasswordField();
        pfConfirm.setPromptText("Confirmer le mot de passe");
        pfConfirm.setStyle(StyleFactory.textFieldStyle());

        lblError = new Label();
        lblError.setStyle("-fx-text-fill: " + StyleFactory.C_DANGER + "; -fx-font-size: 11px;");
        lblError.setWrapText(true);
        lblError.setVisible(false);

        Button btnRegister = StyleFactory.successBtn("S'inscrire");
        btnRegister.setMaxWidth(Double.MAX_VALUE);
        btnRegister.setOnAction(e -> doRegister());

        Button btnBack = StyleFactory.secondaryBtn("Retour");
        btnBack.setMaxWidth(Double.MAX_VALUE);
        btnBack.setOnAction(e -> new LoginView(stage).show());

        card.getChildren().addAll(title, 
            new Label("Email"), tfEmail, 
            new Label("Mot de passe"), pfPassword, 
            new Label("Confirmation"), pfConfirm, 
            lblError, btnRegister, btnBack);

        root.getChildren().add(card);
        stage.setScene(new Scene(root, 450, 600));
        stage.setTitle("Inscription");
        stage.show();
    }

    private void doRegister() {
        String email = tfEmail.getText().trim();
        String pass = pfPassword.getText();
        String confirm = pfConfirm.getText();

        // 1. Validation des champs vides
        if (email.isEmpty() || pass.isEmpty()) {
            showError("Tous les champs sont obligatoires.");
            return;
        }

        // 2. Validation de la complexité du mot de passe
        // Regex : 8 caractères, 1 Majuscule, 1 Caractère spécial
        String passwordPattern = "^(?=.*[A-Z])(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,}$";
        
        if (!pass.matches(passwordPattern)) {
            showError("Le mot de passe doit contenir au moins 8 caractères, une majuscule et un caractère spécial.");
            return;
        }

        // 3. Vérification de la confirmation
        if (!pass.equals(confirm)) {
            showError("Les mots de passe ne correspondent pas.");
            return;
        }

        try {
            // Génération d'un sel unique pour cet utilisateur
            byte[] saltBytes = new byte[16];
            new SecureRandom().nextBytes(saltBytes);
            String salt = Base64.getEncoder().encodeToString(saltBytes);

            // Hachage du mot de passe
            String hashedPass = PasswordHasher.hashPassword(pass, salt);

            // TODO: Appeler votre DAO pour enregistrer en base
            // userDAO.saveUser(email, hashedPass, salt);
            
            System.out.println("Compte créé avec succès !");
            new LoginView(stage).show();

        } catch (Exception ex) {
            showError("Erreur lors de l'inscription : " + ex.getMessage());
        }
    }

    private void showError(String msg) {
        lblError.setText("⚠️ " + msg);
        lblError.setVisible(true);
    }
}