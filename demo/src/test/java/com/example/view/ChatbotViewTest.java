package com.example.view;

import com.example.TestFXInitializer;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.*;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ChatbotViewTest extends TestFXInitializer {

    private ChatbotView chatbot;

    @BeforeEach
    void setup() {
        chatbot = new ChatbotView();
    }

    // ---------------------------------------------------------
    // 1. Structure UI
    // ---------------------------------------------------------

    @Test
    @Order(1)
    void testRootStructure() {
        assertEquals(3, chatbot.getChildren().size(),
                "ChatbotView doit contenir header, scrollPane et inputArea");
    }

    @Test
    @Order(2)
    void testHeader() {
        Node header = chatbot.getChildren().get(0);
        assertInstanceOf(HBox.class, header);

        HBox h = (HBox) header;
        assertEquals(3, h.getChildren().size(),
                "Header doit contenir titre + spacer + bouton toggle");
    }

    @Test
    @Order(3)
    void testScrollPane() {
        Node scroll = chatbot.getChildren().get(1);
        assertInstanceOf(ScrollPane.class, scroll);

        ScrollPane sp = (ScrollPane) scroll;
        assertInstanceOf(VBox.class, sp.getContent(),
                "ScrollPane doit contenir un VBox");
    }

    @Test
    @Order(4)
    void testInputArea() {
        Node input = chatbot.getChildren().get(2);
        assertInstanceOf(HBox.class, input);

        HBox box = (HBox) input;
        assertEquals(2, box.getChildren().size(),
                "InputArea doit contenir TextField + Button");
    }

    // ---------------------------------------------------------
    // 2. Fonctionnalités internes
    // ---------------------------------------------------------

    @Test
    @Order(5)
    void testWelcomeMessage() throws Exception {
        VBox chatContent = getChatContent();
        assertFalse(chatContent.getChildren().isEmpty(),
                "Le message de bienvenue doit être présent");
    }

    @Test
    @Order(6)
    void testSendMessageAddsUserMessage() throws Exception {
        VBox chatContent = getChatContent();
        TextField input = getInputField();

        int before = chatContent.getChildren().size();

        Platform.runLater(() -> {
            input.setText("bonjour");
            input.getOnAction().handle(null);
        });
        Thread.sleep(150);

        assertTrue(chatContent.getChildren().size() > before,
                "Un message utilisateur doit être ajouté");
    }

    @Test
    @Order(7)
    void testBotRespondsToAide() throws Exception {
        VBox chatContent = getChatContent();
        TextField input = getInputField();

        Platform.runLater(() -> {
            input.setText("aide");
            input.getOnAction().handle(null);
        });
        Thread.sleep(150);

        String last = extractLastMessage(chatContent);
        assertTrue(last.contains("aider"),
                "Le bot doit répondre à 'aide'");
    }

    @Test
    @Order(8)
    void testBotRespondsToEtudiant() throws Exception {
        VBox chatContent = getChatContent();
        TextField input = getInputField();

        Platform.runLater(() -> {
            input.setText("ajouter étudiant");
            input.getOnAction().handle(null);
        });
        Thread.sleep(150);

        String last = extractLastMessage(chatContent);
        assertTrue(last.toLowerCase().contains("ajouter"),
                "Le bot doit répondre à 'étudiant'");
    }

    @Test
    @Order(9)
    void testBotRespondsToStat() throws Exception {
        VBox chatContent = getChatContent();
        TextField input = getInputField();

        Platform.runLater(() -> {
            input.setText("stat");
            input.getOnAction().handle(null);
        });
        Thread.sleep(150);

        String last = extractLastMessage(chatContent);
        assertTrue(last.toLowerCase().contains("stat"),
                "Le bot doit répondre à 'stat'");
    }

    @Test
    @Order(10)
    void testBotRespondsToUnknown() throws Exception {
        VBox chatContent = getChatContent();
        TextField input = getInputField();

        Platform.runLater(() -> {
            input.setText("blablabla");
            input.getOnAction().handle(null);
        });
        Thread.sleep(150);

        String last = extractLastMessage(chatContent);
        assertTrue(last.contains("Désolé"),
                "Le bot doit répondre par défaut");
    }

    // ---------------------------------------------------------
    // 3. Tests UI internes (wrapper, CSS, auto-scroll)
    // ---------------------------------------------------------

    @Test
    @Order(11)
    void testMessageWrapperStructure() throws Exception {
        VBox chatContent = getChatContent();
        TextField input = getInputField();

        Platform.runLater(() -> {
            input.setText("test wrapper");
            input.getOnAction().handle(null);
        });
        Thread.sleep(150);

        HBox wrapper = (HBox) chatContent.getChildren()
                .get(chatContent.getChildren().size() - 1);

        assertInstanceOf(Label.class, wrapper.getChildren().get(0),
                "Chaque message doit contenir un Label");
    }

    @Test
    @Order(12)
    void testAutoScroll() throws Exception {
        ScrollPane sp = getScrollPane();
        TextField input = getInputField();

        Platform.runLater(() -> {
            input.setText("test auto-scroll");
            input.getOnAction().handle(null);
        });
        Thread.sleep(150);

        assertEquals(1.0, sp.getVvalue(),
                "Le ScrollPane doit scroller automatiquement en bas");
    }

    @Test
    @Order(13)
    void testCSSUserMessage() throws Exception {
        VBox chatContent = getChatContent();
        TextField input = getInputField();

        Platform.runLater(() -> {
            input.setText("bonjour");
            input.getOnAction().handle(null);
        });
        Thread.sleep(150);

        // Avant-dernier = message utilisateur
        HBox wrapper = (HBox) chatContent.getChildren()
                .get(chatContent.getChildren().size() - 2);

        Label lbl = (Label) wrapper.getChildren().get(0);

        assertTrue(lbl.getStyle().contains("#3498db"),
                "Le message utilisateur doit être bleu");
    }

    @Test
    @Order(14)
    void testCSSBotMessage() throws Exception {
        VBox chatContent = getChatContent();
        TextField input = getInputField();

        Platform.runLater(() -> {
            input.setText("aide");
            input.getOnAction().handle(null);
        });
        Thread.sleep(300);

        // Dernier = message bot
        HBox wrapper = (HBox) chatContent.getChildren()
                .get(chatContent.getChildren().size() - 1);

        Label lbl = (Label) wrapper.getChildren().get(0);

        assertTrue(lbl.getStyle().contains("#ecf0f1"),
                "Le message bot doit être gris");
    }

    // ---------------------------------------------------------
    // 4. Toggle (test logique interne)
    // ---------------------------------------------------------

    @Test
    @Order(15)
    void testToggleLogic() throws Exception {
        HBox header = (HBox) chatbot.getChildren().get(0);
        Button toggle = (Button) header.getChildren().get(2);

        Field f = ChatbotView.class.getDeclaredField("isOpen");
        f.setAccessible(true);

        boolean before = f.getBoolean(chatbot);

        Platform.runLater(toggle::fire);
        Thread.sleep(50);

        boolean after = f.getBoolean(chatbot);

        assertNotEquals(before, after,
                "Le toggle doit inverser l'état interne isOpen");
    }

    // ---------------------------------------------------------
    // Méthodes utilitaires
    // ---------------------------------------------------------

    private VBox getChatContent() throws Exception {
        Field f = ChatbotView.class.getDeclaredField("chatContent");
        f.setAccessible(true);
        return (VBox) f.get(chatbot);
    }

    private TextField getInputField() throws Exception {
        Field f = ChatbotView.class.getDeclaredField("inputField");
        f.setAccessible(true);
        return (TextField) f.get(chatbot);
    }

    private ScrollPane getScrollPane() throws Exception {
        return (ScrollPane) chatbot.getChildren().get(1);
    }

    private String extractLastMessage(VBox chatContent) {
        HBox wrapper = (HBox) chatContent.getChildren()
                .get(chatContent.getChildren().size() - 1);

        Label lbl = (Label) wrapper.getChildren().get(0);
        return lbl.getText();
    }
}
