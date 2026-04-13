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
 * RegisterView — écran d'inscription premium.
 *
 * Toutes les animations et boutons sont délégués à {@link StyleFactory}.
 * Après inscription réussie, retour automatique vers LoginView.
 */
public class RegisterView {

    private final Stage stage;

    private TextField     tfUsername;
    private TextField     tfEmail;
    private PasswordField pfPass;
    private PasswordField pfConfirm;
    private Label         lblError;
    private Button        btnRegister;
    private Rectangle[]   strengthBars;
    private Label         lblStrength;

    public RegisterView(Stage stage) { this.stage = stage; }

    public void show() {
        stage.setWidth(900);
        stage.setHeight(620);
        stage.setResizable(false);
        stage.setTitle("Student Manager — Inscription");

        HBox root = new HBox();
        StackPane left  = buildLeft();
        StackPane right = buildRight();
        left.setPrefWidth(380);
        right.setPrefWidth(520);
        HBox.setHgrow(left,  Priority.ALWAYS);
        HBox.setHgrow(right, Priority.ALWAYS);
        root.getChildren().addAll(left, right);

        stage.setScene(new Scene(root, 900, 620));
        stage.centerOnScreen();
        stage.show();

        // Animation d'entrée via StyleFactory
        StyleFactory.animateFadeSlideIn(right, 24, 480);
    }

    // ── Panneau gauche ────────────────────────────────────────────────────
    private StackPane buildLeft() {
        StackPane pane = new StackPane();
        pane.setStyle(StyleFactory.darkLeftBg());

        Rectangle bg = new Rectangle(380, 620);
        bg.setFill(new LinearGradient(0,0,1,1,true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#111420")),
                new Stop(1, Color.web("#0d0f1a"))));

        Pane dots = buildDotGrid(380, 620);

        // Orbes via StyleFactory — couleurs différentes du Login
        Circle orb1 = StyleFactory.animatedOrb(140, StyleFactory.D_SUCCESS, 0.22, -7,  10, 4200);
        Circle orb2 = StyleFactory.animatedOrb(100, StyleFactory.D_ACCENT2, 0.28,  8,  -9, 5100);
        Circle orb3 = StyleFactory.animatedOrb( 55, "#f472b6",              0.20,  4,   7, 3300);
        StackPane.setAlignment(orb1, Pos.TOP_RIGHT);    StackPane.setMargin(orb1, new Insets(-40,-40,0,0));
        StackPane.setAlignment(orb2, Pos.BOTTOM_LEFT);  StackPane.setMargin(orb2, new Insets(0,0,-30,-30));
        StackPane.setAlignment(orb3, Pos.CENTER);       StackPane.setMargin(orb3, new Insets(-40,-60,0,0));

        VBox content = new VBox(14);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(0, 40, 0, 40));

        StackPane iconBox = new StackPane();
        Circle iconBg = new Circle(38);
        iconBg.setFill(new LinearGradient(0,0,1,1,true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web(StyleFactory.D_SUCCESS)),
                new Stop(1, Color.web(StyleFactory.D_ACCENT))));
        Text iconTxt = new Text("✨"); iconTxt.setFont(Font.font(26));
        iconBox.getChildren().addAll(iconBg, iconTxt);

        Text t1 = new Text("Rejoignez");
        t1.setFont(Font.font("Georgia", FontWeight.BOLD, 36));
        t1.setFill(Color.web(StyleFactory.D_TEXT));

        Text t2 = new Text("l'équipe");
        t2.setFont(Font.font("Georgia", FontWeight.BOLD, 36));
        t2.setFill(new LinearGradient(0,0,1,0,true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web(StyleFactory.D_SUCCESS)),
                new Stop(1, Color.web(StyleFactory.D_ACCENT))));

        Text sub = new Text("Créez votre compte en quelques\nsecondes et gérez vos étudiants.");
        sub.setFont(Font.font("System", 13));
        sub.setFill(Color.web(StyleFactory.D_SUBTEXT));
        sub.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        VBox perks = new VBox(8); perks.setAlignment(Pos.CENTER_LEFT);
        for (String perk : new String[]{
                "✓  Accès complet au tableau de bord",
                "✓  Gestion des étudiants en temps réel",
                "✓  Export CSV, JSON, PDF"}) {
            Text pt = new Text(perk);
            pt.setFont(Font.font("System", 12));
            pt.setFill(Color.web("#4ade80"));
            perks.getChildren().add(pt);
        }

        content.getChildren().addAll(iconBox, t1, t2, sub, perks);
        pane.getChildren().addAll(bg, dots, orb1, orb2, orb3, content);
        return pane;
    }

    // ── Panneau droit ─────────────────────────────────────────────────────
    private StackPane buildRight() {
        StackPane pane = new StackPane();
        pane.setStyle(StyleFactory.darkBg());

        Rectangle div = new Rectangle(1, 620);
        div.setFill(Color.web(StyleFactory.D_BORDER));
        StackPane.setAlignment(div, Pos.CENTER_LEFT);

        VBox form = new VBox(0);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(0, 48, 0, 48));
        form.setMaxWidth(380);

        // Titre
        VBox welcome = new VBox(5); welcome.setAlignment(Pos.CENTER_LEFT);
        Text tag = new Text("NOUVEAU COMPTE");
        tag.setFont(Font.font("System", FontWeight.BOLD, 10));
        tag.setFill(Color.web(StyleFactory.D_SUCCESS));
        Text heading = new Text("Inscription");
        heading.setFont(Font.font("Georgia", FontWeight.BOLD, 27));
        heading.setFill(Color.web(StyleFactory.D_TEXT));
        Text subTxt = new Text("Remplissez le formulaire pour commencer.");
        subTxt.setFont(Font.font("System", 13));
        subTxt.setFill(Color.web(StyleFactory.D_SUBTEXT));
        welcome.getChildren().addAll(tag, heading, subTxt);
        VBox.setMargin(welcome, new Insets(0,0,20,0));

        // Champs
        VBox fUser    = fieldBox("Nom d'utilisateur", "ex: alice",                false);
        tfUsername    = (TextField) fUser.getChildren().get(1);

        VBox fEmail   = fieldBox("Adresse e-mail", "ex: alice@example.com",       false);
        tfEmail       = (TextField) fEmail.getChildren().get(1);

        VBox fPass    = fieldBox("Mot de passe", "8 car. min. + 1 Maj + 1 Spécial", true);
        pfPass        = (PasswordField) fPass.getChildren().get(1);
        pfPass.textProperty().addListener((o, old, val) -> updateStrength(val));

        VBox fConfirm = fieldBox("Confirmer le mot de passe", "Répétez votre mot de passe", true);
        pfConfirm     = (PasswordField) fConfirm.getChildren().get(1);

        // Barre de force
        VBox strengthBox = buildStrengthMeter();

        // Erreur
        lblError = new Label();
        lblError.setStyle("-fx-text-fill:" + StyleFactory.D_DANGER + ";-fx-font-size:12px;");
        lblError.setWrapText(true);
        lblError.setVisible(false); lblError.setManaged(false);
        VBox.setMargin(lblError, new Insets(4,0,0,0));

        // Bouton — via StyleFactory
        btnRegister = StyleFactory.registerBtn("Créer mon compte  →");
        btnRegister.setOnAction(e -> doRegister());

        // Lien vers login
        HBox loginLink = new HBox(4); loginLink.setAlignment(Pos.CENTER);
        Text alreadyTxt = new Text("Déjà un compte ?");
        alreadyTxt.setFont(Font.font("System", 12));
        alreadyTxt.setFill(Color.web(StyleFactory.D_SUBTEXT));
        Text loginBtn = new Text("Se connecter");
        loginBtn.setFont(Font.font("System", FontWeight.BOLD, 12));
        loginBtn.setFill(Color.web(StyleFactory.D_ACCENT));
        loginBtn.setStyle("-fx-cursor:hand;");
        loginBtn.setOnMouseEntered(e -> loginBtn.setFill(Color.web(StyleFactory.D_ACCENT2)));
        loginBtn.setOnMouseExited (e -> loginBtn.setFill(Color.web(StyleFactory.D_ACCENT)));
        loginBtn.setOnMouseClicked(e -> new LoginView(stage).show());
        loginLink.getChildren().addAll(alreadyTxt, loginBtn);
        VBox.setMargin(loginLink, new Insets(12,0,0,0));

        form.getChildren().addAll(
                welcome,
                fUser,    StyleFactory.spacer(9),
                fEmail,   StyleFactory.spacer(9),
                fPass,    StyleFactory.spacer(4),
                strengthBox, StyleFactory.spacer(7),
                fConfirm,
                lblError, StyleFactory.spacer(16),
                btnRegister,
                loginLink
        );

        pane.getChildren().addAll(div, form);
        StackPane.setAlignment(form, Pos.CENTER);
        return pane;
    }

    // ── Barre de force du mot de passe ────────────────────────────────────
    private VBox buildStrengthMeter() {
        VBox box = new VBox(4);
        HBox bars = new HBox(4);
        strengthBars = new Rectangle[4];
        for (int i = 0; i < 4; i++) {
            Rectangle r = new Rectangle(0, 4);
            r.setArcWidth(4); r.setArcHeight(4);
            r.setFill(Color.web(StyleFactory.D_BORDER));
            strengthBars[i] = r;
            bars.getChildren().add(r);
        }
        lblStrength = new Label("Saisissez un mot de passe");
        lblStrength.setStyle("-fx-font-size:11px;-fx-text-fill:" + StyleFactory.D_SUBTEXT + ";");

        // Ajuster largeur des barres dynamiquement
        bars.widthProperty().addListener((o, old, w) -> {
            double bw = (w.doubleValue() - 12) / 4;
            for (Rectangle r : strengthBars) r.setWidth(bw);
        });
        box.getChildren().addAll(bars, lblStrength);
        return box;
    }

    private void updateStrength(String pass) {
        int score = 0;
        if (pass.length() >= 8)                              score++;
        if (pass.matches(".*[A-Z].*"))                       score++;
        if (pass.matches(".*[0-9].*"))                       score++;
        if (pass.matches(".*[!@#$%^&*(),.?\":{}|<>].*"))    score++;

        String[] colors = { StyleFactory.D_DANGER, StyleFactory.D_WARNING, "#eab308", StyleFactory.D_SUCCESS };
        String[] labels = { "Très faible", "Faible", "Moyen", "Fort" };

        for (int i = 0; i < 4; i++)
            strengthBars[i].setFill(Color.web(i < score ? colors[score-1] : StyleFactory.D_BORDER));

        if (pass.isEmpty()) {
            lblStrength.setText("Saisissez un mot de passe");
            lblStrength.setStyle("-fx-font-size:11px;-fx-text-fill:" + StyleFactory.D_SUBTEXT + ";");
        } else {
            int idx = Math.max(0, score - 1);
            lblStrength.setText("Force : " + labels[idx]);
            lblStrength.setStyle("-fx-font-size:11px;-fx-text-fill:" + colors[idx] + ";");
        }
    }

    // ── Validation & inscription ──────────────────────────────────────────
    private void doRegister() {
        String user    = tfUsername.getText().trim();
        String email   = tfEmail.getText().trim();
        String pass    = pfPass.getText();
        String confirm = pfConfirm.getText();

        if (user.isEmpty() || email.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
            err("Veuillez remplir tous les champs."); return;
        }
        if (!email.matches("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
            err("Adresse e-mail invalide."); return;
        }
        if (!pass.matches("^(?=.*[A-Z])(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,}$")) {
            err("Mot de passe : 8 car. min., 1 majuscule, 1 caractère spécial."); return;
        }
        if (!pass.equals(confirm)) {
            err("Les mots de passe ne correspondent pas.");
            StyleFactory.animateShake(btnRegister, 7); // Animation shake via StyleFactory
            return;
        }

        // Animation succès via StyleFactory
        StyleFactory.animateSuccess(btnRegister, "✓  Compte créé !", 800,
                () -> new LoginView(stage).show());
    }

    private void err(String msg) {
        lblError.setText("⚠  " + msg);
        lblError.setVisible(true); lblError.setManaged(true);
    }

    // ── Helpers 
    private VBox fieldBox(String label, String prompt, boolean password) {
        VBox box = new VBox(6);
        Text lbl = new Text(label);
        lbl.setFont(Font.font("System", FontWeight.BOLD, 12));
        lbl.setFill(Color.web("#94a3b8"));
        Control field;
        if (password) {
            PasswordField pf = new PasswordField();
            pf.setPromptText(prompt); pf.setPrefHeight(44);
            pf.setStyle(StyleFactory.darkInputStyle(false));
            pf.focusedProperty().addListener((o,old,f) -> pf.setStyle(StyleFactory.darkInputStyle(f)));
            field = pf;
        } else {
            TextField tf = new TextField();
            tf.setPromptText(prompt); tf.setPrefHeight(44);
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