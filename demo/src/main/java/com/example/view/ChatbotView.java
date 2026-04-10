package com.example.view;

import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class ChatbotView extends VBox {

    private VBox chatContent;
    private TextField inputField;
    private ScrollPane scrollPane;
    private boolean isOpen = false;

    public ChatbotView() {
        // Style du conteneur principal du chat
        this.setPrefWidth(300);
        this.setMaxHeight(400);
        this.setSpacing(10);
        this.setPadding(new Insets(10));
        this.setStyle("-fx-background-color: white; " +
                      "-fx-background-radius: 15 15 0 0; " +
                      "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 0);");

        // Masquer par défaut (en bas de l'écran)
        this.setTranslateY(350);

        // --- HEADER ---
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: #3498db; -fx-padding: 10; -fx-background-radius: 10 10 0 0;");
        
        Label title = new Label("🤖 Assistant Guide");
        title.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button btnToggle = new Button("展开"); // Petit bouton pour ouvrir/fermer
        btnToggle.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-cursor: hand;");
        btnToggle.setOnAction(e -> toggleChat());

        header.getChildren().addAll(title, spacer, btnToggle);

        // --- ZONE DE MESSAGES ---
        chatContent = new VBox(10);
        chatContent.setPadding(new Insets(5));
        
        scrollPane = new ScrollPane(chatContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(300);
        scrollPane.setStyle("-fx-background: white; -fx-border-color: transparent;");

        // --- ZONE DE SAISIE ---
        HBox inputArea = new HBox(5);
        inputField = new TextField();
        inputField.setPromptText("Posez une question...");
        HBox.setHgrow(inputField, Priority.ALWAYS);
        
        Button btnSend = new Button("➤");
        btnSend.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 50;");
        btnSend.setOnAction(e -> handleUserMessage());
        inputField.setOnAction(e -> handleUserMessage());

        inputArea.getChildren().addAll(inputField, btnSend);

        this.getChildren().addAll(header, scrollPane, inputArea);
        
        // Message de bienvenue automatique
        addMessage("Bonjour ! Je suis votre guide. Tapez 'aide' pour voir ce que je peux faire.", false);
    }

    private void handleUserMessage() {
        String text = inputField.getText().trim();
        if (!text.isEmpty()) {
            addMessage(text, true); // Message de l'utilisateur (à droite)
            processResponse(text.toLowerCase()); // Algorithme de réponse
            inputField.clear();
        }
    }

    // --- ALGORITHME DE GUIDAGE SIMPLE ---
    private void processResponse(String msg) {
        if (msg.contains("aide") || msg.contains("bonjour")) {
            addMessage("Je peux vous aider à :\n1. Gérer les étudiants\n2. Voir les statistiques\n3. Exporter des données", false);
        } else if (msg.contains("étudiant") || msg.contains("ajouter")) {
            addMessage("Pour ajouter un étudiant, cliquez sur le bouton '+' dans le menu de gauche ou l'onglet 'Ajouter'.", false);
        } else if (msg.contains("stat")) {
            addMessage("Les statistiques montrent la répartition des notes et la démographie des élèves.", false);
        } else {
            addMessage("Désolé, je ne comprends pas. Tapez 'aide' !", false);
        }
    }

    private void addMessage(String text, boolean isUser) {
        Label lbl = new Label(text);
        lbl.setWrapText(true);
        lbl.setMaxWidth(200);
        lbl.setPadding(new Insets(8, 12, 8, 12));

        HBox wrapper = new HBox();
        if (isUser) {
            wrapper.setAlignment(Pos.CENTER_RIGHT);
            lbl.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 15 15 0 15;");
        } else {
            wrapper.setAlignment(Pos.CENTER_LEFT);
            lbl.setStyle("-fx-background-color: #ecf0f1; -fx-text-fill: black; -fx-background-radius: 15 15 15 0;");
        }
        
        wrapper.getChildren().add(lbl);
        chatContent.getChildren().add(wrapper);
        
        // Auto-scroll vers le bas
        scrollPane.setVvalue(1.0);
    }

    private void toggleChat() {
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), this);
        if (isOpen) {
            transition.setToY(350); // Replier
        } else {
            transition.setToY(0);   // Déplier
        }
        transition.play();
        isOpen = !isOpen;
    }
}