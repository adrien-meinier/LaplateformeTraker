package com.example.view;

import com.example.TestFXInitializer;
import com.example.controller.StudentDAO;
import com.example.model.StudentModel;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class StatistiquesViewTest extends TestFXInitializer {

    private StatistiquesView view;
    private Node root;

    private static class FakeDAO extends StudentDAO {
        @Override
        public List<StudentModel> getAllStudents() {
            return List.of(
                    new StudentModel(1, "Alice", "Martin", LocalDate.of(2005, 3, 12),
                            LocalDateTime.now(), LocalDateTime.now()),
                    new StudentModel(2, "Bob", "Durand", LocalDate.of(2004, 5, 22),
                            LocalDateTime.now(), LocalDateTime.now()),
                    new StudentModel(3, "Charlie", "Dupont", LocalDate.of(2003, 1, 10),
                            LocalDateTime.now(), LocalDateTime.now())
            );
        }
    }

    @BeforeEach
    void setup() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            view = new StatistiquesView(new FakeDAO());
            root = view.build();
            latch.countDown();
        });

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    @Test
    void testScrollPaneExists() {
        assertNotNull(root);
        assertInstanceOf(ScrollPane.class, root);

        ScrollPane scroll = (ScrollPane) root;
        assertNotNull(scroll.getContent());
        assertInstanceOf(VBox.class, scroll.getContent());
    }

    @Test
    void testHeaderExists() {
        VBox rootBox = (VBox) ((ScrollPane) root).getContent();
        VBox header = (VBox) rootBox.getChildren().get(0);

        assertNotNull(header);
        assertTrue(header.getChildren().size() >= 2);
    }

    @Test
    void testKpiRowExists() {
        VBox rootBox = (VBox) ((ScrollPane) root).getContent();
        HBox kpiRow = (HBox) rootBox.getChildren().get(1);

        assertNotNull(kpiRow);
        assertEquals(3, kpiRow.getChildren().size());
    }

    @Test
    void testChartsRowExists() {
        VBox rootBox = (VBox) ((ScrollPane) root).getContent();
        HBox chartsRow = (HBox) rootBox.getChildren().get(2);

        assertNotNull(chartsRow);
        assertEquals(2, chartsRow.getChildren().size());
    }

    @Test
    void testGenrePieChartExists() {
        VBox rootBox = (VBox) ((ScrollPane) root).getContent();
        HBox chartsRow = (HBox) rootBox.getChildren().get(2);

        VBox genreCard = (VBox) chartsRow.getChildren().get(0);

        boolean hasPie = genreCard.getChildren().stream()
                .anyMatch(n -> n instanceof PieChart);

        assertTrue(hasPie);
    }

    @Test
    void testAgeDistributionCardExists() {
        VBox rootBox = (VBox) ((ScrollPane) root).getContent();
        HBox chartsRow = (HBox) rootBox.getChildren().get(2);

        VBox ageCard = (VBox) chartsRow.getChildren().get(1);

        assertTrue(containsProgressBar(ageCard),
                "Aucune ProgressBar trouvée dans la carte d'âge");
    }

    private boolean containsProgressBar(Node node) {
        if (node instanceof ProgressBar) return true;

        if (node instanceof Pane pane) {
            for (Node child : pane.getChildren()) {
                if (containsProgressBar(child)) return true;
            }
        }
        return false;
    }

    @Test
    void testComputeDashboardStats() throws Exception {
        var method = StatistiquesView.class.getDeclaredMethod(
                "computeDashboardStats", List.class
        );
        method.setAccessible(true);

        List<StudentModel> list = new FakeDAO().getAllStudents();

        Object stats = method.invoke(view, list);

        assertNotNull(stats);
    }

    @Test
    void testBuildAgeDistributionCard() throws Exception {
        var method = StatistiquesView.class.getDeclaredMethod(
                "buildAgeDistributionCard", Map.class
        );
        method.setAccessible(true);

        Map<String, Integer> dist = Map.of("Classe 1", 10, "Classe 2", 5);

        VBox card = (VBox) method.invoke(view, dist);

        assertNotNull(card);
        assertTrue(card.getChildren().size() >= 2);
    }

    @Test
    void testBuildGenrePieChart() throws Exception {
        var method = StatistiquesView.class.getDeclaredMethod(
                "buildGenrePieChart", List.class
        );
        method.setAccessible(true);

        List<StudentModel> list = new FakeDAO().getAllStudents();

        VBox card = (VBox) method.invoke(view, list);

        assertNotNull(card);

        boolean hasPie = card.getChildren().stream()
                .anyMatch(n -> n instanceof PieChart);

        assertTrue(hasPie);
    }
}
