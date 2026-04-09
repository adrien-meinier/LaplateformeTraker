package com.example.view;

import com.example.controller.StudentDAO;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * MainMenuView — conteneur principal avec barre latérale + zone de contenu.
 */
public class MainMenuView {

    private final Stage stage;
    private final StudentDAO studentDAO;

    private BorderPane root;
    private StackPane contentArea;
    private Button activeSidebarBtn;

    public MainMenuView(Stage stage) {
        this.stage = stage;
        this.studentDAO = new StudentDAO();
    }

    public void show() {
        stage.setWidth(1100);
        stage.setHeight(700);
        stage.setResizable(true);
        stage.setScene(buildScene());
        stage.centerOnScreen();
        showEtudiants();
    }

    private Scene buildScene() {
        root = new BorderPane();
        root.setLeft(buildSidebar());

        contentArea = new StackPane();
        contentArea.setStyle(StyleFactory.rootBg());
        contentArea.setPadding(new Insets(24));

        root.setCenter(contentArea);
        return new Scene(root, 1100, 700);
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox(0);
        sidebar.setPrefWidth(220);
        sidebar.setStyle(StyleFactory.sidebarBg());

        VBox header = new VBox(4);
        header.setPadding(new Insets(28, 20, 24, 20));
        header.setStyle("-fx-background-color: rgba(0,0,0,0.15);");

        Label icon = new Label("🎓");
        icon.setFont(Font.font(28));

        Label appName = new Label("Student Manager");
        appName.setStyle("-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold;");

        Label userLabel = new Label("Mode local");
        userLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.6); -fx-font-size: 11px;");

        header.getChildren().addAll(icon, appName, userLabel);

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: rgba(255,255,255,0.15);");

        Button btnEtudiants = navBtn("👨‍🎓  Étudiants", this::showEtudiants);
        Button btnAjouter = navBtn("➕  Ajouter", this::showAjouterEtudiant);
        Button btnRecherche = navBtn("🔎  Recherche", this::showRecherche);
        Button btnStatistiques = navBtn("📊  Statistiques", this::showStatistiques);

        activeSidebarBtn = btnEtudiants;
        setActive(btnEtudiants);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Separator sep2 = new Separator();
        sep2.setStyle("-fx-background-color: rgba(255,255,255,0.15);");

        Button btnQuitter = StyleFactory.sidebarBtn("🚪  Quitter");
        btnQuitter.setStyle(btnQuitter.getStyle() + "-fx-text-fill: #e74c3c;");
        btnQuitter.setOnAction(e -> stage.close());
        VBox.setMargin(btnQuitter, new Insets(0, 0, 16, 0));

        sidebar.getChildren().addAll(
                header, sep,
                btnEtudiants, btnAjouter, btnRecherche, btnStatistiques,
                spacer, sep2, btnQuitter
        );
        return sidebar;
    }

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

    private void showEtudiants() {
        setContent(new EtudiantsView(studentDAO).build());
    }

    private void showAjouterEtudiant() {
        setContent(new EtudiantFormView(studentDAO, null, this::showEtudiants).build());
    }

    private void showRecherche() {
        setContent(new RechercheView(studentDAO).build());
    }

    private void showStatistiques() {
        setContent(new StatistiquesView(studentDAO).build());
    }

    private void setContent(Node node) {
        contentArea.getChildren().setAll(node);
    }
}