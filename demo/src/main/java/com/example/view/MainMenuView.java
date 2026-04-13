package com.example.view;

import com.example.controller.StudentDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class MainMenuView {

    private final Stage stage;
    private final StudentDAO studentDAO;

    private BorderPane root;
    private StackPane contentArea;
    private Button activeSidebarBtn;
    private ImageView profileImageView; // Pour pouvoir changer l'image dynamiquement

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

        // ── SECTION PROFIL EN HAUT À GAUCHE ──────────────────────────────
        VBox profileHeader = new VBox(10);
        profileHeader.setPadding(new Insets(28, 20, 24, 20));
        profileHeader.setAlignment(Pos.CENTER_LEFT);
        profileHeader.setStyle("-fx-background-color: rgba(0,0,0,0.15);");

        // Conteneur pour la photo circulaire
        StackPane photoContainer = new StackPane();
        photoContainer.setAlignment(Pos.CENTER_LEFT);

        profileImageView = new ImageView(new Image("https://openmoji.org/data/color/svg/1F468-200D-1F4BB.svg"));
        profileImageView.setFitHeight(70);
        profileImageView.setFitWidth(70);

        // Masque circulaire
        Circle clip = new Circle(35, 35, 35);
        profileImageView.setClip(clip);

        // Bordure blanche décorative
        Circle border = new Circle(35, 35, 37);
        border.setFill(Color.TRANSPARENT);
        border.setStroke(Color.WHITE);
        border.setStrokeWidth(2);

        photoContainer.getChildren().addAll(border, profileImageView);
        photoContainer.setCursor(javafx.scene.Cursor.HAND);
        
        // Action pour changer la photo au clic
        photoContainer.setOnMouseClicked(e -> handleChoosePhoto());

        Label appName = new Label("Admin Panel");
        appName.setStyle("-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold;");

        Hyperlink changePhotoLink = new Hyperlink("Modifier la photo");
        changePhotoLink.setStyle("-fx-text-fill: rgba(255,255,255,0.7); -fx-font-size: 10px; -fx-padding: 0;");
        changePhotoLink.setOnAction(e -> handleChoosePhoto());

        profileHeader.getChildren().addAll(photoContainer, appName, changePhotoLink);
        // ────────────────────────────────────────────────────────────────

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: rgba(255,255,255,0.15);");

        Button btnEtudiants = navBtn("👨‍🎓  Étudiants", this::showEtudiants);
        Button btnAjouter = navBtn("➕  Ajouter", this::showAjouterEtudiant);
        Button btnRecherche = navBtn("🔎  Recherche", this::showRecherche);
        Button btnStatistiques = navBtn("📊  Statistiques", this::showStatistiques);

        activeSidebarBtn = btnEtudiants;
        setActive(btnEtudiants);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Separator sep2 = new Separator();
        sep2.setStyle("-fx-background-color: rgba(255,255,255,0.15);");

        Button btnQuitter = StyleFactory.sidebarBtn("🚪  Quitter");
        btnQuitter.setStyle(btnQuitter.getStyle() + "-fx-text-fill: #e74c3c;");
        btnQuitter.setOnAction(e -> stage.close());

        sidebar.getChildren().addAll(
                profileHeader, sep,
                btnEtudiants, btnAjouter, btnRecherche, btnStatistiques,
                spacer, sep2, btnQuitter
        );
        return sidebar;
    }

    /**
     * Ouvre un sélecteur de fichier pour changer la photo de profil
     */
    private void handleChoosePhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une photo de profil");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            Image newImage = new Image(selectedFile.toURI().toString());
            profileImageView.setImage(newImage);
        }
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