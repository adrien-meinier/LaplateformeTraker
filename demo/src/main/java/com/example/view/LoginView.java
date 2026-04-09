package com.example.view;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
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
import javafx.util.Duration;

public class LoginView {

    private static final String BG_DARK  = "#0f1117";
    private static final String BG_LEFT  = "#111420";
    private static final String ACCENT   = "#4f8ef7";
    private static final String ACCENT2  = "#7c4df8";
    private static final String SUCCESS  = "#22c55e";
    private static final String DANGER   = "#f43f5e";
    private static final String TEXT_MAIN= "#f1f5f9";
    private static final String TEXT_SUB = "#64748b";
    private static final String BORDER   = "#2a2d3a";
    private static final String INPUT_BG = "#1e2130";

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
        stage.setTitle("Student Manager");

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
        animateIn(right);
    }

    // ── Panneau gauche ────────────────────────────────────────────────────
    private StackPane buildLeft() {
        StackPane pane = new StackPane();
        pane.setStyle("-fx-background-color: " + BG_LEFT + ";");

        Rectangle bgGrad = new Rectangle(400, 580);
        bgGrad.setFill(new LinearGradient(0,0,1,1,true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#111420")),
                new Stop(1, Color.web("#0d0f1a"))));

        Pane dots = buildDotGrid(400, 580);

        Circle orb1 = orb(150, ACCENT2, 0.30); StackPane.setAlignment(orb1, Pos.TOP_LEFT);    StackPane.setMargin(orb1, new Insets(-50,0,0,-50));
        Circle orb2 = orb(100, ACCENT,  0.25); StackPane.setAlignment(orb2, Pos.BOTTOM_RIGHT); StackPane.setMargin(orb2, new Insets(0,-30,-30,0));
        Circle orb3 = orb(60,  "#22d3ee",0.20); StackPane.setAlignment(orb3, Pos.CENTER);      StackPane.setMargin(orb3, new Insets(50,0,0,60));
        floatOrb(orb1,  8, 12, 4000);
        floatOrb(orb2, -6, 10, 5000);
        floatOrb(orb3,  5, -8, 3500);

        VBox content = new VBox(14);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(0, 44, 0, 44));

        StackPane iconBox = new StackPane();
        Circle iconBg = new Circle(40);
        iconBg.setFill(new LinearGradient(0,0,1,1,true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web(ACCENT)), new Stop(1, Color.web(ACCENT2))));
        Text iconTxt = new Text("🎓"); iconTxt.setFont(Font.font(28));
        iconBox.getChildren().addAll(iconBg, iconTxt);

        Text t1 = new Text("Student"); t1.setFont(Font.font("Georgia", FontWeight.BOLD, 40)); t1.setFill(Color.web(TEXT_MAIN));
        Text t2 = new Text("Manager");
        t2.setFont(Font.font("Georgia", FontWeight.BOLD, 40));
        t2.setFill(new LinearGradient(0,0,1,0,true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web(ACCENT)), new Stop(1, Color.web(ACCENT2))));

        Text sub = new Text("Gérez vos étudiants\nde façon simple et élégante.");
        sub.setFont(Font.font("System", 13)); sub.setFill(Color.web(TEXT_SUB));
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
        Text v = new Text(val); v.setFont(Font.font("Georgia", FontWeight.BOLD, 18)); v.setFill(Color.web(ACCENT));
        Text l = new Text(lbl); l.setFont(Font.font("System", 11)); l.setFill(Color.web(TEXT_SUB));
        b.getChildren().addAll(v, l); return b;
    }

    private Pane buildDotGrid(double w, double h) {
        Pane p = new Pane(); p.setPrefSize(w,h); p.setMouseTransparent(true);
        for (int x = 24; x < w; x += 28)
            for (int y = 24; y < h; y += 28) {
                Circle d = new Circle(1.2, Color.web("rgba(255,255,255,0.05)"));
                d.setCenterX(x); d.setCenterY(y); p.getChildren().add(d);
            }
        return p;
    }

    // ── Panneau droit ─────────────────────────────────────────────────────
    private StackPane buildRight() {
        StackPane pane = new StackPane();
        pane.setStyle("-fx-background-color:" + BG_DARK + ";");

        Rectangle div = new Rectangle(1, 580); div.setFill(Color.web(BORDER));
        StackPane.setAlignment(div, Pos.CENTER_LEFT);

        VBox form = new VBox(0); form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(0, 52, 0, 52)); form.setMaxWidth(360);

        // Bienvenue
        VBox welcome = new VBox(5); welcome.setAlignment(Pos.CENTER_LEFT);
        Text tag = new Text("BIENVENUE"); tag.setFont(Font.font("System", FontWeight.BOLD, 10));
        tag.setFill(Color.web(ACCENT));
        Text heading = new Text("Connectez-vous");
        heading.setFont(Font.font("Georgia", FontWeight.BOLD, 27)); heading.setFill(Color.web(TEXT_MAIN));
        Text subTxt = new Text("Accédez à votre espace de gestion.");
        subTxt.setFont(Font.font("System",13)); subTxt.setFill(Color.web(TEXT_SUB));
        welcome.getChildren().addAll(tag, heading, subTxt);
        VBox.setMargin(welcome, new Insets(0,0,28,0));

        // Champs
        VBox fUser = fieldBox("Utilisateur", false);
        tfUser = (TextField) fUser.getChildren().get(1);

        VBox fPass = fieldBox("Mot de passe", true);
        pfPass = (PasswordField) fPass.getChildren().get(1);
        pfPass.setOnAction(e -> doLogin());

        // Erreur
        lblError = new Label();
        lblError.setStyle("-fx-text-fill:" + DANGER + ";-fx-font-size:12px;");
        lblError.setWrapText(true); lblError.setVisible(false); lblError.setManaged(false);
        VBox.setMargin(lblError, new Insets(8,0,0,0));

        // Bouton
        btnLogin = new Button("Se connecter  →");
        btnLogin.setMaxWidth(Double.MAX_VALUE); btnLogin.setPrefHeight(48);
        btnLogin.setFont(Font.font("System", FontWeight.BOLD, 14));
        btnLogin.setStyle(btnStyle("0.95"));
        btnLogin.setOnMouseEntered(e -> btnLogin.setStyle(btnStyle("1.0")));
        btnLogin.setOnMouseExited (e -> btnLogin.setStyle(btnStyle("0.95")));
        btnLogin.setOnAction(e -> doLogin());

        // Hint
        HBox hint = new HBox(6); hint.setAlignment(Pos.CENTER);
        Rectangle dot = new Rectangle(6,6); dot.setArcWidth(6); dot.setArcHeight(6); dot.setFill(Color.web(SUCCESS));
        Text hintTxt = new Text("Démo : admin / admin123");
        hintTxt.setFont(Font.font("System",11)); hintTxt.setFill(Color.web(TEXT_SUB));
        hint.getChildren().addAll(dot, hintTxt);
        VBox.setMargin(hint, new Insets(18,0,0,0));

        form.getChildren().addAll(welcome, fUser, sp(12), fPass, lblError, sp(22), btnLogin, hint);
        pane.getChildren().addAll(div, form);
        StackPane.setAlignment(form, Pos.CENTER);
        return pane;
    }

    private VBox fieldBox(String label, boolean password) {
        VBox box = new VBox(6);
        Text lbl = new Text(label); lbl.setFont(Font.font("System", FontWeight.BOLD, 12));
        lbl.setFill(Color.web("#94a3b8"));

        Control field;
        if (password) {
            PasswordField pf = new PasswordField();
            pf.setPromptText("••••••••"); pf.setPrefHeight(46);
            pf.setStyle(inputStyle(false));
            pf.focusedProperty().addListener((o,old,f) -> pf.setStyle(inputStyle(f)));
            field = pf;
        } else {
            TextField tf = new TextField();
            tf.setPromptText("admin"); tf.setPrefHeight(46);
            tf.setStyle(inputStyle(false));
            tf.focusedProperty().addListener((o,old,f) -> tf.setStyle(inputStyle(f)));
            field = tf;
        }
        box.getChildren().addAll(lbl, field);
        return box;
    }

    private String inputStyle(boolean focused) {
        return "-fx-background-color:" + INPUT_BG + ";"
             + "-fx-border-color:" + (focused ? ACCENT : BORDER) + ";"
             + "-fx-border-radius:10;-fx-background-radius:10;"
             + "-fx-text-fill:" + TEXT_MAIN + ";-fx-prompt-text-fill:" + TEXT_SUB + ";"
             + "-fx-padding:0 14;-fx-font-size:13px;";
    }

    private String btnStyle(String opacity) {
        return "-fx-background-color:linear-gradient(to right," + ACCENT + "," + ACCENT2 + ");"
             + "-fx-text-fill:white;-fx-font-size:14px;-fx-font-weight:bold;"
             + "-fx-background-radius:10;-fx-cursor:hand;-fx-opacity:" + opacity + ";";
    }

    // ── Connexion ─────────────────────────────────────────────────────────
    private void doLogin() {
        String user = tfUser.getText().trim();
        String pass = pfPass.getText();
        if (user.isEmpty() || pass.isEmpty()) { err("Remplissez tous les champs."); return; }
        if (user.equals("admin") && pass.equals("admin123")) {
            btnLogin.setText("✓  Connexion réussie");
            btnLogin.setStyle("-fx-background-color:" + SUCCESS + ";-fx-text-fill:white;"
                    + "-fx-font-weight:bold;-fx-font-size:14px;-fx-background-radius:10;");
            PauseTransition pause = new PauseTransition(Duration.millis(650));
            pause.setOnFinished(e -> new MainMenuView(stage).show());
            pause.play();
        } else {
            attempts++;
            if (attempts >= 3) { err("Trop de tentatives. Relancez l'application."); btnLogin.setDisable(true); }
            else { err("Identifiants incorrects. " + (3-attempts) + " essai(s)."); shake(); }
        }
    }

    private void err(String msg) { lblError.setText("⚠  " + msg); lblError.setVisible(true); lblError.setManaged(true); }

    // ── Animations ────────────────────────────────────────────────────────
    private void animateIn(StackPane pane) {
        pane.setOpacity(0); pane.setTranslateX(24);
        FadeTransition ft = new FadeTransition(Duration.millis(480), pane); ft.setToValue(1);
        TranslateTransition tt = new TranslateTransition(Duration.millis(480), pane);
        tt.setToX(0); tt.setInterpolator(Interpolator.EASE_OUT);
        new ParallelTransition(ft, tt).play();
    }

    private void shake() {
        TranslateTransition tt = new TranslateTransition(Duration.millis(55), btnLogin);
        tt.setFromX(0); tt.setByX(7); tt.setCycleCount(6); tt.setAutoReverse(true);
        tt.setOnFinished(e -> btnLogin.setTranslateX(0)); tt.play();
    }

    private Circle orb(double r, String color, double op) {
        Circle c = new Circle(r); c.setFill(Color.web(color, op));
        c.setEffect(new GaussianBlur(r * 0.65)); return c;
    }

    private void floatOrb(Circle orb, double dx, double dy, long ms) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(ms), orb);
        tt.setByX(dx); tt.setByY(dy); tt.setAutoReverse(true);
        tt.setCycleCount(Animation.INDEFINITE); tt.setInterpolator(Interpolator.EASE_BOTH); tt.play();
    }

    private Region sp(double h) { Region r = new Region(); r.setPrefHeight(h); return r; }
}