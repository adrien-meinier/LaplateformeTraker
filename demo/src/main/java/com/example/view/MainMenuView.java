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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class MainMenuView {

    private final Stage stage;
    private final StudentDAO studentDAO;

    private BorderPane root;
    private StackPane contentArea;
    private Button activeSidebarBtn;
    private ImageView profileImageView;

    private final String DEFAULT_IMAGE = "https://openmoji.org/data/color/svg/1F468-200D-1F4BB.svg";

    public MainMenuView(Stage stage) {
        this.stage = stage;
        this.studentDAO = new StudentDAO();
    }

    public void show() {
        stage.setWidth(1150);
        stage.setHeight(800); // change from 700 to 800 for more vertical space
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
        return new Scene(root, 1150, 800);
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox(0);
        sidebar.setPrefWidth(250);
        sidebar.setStyle(StyleFactory.sidebarBg());

        // 1. header section with a profile picture and application name, using a StackPane to create a circular profile image with a border, and styled with StyleFactory for consistent colors and fonts. The profile picture is clickable to allow the user to change it via a FileChooser.
        VBox profileHeader = buildProfileHeader();

        // 2.navigation buttons with icons, using StyleFactory for consistent styling, and an active state that highlights the currently selected section
        VBox navButtons = new VBox(5);
        navButtons.setPadding(new Insets(15, 0, 15, 0));
        
        Button btnEtudiants = navBtn("👨‍🎓   Étudiants", this::showEtudiants);
        Button btnAjouter = navBtn("➕   Ajouter", this::showAjouterEtudiant);
        Button btnRecherche = navBtn("🔎   Recherche", this::showRecherche);
        Button btnStatistiques = navBtn("📊   Statistiques", this::showStatistiques);
        
        navButtons.getChildren().addAll(btnEtudiants, btnAjouter, btnRecherche, btnStatistiques);
        setActive(btnEtudiants);

        // 3. flexible spacer to push the chatbot and quit button to the bottom
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // 4. chatbot area with a title, a display area for messages, and an input field for user queries. The chatbot provides contextual help based on keywords in the user's input, and is styled with a light background and clear typography for readability. The chatbot logic is simple but can be expanded in the future to provide more comprehensive assistance.
        VBox chatbotArea = buildChatbotInline();

        // 5. footer section with a separator and a quit button, styled using StyleFactory to maintain visual consistency. The quit button is highlighted in red to indicate its importance and has an action handler that closes the application when clicked.
        Separator sep = new Separator();
        sep.setOpacity(0.2);
        
        Button btnQuitter = StyleFactory.sidebarBtn("🚪   Quitter");
        btnQuitter.setStyle(btnQuitter.getStyle() + "-fx-text-fill: #e74c3c;");
        btnQuitter.setOnAction(e -> stage.close());

        sidebar.getChildren().addAll(profileHeader, navButtons, spacer, chatbotArea, sep, btnQuitter);
        return sidebar;
    }

    private VBox buildChatbotInline() {
        VBox chatBox = new VBox(8);
        chatBox.setPadding(new Insets(12));
        VBox.setMargin(chatBox, new Insets(10)); 
        
        // light background with some transparency and rounded corners for a modern look, using StyleFactory for consistent colors
        chatBox.setStyle("-fx-background-color: rgba(255,255,255,0.25); -fx-background-radius: 12;");

        Label chatTitle = new Label("🤖 Assistant Scolaire");
        chatTitle.setStyle("-fx-text-fill: #0d47a1; -fx-font-size: 11px; -fx-font-weight: bold;");

        TextArea chatDisplay = new TextArea("Bonjour ! Je suis votre assistant.\nPosez-moi une question (ex: bulletin, aide, stats).");
        chatDisplay.setEditable(false);
        chatDisplay.setWrapText(true);
        chatDisplay.setPrefHeight(100);
        
        // styling the chat display area with a light background, clear typography, and no borders for a clean look, using StyleFactory for consistent colors and fonts
        chatDisplay.setStyle("-fx-control-inner-background: transparent; " +
                           "-fx-text-fill: black; " + 
                           "-fx-background-color: transparent; " +
                           "-fx-border-color: transparent; " +
                           "-fx-font-size: 11px; " +
                           "-fx-font-family: 'Segoe UI';");

        TextField chatInput = new TextField();
        chatInput.setPromptText("Écrivez ici...");
        chatInput.setStyle("-fx-background-color: rgba(255,255,255,0.5); -fx-text-fill: black; -fx-prompt-text-fill: #555; -fx-background-radius: 10;");
        
        // simple keyword-based response logic for the chatbot, which can be expanded in the future with more complex natural language processing or integration with an AI API. The chatbot provides contextual help based on common user queries related to the application's features.
        chatInput.setOnAction(e -> {
            String input = chatInput.getText().toLowerCase().trim();
            String response;

            if (input.contains("bulletin") || input.contains("note")) {
                response = "🤖 Assistant : Pour générer un bulletin, allez dans 'Étudiants', sélectionnez un élève, puis cliquez sur l'icône 'Actions' (bleu) pour voir ses résultats.";
            } else if (input.contains("ajouter") || input.contains("inscription")) {
                response = "🤖 Assistant : Cliquez sur le bouton '➕ Ajouter' dans le menu juste au-dessus pour ouvrir le formulaire d'inscription.";
            } else if (input.contains("stats") || input.contains("graphique")) {
                response = "🤖 Assistant : L'onglet 'Statistiques' vous permet de voir la moyenne générale et la répartition par classe.";
            } else if (input.contains("recherche") || input.contains("trouver")) {
                response = "🤖 Assistant : Utilisez la loupe 'Recherche' pour filtrer les étudiants par nom ou par ID.";
            } else if (input.contains("aide") || input.contains("bonjour")) {
                response = "🤖 Assistant : Je peux vous aider pour :\n- Extraire un bulletin\n- Inscrire un élève\n- Voir les stats\nQue voulez-vous faire ?";
            } else {
                response = "🤖 Assistant : Je n'ai pas compris. Essayez de me demander comment 'avoir le bulletin' ou 'ajouter' un élève.";
            }

            chatDisplay.setText(response);
            chatInput.clear();
        });

        chatBox.getChildren().addAll(chatTitle, chatDisplay, chatInput);
        return chatBox;
    }

    private VBox buildProfileHeader() {
        VBox header = new VBox(10);
        header.setPadding(new Insets(30, 20, 20, 20));
        header.setStyle("-fx-background-color: rgba(0,0,0,0.15);");

        StackPane photoContainer = new StackPane();
        photoContainer.setAlignment(Pos.CENTER_LEFT);
        profileImageView = new ImageView(new Image(DEFAULT_IMAGE));
        profileImageView.setFitHeight(60);
        profileImageView.setFitWidth(60);
        profileImageView.setClip(new Circle(30, 30, 30));

        Circle border = new Circle(30, 30, 32);
        border.setFill(Color.TRANSPARENT);
        border.setStroke(Color.WHITE);
        border.setStrokeWidth(2);

        photoContainer.getChildren().addAll(border, profileImageView);
        photoContainer.setCursor(javafx.scene.Cursor.HAND);
        photoContainer.setOnMouseClicked(e -> handleChoosePhoto());

        Label appName = new Label("Student Manager");
        appName.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

        header.getChildren().addAll(photoContainer, appName);
        return header;
    }

    private void handleChoosePhoto() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Changer photo");
        File f = fc.showOpenDialog(stage);
        if (f != null) profileImageView.setImage(new Image(f.toURI().toString()));
    }

    private Button navBtn(String text, Runnable action) {
        Button btn = StyleFactory.sidebarBtn(text);
        btn.setOnAction(e -> { setActive(btn); action.run(); });
        return btn;
    }

    private void setActive(Button btn) {
        if (activeSidebarBtn != null) {
            activeSidebarBtn.setStyle(activeSidebarBtn.getStyle().replace("rgba(255,255,255,0.2)", "transparent"));
        }
        btn.setStyle(btn.getStyle().replace("transparent", "rgba(255,255,255,0.2)"));
        activeSidebarBtn = btn;
    }

    private void showEtudiants() { setContent(new EtudiantsView(studentDAO).build()); }
    private void showAjouterEtudiant() { setContent(new EtudiantFormView(studentDAO, null, this::showEtudiants).build()); }
    private void showRecherche() { setContent(new RechercheView(studentDAO).build()); }
    private void showStatistiques() { setContent(new StatistiquesView(studentDAO).build()); }
    private void setContent(Node node) { contentArea.getChildren().setAll(node); }
}