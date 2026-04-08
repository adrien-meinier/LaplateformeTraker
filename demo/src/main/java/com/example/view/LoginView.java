package com.example.view;

import com.example.view.App;
import com.example.auth.AuthService;//Service de connexion fictif
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * LoginView — écran de connexion.
 *
 * Affiche un formulaire nom d'utilisateur / mot de passe.
 * En cas de succès, ouvre le {@link MainMenuView}.
 * Bloque après 3 tentatives échouées.
 */
public class LoginView {

    private final Stage stage;
    private int attempts = 0;

    private TextField    tfUsername;
    private PasswordField pfPassword;
    private Label        lblError;

    public LoginView(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        stage.setWidth(420);
        stage.setHeight(520);
        stage.setScene(buildScene());
        stage.centerOnScreen();
        stage.show();
    }

    // ── Construction de la scène ──────────────────────────────────────────

    private Scene buildScene() {

        // ── Panneau principal ────────────────────────────────────────────
        VBox card = new VBox(18);
        card.setPadding(new Insets(48, 48, 40, 48));
        card.setAlignment(Pos.TOP_CENTER);
        card.setStyle(StyleFactory.cardBg());
        card.setMaxWidth(340);
        card.setMaxHeight(420);

        // Logo / titre
        Label logo = new Label("🎓");
        logo.setFont(Font.font(48));

        Label title = new Label("Student Manager");
        title.setFont(Font.font("System", FontWeight.BOLD, 22));
        title.setStyle("-fx-text-fill: " + StyleFactory.C_PRIMARY + ";");

        Label sub = new Label("Connectez-vous pour continuer");
        sub.setStyle(StyleFactory.subtitleStyle());

        // Formulaire
        VBox form = new VBox(12);
        form.setFillWidth(true);

        tfUsername = new TextField();
        tfUsername.setPromptText("Nom d'utilisateur");
        tfUsername.setStyle(StyleFactory.textFieldStyle());
        tfUsername.setPrefHeight(42);
        applyFocusStyle(tfUsername);

        pfPassword = new PasswordField();
        pfPassword.setPromptText("Mot de passe");
        pfPassword.setStyle(StyleFactory.textFieldStyle());
        pfPassword.setPrefHeight(42);
        applyFocusStyle(pfPassword);

        // Appui sur Entrée = connexion
        pfPassword.setOnAction(e -> doLogin());

        // Message d'erreur (caché par défaut)
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
                fieldGroup("Utilisateur", tfUsername),
                fieldGroup("Mot de passe", pfPassword),
                lblError,
                btnLogin
        );

        Label hint = new Label("Compte par défaut : admin / admin123");
        hint.setStyle("-fx-text-fill: #aab; -fx-font-size: 11px;");

        card.getChildren().addAll(logo, title, sub, form, hint);

        // ── Fond dégradé ────────────────────────────────────────────────
        StackPane root = new StackPane(card);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #2c3e50, #3498db);");
        StackPane.setAlignment(card, Pos.CENTER);

        Scene scene = new Scene(root, 420, 520);
        return scene;
    }

    // ── Logique de connexion ──────────────────────────────────────────────

    private void doLogin() {
        String user = tfUsername.getText().trim();
        String pass = pfPassword.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            showError("Veuillez remplir tous les champs.");
            return;
        }

        try {
            AuthService auth = new AuthService();
            if (auth.login(user, pass)) {
                new MainMenuView(stage, auth).show();
            } else {
                attempts++;
                if (attempts >= 3) {
                    showError("Trop de tentatives. Fermez et relancez l'application.");
                    tfUsername.setDisable(true);
                    pfPassword.setDisable(true);
                } else {
                    showError("Identifiants incorrects. " + (3 - attempts) + " tentative(s) restante(s).");
                    pfPassword.clear();
                }
            }
        } catch (Exception ex) {
            showError("Erreur de connexion à la base : " + ex.getMessage());
        }
    }

    private void showError(String msg) {
        lblError.setText("⚠ " + msg);
        lblError.setVisible(true);
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private VBox fieldGroup(String labelText, Control field) {
        Label lbl = new Label(labelText);
        lbl.setStyle(StyleFactory.labelStyle());
        VBox box = new VBox(4, lbl, field);
        box.setFillWidth(true);
        return box;
    }

    private void applyFocusStyle(TextField tf) {
        tf.focusedProperty().addListener((obs, old, focused) ->
            tf.setStyle(focused ? StyleFactory.textFieldFocusStyle() : StyleFactory.textFieldStyle())
        );
    }

    private void applyFocusStyle(PasswordField pf) {
        pf.focusedProperty().addListener((obs, old, focused) ->
            pf.setStyle(focused ? StyleFactory.textFieldFocusStyle() : StyleFactory.textFieldStyle())
        );
    }
}