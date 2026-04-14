package com.example.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *  LoginView conexion view for the application, with a modern design and animations.
 *
 * All animations and buttons are delegated to{@link StyleFactory}.
 * ID demo : admin / admin123
 */
public class LoginView {

    private final Stage stage;
    private TextField     tfUser;
    private PasswordField pfPass;
    private Label         lblError;
    private Button        btnLogin;
    private int           attempts = 0;

    public LoginView(Stage stage) { this.stage = stage; }

    public void show() {
        stage.setWidth(900);
        stage.setHeight(580);
        stage.setResizable(false);
        stage.setTitle("Student Manager — Connexion");

        HBox root = new HBox();
        StackPane left  = buildLeft();
        StackPane right = buildRight();
        left.setPrefWidth(400);
        right.setPrefWidth(500);
        HBox.setHgrow(left,  Priority.ALWAYS);
        HBox.setHgrow(right, Priority.ALWAYS);
        root.getChildren().addAll(left, right);

        stage.setScene(new Scene(root, 900, 580));
        stage.centerOnScreen();
        stage.show();

        // animation slide in from right for the form, and fade in for the left panel
        StyleFactory.animateFadeSlideIn(right, 24, 480);
    }

    // left panel with gradient background, animated orbs, and a welcome message
    private StackPane buildLeft() {
        StackPane pane = new StackPane();
        pane.setStyle(StyleFactory.darkLeftBg());

        Rectangle bgGrad = new Rectangle(400, 580);
        bgGrad.setFill(new LinearGradient(0,0,1,1,true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#111420")),
                new Stop(1, Color.web("#0d0f1a"))));

        Pane dots = buildDotGrid(400, 580);

        // orbs with different sizes, colors, opacities, and animation parameters, using StyleFactory.animatedOrb
        Circle orb1 = StyleFactory.animatedOrb(150, StyleFactory.D_ACCENT2, 0.30,  8,  12, 4000);
        Circle orb2 = StyleFactory.animatedOrb(100, StyleFactory.D_ACCENT,  0.25, -6,  10, 5000);
        Circle orb3 = StyleFactory.animatedOrb( 60, "#22d3ee",              0.20,  5,  -8, 3500);
        StackPane.setAlignment(orb1, Pos.TOP_LEFT);    StackPane.setMargin(orb1, new Insets(-50,0,0,-50));
        StackPane.setAlignment(orb2, Pos.BOTTOM_RIGHT); StackPane.setMargin(orb2, new Insets(0,-30,-30,0));
        StackPane.setAlignment(orb3, Pos.CENTER);      StackPane.setMargin(orb3, new Insets(50,0,0,60));

        VBox content = new VBox(14);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(0, 44, 0, 44));

        StackPane iconBox = new StackPane();
        Circle iconBg = new Circle(40);
        iconBg.setFill(new LinearGradient(0,0,1,1,true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web(StyleFactory.D_ACCENT)),
                new Stop(1, Color.web(StyleFactory.D_ACCENT2))));
        Text iconTxt = new Text("🎓"); iconTxt.setFont(Font.font(28));
        iconBox.getChildren().addAll(iconBg, iconTxt);

        Text t1 = new Text("Student");
        t1.setFont(Font.font("Georgia", FontWeight.BOLD, 40));
        t1.setFill(Color.web(StyleFactory.D_TEXT));

        Text t2 = new Text("Manager");
        t2.setFont(Font.font("Georgia", FontWeight.BOLD, 40));
        t2.setFill(new LinearGradient(0,0,1,0,true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web(StyleFactory.D_ACCENT)),
                new Stop(1, Color.web(StyleFactory.D_ACCENT2))));

        Text sub = new Text("Gérez vos étudiants\nde façon simple et élégante.");
        sub.setFont(Font.font("System", 13));
        sub.setFill(Color.web(StyleFactory.D_SUBTEXT));
        sub.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        HBox badges = new HBox(10); badges.setAlignment(Pos.CENTER);
        badges.getChildren().addAll(badge("15","Étudiants"), badge("4","Vues"), badge("∞","Données"));

        content.getChildren().addAll(iconBox, t1, t2, sub, badges);
        pane.getChildren().addAll(bgGrad, dots, orb1, orb2, orb3, content);
        return pane;
    }

    private VBox badge(String val, String lbl) {
        VBox b = new VBox(2); b.setAlignment(Pos.CENTER);
        b.setPadding(new Insets(10,14,10,14));
        b.setStyle("-fx-background-color:rgba(255,255,255,0.05);-fx-background-radius:10;"
                + "-fx-border-color:rgba(255,255,255,0.08);-fx-border-radius:10;");
        Text v = new Text(val);
        v.setFont(Font.font("Georgia", FontWeight.BOLD, 18));
        v.setFill(Color.web(StyleFactory.D_ACCENT));
        Text l = new Text(lbl);
        l.setFont(Font.font("System", 11));
        l.setFill(Color.web(StyleFactory.D_SUBTEXT));
        b.getChildren().addAll(v, l); return b;
    }

    // right panel with the login form, including fields for username and password, a login button, error messages, and a link to the registration view. The form uses StyleFactory for consistent styling of inputs, buttons, and error messages.
    private StackPane buildRight() {
        StackPane pane = new StackPane();
        pane.setStyle(StyleFactory.darkBg());

        Rectangle div = new Rectangle(1, 580);
        div.setFill(Color.web(StyleFactory.D_BORDER));
        StackPane.setAlignment(div, Pos.CENTER_LEFT);

        VBox form = new VBox(0);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(0, 52, 0, 52));
        form.setMaxWidth(360);

        // title section with a welcome message, using different font sizes and colors for the tag, heading, and subtext
        VBox welcome = new VBox(5); welcome.setAlignment(Pos.CENTER_LEFT);
        Text tag = new Text("BIENVENUE");
        tag.setFont(Font.font("System", FontWeight.BOLD, 10));
        tag.setFill(Color.web(StyleFactory.D_ACCENT));
        Text heading = new Text("Connectez-vous");
        heading.setFont(Font.font("Georgia", FontWeight.BOLD, 27));
        heading.setFill(Color.web(StyleFactory.D_TEXT));
        Text subTxt = new Text("Accédez à votre espace de gestion.");
        subTxt.setFont(Font.font("System", 13));
        subTxt.setFill(Color.web(StyleFactory.D_SUBTEXT));
        welcome.getChildren().addAll(tag, heading, subTxt);
        VBox.setMargin(welcome, new Insets(0,0,28,0));

        //  Form fields for username and password, with labels and styled inputs using StyleFactory
        VBox fUser = fieldBox("Utilisateur", false);
        tfUser = (TextField) fUser.getChildren().get(1);

        VBox fPass = fieldBox("Mot de passe", true);
        pfPass = (PasswordField) fPass.getChildren().get(1);
        pfPass.setOnAction(e -> doLogin());

        // Error label for displaying login errors, hidden by default and styled with StyleFactory
        lblError = new Label();
        lblError.setStyle("-fx-text-fill:" + StyleFactory.D_DANGER + ";-fx-font-size:12px;");
        lblError.setWrapText(true);
        lblError.setVisible(false);
        lblError.setManaged(false);
        VBox.setMargin(lblError, new Insets(8,0,0,0));

        // button for submitting the login form, with an action handler that calls the doLogin() method, and styled using StyleFactory
        btnLogin = StyleFactory.loginBtn("Se connecter  →");
        btnLogin.setOnAction(e -> doLogin());

        // Link to the registration view, with a prompt and a clickable "S'inscrire" text that opens the RegisterView when clicked, styled using StyleFactory
        HBox registerLink = new HBox(4); registerLink.setAlignment(Pos.CENTER);
        Text noAccount = new Text("Pas encore de compte ?");
        noAccount.setFont(Font.font("System", 12));
        noAccount.setFill(Color.web(StyleFactory.D_SUBTEXT));
        Text regBtn = new Text("S'inscrire");
        regBtn.setFont(Font.font("System", FontWeight.BOLD, 12));
        regBtn.setFill(Color.web(StyleFactory.D_ACCENT2));
        regBtn.setStyle("-fx-cursor:hand;");
        regBtn.setOnMouseEntered(e -> regBtn.setFill(Color.web(StyleFactory.D_ACCENT)));
        regBtn.setOnMouseExited (e -> regBtn.setFill(Color.web(StyleFactory.D_ACCENT2)));
        regBtn.setOnMouseClicked(e -> new RegisterView(stage).show());
        registerLink.getChildren().addAll(noAccount, regBtn);
        VBox.setMargin(registerLink, new Insets(10,0,0,0));

        form.getChildren().addAll(
                welcome,
                fUser, StyleFactory.spacer(12),
                fPass,
                lblError, StyleFactory.spacer(22),
                btnLogin, registerLink
        );

        pane.getChildren().addAll(div, form);
        StackPane.setAlignment(form, Pos.CENTER);
        return pane;
    }

    //  Login logic that validates the input fields, attempts to authenticate the user using the UserDAO, and handles success and error cases with appropriate messages and animations using StyleFactory.
    private void doLogin() {
        String user = tfUser.getText().trim();
        String pass = pfPass.getText();
        if (user.isEmpty() || pass.isEmpty()) { err("Remplissez tous les champs."); return; }

        try {
            com.example.controller.UserDAO dao = new com.example.controller.UserDAO();
            com.example.model.UserModel loggedIn = dao.login(user, pass);

            if (loggedIn != null) {
                StyleFactory.animateSuccess(btnLogin, "✓  Connexion réussie", 650,
                        () -> new MainMenuView(stage).show());
            } else {
                attempts++;
                if (attempts >= 3) {
                    err("Trop de tentatives. Relancez l'application.");
                    btnLogin.setDisable(true);
                } else {
                    err("Identifiants incorrects. " + (3 - attempts) + " essai(s).");
                    StyleFactory.animateShake(btnLogin, 7);
                }
            }
        } catch (Exception ex) {
            err("Erreur de connexion : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void err(String msg) {
        lblError.setText("⚠  " + msg);
        lblError.setVisible(true);
        lblError.setManaged(true);
    }

    // helper method to create a labeled input field, with support for both text and password fields, using StyleFactory for consistent styling
    private VBox fieldBox(String label, boolean password) {
        VBox box = new VBox(6);
        Text lbl = new Text(label);
        lbl.setFont(Font.font("System", FontWeight.BOLD, 12));
        lbl.setFill(Color.web("#94a3b8"));
        Control field;
        if (password) {
            PasswordField pf = new PasswordField();
            pf.setPromptText("••••••••"); pf.setPrefHeight(46);
            pf.setStyle(StyleFactory.darkInputStyle(false));
            pf.focusedProperty().addListener((o,old,f) -> pf.setStyle(StyleFactory.darkInputStyle(f)));
            field = pf;
        } else {
            TextField tf = new TextField();
            tf.setPromptText("admin"); tf.setPrefHeight(46);
            tf.setStyle(StyleFactory.darkInputStyle(false));
            tf.focusedProperty().addListener((o,old,f) -> tf.setStyle(StyleFactory.darkInputStyle(f)));
            field = tf;
        }
        box.getChildren().addAll(lbl, field); return box;
    }

    private Pane buildDotGrid(double w, double h) {
        Pane p = new Pane(); p.setPrefSize(w, h); p.setMouseTransparent(true);
        for (int x = 24; x < w; x += 28)
            for (int y = 24; y < h; y += 28) {
                Circle d = new Circle(1.2, Color.web("rgba(255,255,255,0.05)"));
                d.setCenterX(x); d.setCenterY(y); p.getChildren().add(d);
            }
        return p;
    }
}