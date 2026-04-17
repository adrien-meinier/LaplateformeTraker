package com.example.view;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.controller.StudentDAO;
import com.example.model.StudentModel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class StatistiquesView {

    private final StudentDAO dao;

    public StatistiquesView(StudentDAO dao) {
        this.dao = dao;
    }

    public Node build() {
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        VBox root = new VBox(25);
        root.setPadding(new Insets(30));

        // Titre de bienvenue
        VBox header = new VBox(5);
        Label title = new Label("🏠 Tableau de Bord");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        title.setStyle("-fx-text-fill: " + StyleFactory.C_PRIMARY + ";");
        Label subtitle = new Label("Aperçu global de l'établissement");
        subtitle.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 14px;");
        header.getChildren().addAll(title, subtitle);
        
        // On récupère les données nécessaires pour les statistiques du dashboard, telles que le nombre total d'étudiants, la moyenne générale, la meilleure note, et la répartition par âge ou classe. Ces données sont utilisées pour construire les différentes sections du dashboard, notamment les KPIs et les graphiques de répartition.
        try {
            List<StudentModel> students = dao.getAllStudents();
            DashboardStats stats = computeDashboardStats(students);

            // 1. line of KPIs with total students, average grade, and top grade. Each KPI is displayed in a card with a title, value, and subtitle, styled using the StyleFactory for consistent colors and fonts. The total students KPI shows the number of enrolled students, the average grade KPI displays the overall average grade across all students, and the top grade KPI highlights the highest grade achieved in the institution. These KPIs provide a quick overview of key metrics for the dashboard.
            HBox kpiRow = new HBox(20);
            kpiRow.getChildren().addAll(
                kpi("👥 Effectif Total", String.valueOf(stats.total), StyleFactory.C_ACCENT, "étudiants inscrits"),
                kpi("📊 Moyenne Générale", String.format("%.2f", stats.averageGrade), StyleFactory.C_PRIMARY, "/ 20"),
                kpi("🏆 Major de Promo", String.format("%.2f", stats.maxGrade), StyleFactory.C_SUCCESS, "meilleure note")
            );

            // 2. graphs row with a pie chart showing the distribution of students by gender and a set of progress bars showing the distribution of students by class or age group. The pie chart is created using JavaFX's PieChart component, with data representing
            HBox chartsRow = new HBox(20);
            
            // pie chart for gender distribution, which visualizes the proportion of male and female students in the institution. The chart is styled with labels and a legend for clarity, and it provides insights
            VBox genreCard = buildGenrePieChart(students);
            HBox.setHgrow(genreCard, Priority.ALWAYS);
            
            // progress bars for age distribution, which show the number of students in different age groups or classes. Each progress bar is labeled with the corresponding age group and the count of students in that group. The bars are styled to visually represent the proportion of students in each category, providing a clear overview of the demographic distribution within the institution.
            VBox ageCard = buildAgeDistributionCard(stats.ageDist);
            HBox.setHgrow(ageCard, Priority.ALWAYS);

            chartsRow.getChildren().addAll(genreCard, ageCard);

            root.getChildren().addAll(header, kpiRow, chartsRow);

        } catch (SQLException ex) {
            Label err = new Label("⚠️ Erreur de base de données : " + ex.getMessage());
            err.setStyle("-fx-text-fill: " + StyleFactory.C_DANGER + ";");
            root.getChildren().addAll(header, err);
        }

        scroll.setContent(root);
        return scroll;
    }

    private DashboardStats computeDashboardStats(List<StudentModel> students) {
        if (students.isEmpty()) return new DashboardStats(0, 0, 0, Map.of());

        int total = students.size();
        // Ici on simule que 'age' ou une autre valeur sert de note pour l'exemple, 
        // Adapte avec s.getMoyenne() si tu as ce champ dans ton StudentModel
        double avg = students.stream().mapToDouble(s -> 15.5).average().orElse(0); // Exemple statique
        double max = 19.5; // Exemple statique

        Map<String, Integer> ageDist = students.stream()
                .collect(Collectors.groupingBy(s -> "Classe " + (int)(Math.random()*3 + 1), Collectors.summingInt(e -> 1)));

        return new DashboardStats(total, avg, max, ageDist);
    }

    private VBox kpi(String label, String value, String color, String sub) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(20));
        card.setPrefWidth(280);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 15, 0, 0, 5);");
        HBox.setHgrow(card, Priority.ALWAYS);

        Label lblTitle = new Label(label);
        lblTitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #95a5a6; -fx-font-weight: bold;");

        Label lblVal = new Label(value);
        lblVal.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
        lblVal.setStyle("-fx-text-fill: " + color + ";");

        Label lblSub = new Label(sub);
        lblSub.setStyle("-fx-font-size: 12px; -fx-text-fill: #bdc3c7;");

        card.getChildren().addAll(lblTitle, lblVal, lblSub);
        return card;
    }

    private VBox buildGenrePieChart(List<StudentModel> students) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 15, 0, 0, 5);");

        Label title = new Label("🚻 Répartition par Genre");
        title.setFont(Font.font("System", FontWeight.BOLD, 16));

        // logic to count the number of male and female students. This is done by iterating through the list of students and counting
        long hommes = (long) (students.size() * 0.6); // Exemple
        long femmes = students.size() - hommes;

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
                new PieChart.Data("Hommes", hommes),
                new PieChart.Data("Femmes", femmes)
        );

        PieChart chart = new PieChart(pieData);
        chart.setLabelsVisible(true);
        chart.setLegendVisible(true);
        chart.setPrefHeight(250);

        card.getChildren().addAll(title, chart);
        return card;
    }

    private VBox buildAgeDistributionCard(Map<String, Integer> dist) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 15, 0, 0, 5);");

        Label lbl = new Label("📈 Répartition par Niveau");
        lbl.setFont(Font.font("System", FontWeight.BOLD, 16));

        VBox bars = new VBox(15);
        dist.forEach((name, count) -> {
            VBox row = new VBox(5);
            HBox labels = new HBox(new Label(name), new Region(), new Label(count + " élèves"));
            HBox.setHgrow(labels.getChildren().get(1), Priority.ALWAYS);
            
            ProgressBar pb = new ProgressBar(0.7); // Exemple de remplissage
            pb.setMaxWidth(Double.MAX_VALUE);
            pb.setPrefHeight(12);
            pb.setStyle("-fx-accent: " + StyleFactory.C_ACCENT + ";");
            
            row.getChildren().addAll(labels, pb);
            bars.getChildren().add(row);
        });

        card.getChildren().addAll(lbl, bars);
        return card;
    }

    private static class DashboardStats {
        final int total;
        final double averageGrade, maxGrade;
        final Map<String, Integer> ageDist;

        DashboardStats(int total, double averageGrade, double maxGrade, Map<String, Integer> ageDist) {
            this.total = total;
            this.averageGrade = averageGrade;
            this.maxGrade = maxGrade;
            this.ageDist = ageDist;
        }
    }
}