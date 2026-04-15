package com.example.view;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.junit.jupiter.api.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class RechercheViewTest {

    private RechercheView view;
    private Node root;

    // Fake DAO vide pour éviter la base
    private static class EmptyDAO extends com.example.controller.StudentDAO {
        @Override
        public java.util.List<com.example.model.StudentModel> getAllStudents() {
            return java.util.List.of();
        }
    }

    @BeforeEach
    void setup() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            view = new RechercheView(new EmptyDAO());
            root = view.build();
            latch.countDown();
        });

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    // ---------------------------------------------------------
    // 1. Structure générale
    // ---------------------------------------------------------

    @Test
    void testRootExists() {
        assertNotNull(root);
        assertTrue(root instanceof VBox);
    }

    @Test
    void testTableExists() {
        VBox box = (VBox) root;
        TableView<?> table = (TableView<?>) box.getChildren().get(4);
        assertNotNull(table);
    }

    // ---------------------------------------------------------
    // 2. Recherche par ID (UI uniquement)
    // ---------------------------------------------------------

    @Test
    void testIdSearchControlsExist() {
        VBox box = (VBox) root;
        VBox cardById = (VBox) box.getChildren().get(1);
        HBox rowId = (HBox) cardById.getChildren().get(1);

        TextField tfId = (TextField) rowId.getChildren().get(0);
        Button btnSearchId = (Button) rowId.getChildren().get(1);

        assertNotNull(tfId);
        assertNotNull(btnSearchId);
        assertTrue(btnSearchId.getText().toLowerCase().contains("recher"));
    }

    // ---------------------------------------------------------
    // 3. Recherche avancée (UI uniquement)
    // ---------------------------------------------------------

    @Test
    void testAdvancedSearchControlsExist() {
        VBox box = (VBox) root;
        VBox cardAdv = (VBox) box.getChildren().get(2);

        GridPane grid = (GridPane) cardAdv.getChildren().get(1);

        VBox fgKeyword = (VBox) grid.getChildren().get(0);
        VBox fgMin = (VBox) grid.getChildren().get(1);
        VBox fgMax = (VBox) grid.getChildren().get(2);

        TextField tfKeyword = (TextField) fgKeyword.getChildren().get(1);
        TextField tfMinAge = (TextField) fgMin.getChildren().get(1);
        TextField tfMaxAge = (TextField) fgMax.getChildren().get(1);

        HBox advButtons = (HBox) cardAdv.getChildren().get(2);
        Button btnReset = (Button) advButtons.getChildren().get(0);
        Button btnSearchAdv = (Button) advButtons.getChildren().get(1);

        assertNotNull(tfKeyword);
        assertNotNull(tfMinAge);
        assertNotNull(tfMaxAge);
        assertNotNull(btnReset);
        assertNotNull(btnSearchAdv);
    }

    // ---------------------------------------------------------
    // 4. Label de résultats
    // ---------------------------------------------------------

    @Test
    void testResultLabelExists() {
        VBox box = (VBox) root;
        Label lbl = (Label) box.getChildren().get(3);
        assertNotNull(lbl);
    }
}
