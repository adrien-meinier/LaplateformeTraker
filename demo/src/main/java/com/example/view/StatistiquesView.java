package com.example.view;

import com.example.controller.StudentDAO;
import com.example.model.StudentModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        try {
            List<StudentModel> students = dao.getAllStudents();
            DashboardStats stats = computeDashboardStats(students);

            // 1. Ligne des KPIs (Cartes de score)
            HBox kpiRow = new HBox(20);
            kpiRow.getChildren().addAll(
                kpi("👥 Effectif Total", String.valueOf(stats.total), StyleFactory.C_ACCENT, "étudiants inscrits"),
                kpi("📊 Moyenne Générale", String.format("%.2f", stats.averageGrade), StyleFactory.C_PRIMARY, "/ 20"),
                kpi("🏆 Major de Promo", String.format("%.2f", stats.maxGrade), StyleFactory.C_SUCCESS, "meilleure note")
            );

            // 2. Ligne des Graphiques
            HBox chartsRow = new HBox(20);
            
            // Graphique Camembert (Répartition par Genre)
            VBox genreCard = buildGenrePieChart(students);
            HBox.setHgrow(genreCard, Priority.ALWAYS);
            
            // Barres de progression (Répartition par Classe ou Âge)
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

        // Logique de comptage
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