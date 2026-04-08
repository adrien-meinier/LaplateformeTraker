package com.example.view;

import com.example.auth.AuthService;
import com.example.service.StudentService;
import com.example.util.BackupScheduler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * MainMenuView — conteneur principal avec barre latérale + zone de contenu.
 *
 * La sidebar contient les boutons de navigation.
 * Le contenu central est remplacé dynamiquement selon la vue choisie.
 */
public class MainMenuView {

    private final Stage        stage;
    private final AuthService  auth;

    private StudentService   studentService;
    private BackupScheduler  backupScheduler;

    private BorderPane root;
    private StackPane  contentArea;

    // Bouton actif pour la surbrillance sidebar
    private Button activeSidebarBtn;

    public MainMenuView(Stage stage, AuthService auth) {
        this.stage = stage;
        this.auth  = auth;

        try {
            this.studentService = new StudentService();
            this.backupScheduler = new BackupScheduler(studentService, "backups");
            this.backupScheduler.start(30);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur BDD",
                    "Impossible de se connecter à la base de données :\n" + e.getMessage());
        }
    }

    public void show() {
        stage.setWidth(1100);
        stage.setHeight(700);
        stage.setResizable(true);
        stage.setScene(buildScene());
        stage.centerOnScreen();

        // Vue par défaut : liste des étudiants
        showEtudiants();
    }

    // ── Construction de la scène 

    private Scene buildScene() {
        root = new BorderPane();
        root.setLeft(buildSidebar());

        contentArea = new StackPane();
        contentArea.setStyle(StyleFactory.rootBg());
        contentArea.setPadding(new Insets(24));
        root.setCenter(contentArea);

        return new Scene(root, 1100, 700);
    }

    // ── Sidebar 

    private VBox buildSidebar() {
        VBox sidebar = new VBox(0);
        sidebar.setPrefWidth(220);
        sidebar.setStyle(StyleFactory.sidebarBg());

        // En-tête sidebar
        VBox header = new VBox(4);
        header.setPadding(new Insets(28, 20, 24, 20));
        header.setStyle("-fx-background-color: rgba(0,0,0,0.15);");

        Label icon = new Label("🎓");
        icon.setFont(Font.font(28));

        Label appName = new Label("Student Manager");
        appName.setStyle("-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold;");

        String username = auth.getCurrentUser() != null
                ? auth.getCurrentUser().getUsername() : "—";
        String role = auth.getCurrentUser() != null
                ? auth.getCurrentUser().getRole().name() : "";
        Label userLabel = new Label(username + " · " + role);
        userLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.6); -fx-font-size: 11px;");

        header.getChildren().addAll(icon, appName, userLabel);

        // Séparateur
        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: rgba(255,255,255,0.15);");

        // Boutons de navigation
        Button btnEtudiants    = navBtn("👨‍🎓  Étudiants",    () -> showEtudiants());
        Button btnAjouter      = navBtn("➕  Ajouter",        () -> showAjouterEtudiant());
        Button btnRecherche    = navBtn("🔎  Recherche",      () -> showRecherche());
        Button btnStatistiques = navBtn("📊  Statistiques",   () -> showStatistiques());
        Button btnExport       = navBtn("📤  Export / Import",() -> showExport());

        activeSidebarBtn = btnEtudiants;
        setActive(btnEtudiants);

        // Spacer pour pousser Déconnexion en bas
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Separator sep2 = new Separator();
        sep2.setStyle("-fx-background-color: rgba(255,255,255,0.15);");

        Button btnDeconnexion = StyleFactory.sidebarBtn("🚪  Se déconnecter");
        btnDeconnexion.setStyle(btnDeconnexion.getStyle()
                + "-fx-text-fill: #e74c3c;");
        btnDeconnexion.setOnAction(e -> logout());
        VBox.setMargin(btnDeconnexion, new Insets(0, 0, 16, 0));

        sidebar.getChildren().addAll(
                header, sep,
                btnEtudiants, btnAjouter, btnRecherche,
                btnStatistiques, btnExport,
                spacer, sep2, btnDeconnexion
        );
        return sidebar;
    }

    /** Crée un bouton de navigation avec gestion de l'état actif. */
    private Button navBtn(String text, Runnable action) {
        Button btn = StyleFactory.sidebarBtn(text);
        btn.setOnAction(e -> {
            setActive(btn);
            action.run();
        });
        return btn;
    }

    private void setActive(Button btn) {
        if (activeSidebarBtn != null) {
            activeSidebarBtn.setStyle(
                activeSidebarBtn.getStyle()
                    .replace("-fx-background-color: rgba(255,255,255,0.2);",
                             "-fx-background-color: transparent;")
            );
        }
        btn.setStyle(btn.getStyle()
            .replace("-fx-background-color: transparent;",
                     "-fx-background-color: rgba(255,255,255,0.2);"));
        activeSidebarBtn = btn;
    }

    // ── Navigation des vues 

    private void showEtudiants() {
        setContent(new EtudiantsView(studentService).build());
    }

    private void showAjouterEtudiant() {
        setContent(new EtudiantFormView(studentService, null, this::showEtudiants).build());
    }

    private void showRecherche() {
        setContent(new RechercheView(studentService).build());
    }

    private void showStatistiques() {
        setContent(new StatistiquesView(studentService).build());
    }

    private void showExport() {
        setContent(new ExportView(studentService).build());
    }

    private void setContent(Node node) {
        contentArea.getChildren().setAll(node);
    }

    // ── Déconnexion 

    private void logout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Voulez-vous vous déconnecter ?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Déconnexion");
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                backupScheduler.stop();
                auth.logout();
                stage.setResizable(false);
                new LoginView(stage).show();
            }
        });
    }

    // ── Helpers 

    /** Accesseur pour que les sous-vues puissent naviguer. */
    public void navigateTo(String view) {
        switch (view) {
            case "etudiants"  -> showEtudiants();
            case "ajouter"    -> showAjouterEtudiant();
            case "recherche"  -> showRecherche();
            case "stats"      -> showStatistiques();
            case "export"     -> showExport();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type, msg, ButtonType.OK);
        a.setTitle(title); a.setHeaderText(null); a.showAndWait();
    }
}