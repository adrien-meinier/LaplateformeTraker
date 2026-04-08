package com.example.view;

import com.example.model.PasswordVerifier;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * LoginView — Écran de connexion avec vérification de hachage intégrée.
 */
public class LoginView {

    private final Stage stage;
    private int attempts = 0;

    // Simulation de données "Base de données" pour l'admin@admin.fr / admin
    private static final String ADMIN_EMAIL = "admin@admin.fr";
    private static final String ADMIN_SALT  = "c2FsdF9kZW1vXzEyMw=="; // "salt_demo_123" en Base64
    private static final String ADMIN_HASH  = "votre_hash_genere_ici"; // À remplacer par le vrai hash généré

    private TextField     tfUsername;
    private PasswordField pfPassword;
    private Label         lblError;

    public LoginView(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        stage.setWidth(420);
        stage.setHeight(520);
        stage.setScene(buildScene());
        stage.centerOnScreen();
        stage.setTitle("Connexion - Student Manager");
        stage.show();
    }

    private Scene buildScene() {
        VBox card = new VBox(18);
        card.setPadding(new Insets(48, 48, 40, 48));
        card.setAlignment(Pos.TOP_CENTER);
        card.setStyle(StyleFactory.cardBg());
        card.setMaxWidth(340);
        card.setMaxHeight(420);

        Label logo = new Label("🎓");
        logo.setFont(Font.font(48));

        Label title = new Label("Student Manager");
        title.setFont(Font.font("System", FontWeight.BOLD, 22));
        title.setStyle("-fx-text-fill: " + StyleFactory.C_PRIMARY + ";");

        Label sub = new Label("Connectez-vous pour continuer");
        sub.setStyle(StyleFactory.subtitleStyle());

        VBox form = new VBox(12);
        form.setFillWidth(true);

        tfUsername = new TextField();
        tfUsername.setPromptText("Email");
        tfUsername.setStyle(StyleFactory.textFieldStyle());
        tfUsername.setPrefHeight(42);
        applyFocus(tfUsername);

        pfPassword = new PasswordField();
        pfPassword.setPromptText("Mot de passe");
        pfPassword.setStyle(StyleFactory.textFieldStyle());
        pfPassword.setPrefHeight(42);
        applyFocus(pfPassword);
        pfPassword.setOnAction(e -> doLogin());

        lblError = new Label();
        lblError.setStyle("-fx-text-fill: " + StyleFactory.C_DANGER + "; -fx-font-size: 12px;");
        lblError.setWrapText(true);
        lblError.setVisible(false);

        Button btnLogin = StyleFactory.primaryBtn("Se connecter");
        btnLogin.setMaxWidth(Double.MAX_VALUE);
        btnLogin.setPrefHeight(46);
        btnLogin.setFont(Font.font("System", FontWeight.BOLD, 14));
        btnLogin.setOnAction(e -> doLogin());

        form.getChildren().addAll(
                fieldGroup("Email", tfUsername),
                fieldGroup("Mot de passe", pfPassword),
                lblError,
                btnLogin
        );

        Label hint = new Label("Compte par défaut : admin@admin.fr / admin");
        hint.setStyle("-fx-text-fill: #aab; -fx-font-size: 11px;");

        card.getChildren().addAll(logo, title, sub, form, hint);

        StackPane root = new StackPane(card);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #2c3e50, #3498db);");
        StackPane.setAlignment(card, Pos.CENTER);

        return new Scene(root, 420, 520);
    }

    private void doLogin() {
        String emailInput = tfUsername.getText().trim();
        String passInput = pfPassword.getText();

        if (emailInput.isEmpty() || passInput.isEmpty()) {
            showError("Veuillez remplir tous les champs.");
            return;
        }

        try {
            boolean isAuthenticated = false;

            // Vérification spécifique pour l'admin par défaut
            if (emailInput.equalsIgnoreCase(ADMIN_EMAIL)) {
                // Utilisation de votre PasswordVerifier
                isAuthenticated = PasswordVerifier.verify(passInput, ADMIN_SALT, ADMIN_HASH);
            }

            if (isAuthenticated) {
                // Redirection (ajustez selon vos classes existantes)
                System.out.println("Connexion réussie !");
                // new MainMenuView(stage).show(); 
            } else {
                handleFailedAttempt();
            }

        } catch (Exception ex) {
            // Si APP_PEPPER est null, PasswordHasher lancera une erreur
            showError("Erreur de hachage : Vérifiez votre configuration Pepper.");
            ex.printStackTrace();
        }
    }

    private void handleFailedAttempt() {
        attempts++;
        if (attempts >= 3) {
            showError("Trop de tentatives. Accès bloqué.");
            tfUsername.setDisable(true);
            pfPassword.setDisable(true);
        } else {
            showError("Identifiants incorrects. " + (3 - attempts) + " essai(s) restant(s).");
            pfPassword.clear();
        }
    }

    private void showError(String msg) {
        lblError.setText("⚠ " + msg);
        lblError.setVisible(true);
    }

    private VBox fieldGroup(String labelText, Control field) {
        Label lbl = new Label(labelText);
        lbl.setStyle(StyleFactory.labelStyle());
        VBox box = new VBox(4, lbl, field);
        box.setFillWidth(true);
        return box;
    }

    private void applyFocus(TextField tf) {
        tf.focusedProperty().addListener((obs, old, focused) ->
            tf.setStyle(focused ? StyleFactory.textFieldFocusStyle() : StyleFactory.textFieldStyle()));
    }

    private void applyFocus(PasswordField pf) {
        pf.focusedProperty().addListener((obs, old, focused) ->
            pf.setStyle(focused ? StyleFactory.textFieldFocusStyle() : StyleFactory.textFieldStyle()));
    }
}