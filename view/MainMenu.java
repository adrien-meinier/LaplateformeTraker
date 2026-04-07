package view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class MainMenu extends Application {

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: white;");

        VBox centerBox = new VBox(12);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(20, 35, 20, 35));

        Label title = new Label("Menu principal");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        Label subtitle = new Label("Que souhaitez-vous faire ?");
        subtitle.setFont(Font.font("Arial", 12));
        subtitle.setTextFill(Color.GRAY);

        Button btnEtudiants = createMenuButton("👨‍🎓 Étudiants");
        Button btnFormations = createMenuButton("📚 Formations");
        Button btnStatistiques = createMenuButton("📊 Statistiques");
        Button btnParametres = createMenuButton("⚙️ Paramètres");

        VBox buttonsBox = new VBox(10, btnEtudiants, btnFormations, btnStatistiques, btnParametres);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setFillWidth(true);

        Separator separator = new Separator();

        Button btnDeconnexion = createRedButton("🚪 Se déconnecter");

        centerBox.getChildren().addAll(title, subtitle, buttonsBox, separator, btnDeconnexion);
        root.setCenter(centerBox);

        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("La Plateforme Tracker — Menu");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private Button createMenuButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPrefHeight(45);
        btn.setStyle("""
            -fx-background-color: #3498db;
            -fx-text-fill: white;
            -fx-font-size: 14px;
            -fx-font-weight: bold;
            -fx-background-radius: 10;
            -fx-cursor: hand;
            """);
        return btn;
    }

    private Button createRedButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPrefHeight(45);
        btn.setStyle("""
            -fx-background-color: #e74c3c;
            -fx-text-fill: white;
            -fx-font-size: 14px;
            -fx-font-weight: bold;
            -fx-background-radius: 10;
            -fx-cursor: hand;
            """);
        return btn;
    }

    public static void main(String[] args) {
        launch(args);
    }
}