package com.example.view;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.DAO.StudentDAO;
import com.example.TestFXInitializer;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class EtudiantsViewTest extends TestFXInitializer {

    private EtudiantsView view;
    private Node root;

    // DAO vide pour ne pas toucher à la base
    private static class EmptyDAO extends StudentDAO {
        @Override
        public java.util.List<com.example.model.StudentModel> getAllStudents() {
            return java.util.List.of();
        }
    }

    @BeforeEach
    void setup() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            view = new EtudiantsView(new EmptyDAO());
            root = view.build();
            latch.countDown();
        });

        assertTrue(latch.await(2, TimeUnit.SECONDS), "JavaFX n'a pas fini l'initialisation");
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
    void testHeaderExistsAndButtons() {
        VBox box = (VBox) root;
        HBox header = (HBox) box.getChildren().get(0);

        assertNotNull(header);
        assertTrue(header.getChildren().size() >= 3);

        // title, spacer, export, add
        Label title = (Label) header.getChildren().get(0);
        Button btnExport = (Button) header.getChildren().get(header.getChildren().size() - 2);
        Button btnAdd = (Button) header.getChildren().get(header.getChildren().size() - 1);

        assertNotNull(title);
        assertTrue(title.getText().contains("Étudiants"));

        assertNotNull(btnExport);
        assertNotNull(btnAdd);
    }

    // ---------------------------------------------------------
    // 2. Table
    // ---------------------------------------------------------

    @Test
    void testTableExists() {
        VBox box = (VBox) root;
        TableView<?> table = (TableView<?>) box.getChildren().get(1);

        assertNotNull(table);
        assertTrue(table.getColumns().size() >= 6);
    }

    // ---------------------------------------------------------
    // 3. Pagination
    // ---------------------------------------------------------

    @Test
    void testPaginationExists() {
        VBox box = (VBox) root;
        HBox pagination = (HBox) box.getChildren().get(2);

        assertNotNull(pagination);
        assertEquals(3, pagination.getChildren().size());

        Button btnPrev = (Button) pagination.getChildren().get(0);
        Label lblPage = (Label) pagination.getChildren().get(1);
        Button btnNext = (Button) pagination.getChildren().get(2);

        assertNotNull(btnPrev);
        assertNotNull(btnNext);
        assertNotNull(lblPage);
    }
}
