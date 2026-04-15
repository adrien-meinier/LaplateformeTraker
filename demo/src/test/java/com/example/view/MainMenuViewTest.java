
package com.example.view;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MainMenuViewTest {

    private static Stage stage;
    private static MainMenuView mainMenuView;

    // -----------------------------------------------------------------------
    // Initialisation JavaFX (corrigée)
    // -----------------------------------------------------------------------

    @BeforeAll
    static void startJavaFX() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        try {
            Platform.startup(() -> {
                stage = new Stage();
                mainMenuView = new MainMenuView(stage);
                mainMenuView.show();

                // attendre que JavaFX soit prêt
                Platform.runLater(latch::countDown);
            });
        } catch (IllegalStateException e) {
            Platform.runLater(() -> {
                stage = new Stage();
                mainMenuView = new MainMenuView(stage);
                mainMenuView.show();

                Platform.runLater(latch::countDown);
            });
        }

        assertTrue(latch.await(10, TimeUnit.SECONDS),
                "JavaFX ne s'est pas lancé correctement");
    }

    @AfterAll
    static void closeStage() throws Exception {
        runOnFXThread(() -> {
            if (stage != null) stage.close();
        });
    }

    // -----------------------------------------------------------------------
    // 1. Stage (corrigé pour Maven headless)
    // -----------------------------------------------------------------------

    @Test
    @Order(1)
    @DisplayName("Stage : largeur ~1150")
    void testStageWidth() throws Exception {
        runOnFXThread(() ->
                assertEquals(1150, stage.getWidth(), 5.0)
        );
    }

    @Test
    @Order(2)
    @DisplayName("Stage : hauteur ~800")
    void testStageHeight() throws Exception {
        runOnFXThread(() ->
                assertEquals(800, stage.getHeight(), 5.0)
        );
    }

    @Test
    @Order(3)
    @DisplayName("Stage : doit avoir une scène après show()")
    void testStageHasScene() throws Exception {
        runOnFXThread(() ->
                assertNotNull(stage.getScene(),
                        "Le Stage doit avoir une scène après show()")
        );
    }

    // -----------------------------------------------------------------------
    // 2. Structure UI
    // -----------------------------------------------------------------------

    @Test
    @Order(4)
    void testRootIsBorderPane() throws Exception {
        runOnFXThread(() -> {
            Scene scene = stage.getScene();
            assertNotNull(scene);
            assertInstanceOf(BorderPane.class, scene.getRoot());
        });
    }

    @Test
    @Order(5)
    void testCenterIsStackPane() throws Exception {
        runOnFXThread(() -> {
            BorderPane root = (BorderPane) stage.getScene().getRoot();
            assertInstanceOf(StackPane.class, root.getCenter());
        });
    }

    @Test
    @Order(6)
    void testSidebarExists() throws Exception {
        runOnFXThread(() -> {
            BorderPane root = (BorderPane) stage.getScene().getRoot();
            assertInstanceOf(VBox.class, root.getLeft());
        });
    }

    // -----------------------------------------------------------------------
    // 3. Boutons
    // -----------------------------------------------------------------------

    @Test
    @Order(7)
    void testAllNavButtonsPresent() throws Exception {
        runOnFXThread(() -> {
            VBox sidebar = getSidebar();

            assertAll(
                    () -> assertEquals(1, countButtonsWithText(sidebar, "Étudiants")),
                    () -> assertEquals(1, countButtonsWithText(sidebar, "Ajouter")),
                    () -> assertEquals(1, countButtonsWithText(sidebar, "Recherche")),
                    () -> assertEquals(1, countButtonsWithText(sidebar, "Statistiques"))
            );
        });
    }

    @Test
    @Order(8)
    void testQuitButtonPresent() throws Exception {
        runOnFXThread(() ->
                assertEquals(1, countButtonsWithText(getSidebar(), "Quitter"))
        );
    }

    // -----------------------------------------------------------------------
    // 4. Header
    // -----------------------------------------------------------------------

    @Test
    @Order(9)
    void testProfileHeaderLabel() throws Exception {
        runOnFXThread(() -> {
            VBox sidebar = getSidebar();
            VBox header = (VBox) sidebar.getChildren().get(0);

            boolean found = header.getChildren().stream()
                    .anyMatch(n -> n instanceof Label &&
                            ((Label) n).getText().contains("Student Manager"));

            assertTrue(found);
        });
    }

    @Test
    @Order(10)
    void testProfileImageViewNotNull() throws Exception {
        Field field = MainMenuView.class.getDeclaredField("profileImageView");
        field.setAccessible(true);
        assertNotNull(field.get(mainMenuView));
    }

    // -----------------------------------------------------------------------
    // 5. Navigation
    // -----------------------------------------------------------------------

    @Test
    @Order(11)
    void testContentAreaNotEmpty() throws Exception {
        runOnFXThread(() ->
                assertFalse(getContentArea().getChildren().isEmpty())
        );
    }

    @Test
    @Order(12)
    void testClickAjouterChangesContent() throws Exception {
        final javafx.scene.Node[] before = new javafx.scene.Node[1];
        runOnFXThread(() -> before[0] = getContentArea().getChildren().get(0));

        runOnFXThread(() -> findButtonByText(getSidebar(), "Ajouter").fire());

        final javafx.scene.Node[] after = new javafx.scene.Node[1];
        runOnFXThread(() -> after[0] = getContentArea().getChildren().get(0));

        assertNotSame(before[0], after[0]);
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private static void runOnFXThread(ThrowingRunnable action) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean hasError = new AtomicBoolean(false);

        Platform.runLater(() -> {
            try {
                action.run();
            } catch (Throwable t) {
                hasError.set(true);
            } finally {
                latch.countDown();
            }
        });

        assertTrue(latch.await(10, TimeUnit.SECONDS));
        if (hasError.get()) fail("Erreur sur le FX thread");
    }

    private VBox getSidebar() {
        return (VBox) ((BorderPane) stage.getScene().getRoot()).getLeft();
    }

    private StackPane getContentArea() {
        return (StackPane) ((BorderPane) stage.getScene().getRoot()).getCenter();
    }

    private long countButtonsWithText(javafx.scene.Parent parent, String text) {
        return parent.getChildrenUnmodifiable().stream()
                .mapToLong(node -> {
                    if (node instanceof Button &&
                            ((Button) node).getText().contains(text)) return 1;
                    if (node instanceof javafx.scene.Parent)
                        return countButtonsWithText((javafx.scene.Parent) node, text);
                    return 0;
                }).sum();
    }

    private Button findButtonByText(javafx.scene.Parent parent, String text) {
        for (javafx.scene.Node node : parent.getChildrenUnmodifiable()) {
            if (node instanceof Button &&
                    ((Button) node).getText().contains(text))
                return (Button) node;

            if (node instanceof javafx.scene.Parent) {
                Button found = findButtonByText((javafx.scene.Parent) node, text);
                if (found != null) return found;
            }
        }
        return null;
    }

    @FunctionalInterface
    interface ThrowingRunnable {
        void run() throws Exception;
    }
}