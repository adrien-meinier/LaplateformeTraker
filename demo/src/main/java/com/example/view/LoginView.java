package com.example.view;

import com.example.AuthService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * LoginView — écran de connexion.
 */
public class LoginView {

    private final Stage stage;
    private int attempts = 0;

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
            showError("Erreur de connexion : " + ex.getMessage());
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