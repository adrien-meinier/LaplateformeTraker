package com.example.view;

import com.example.controller.StudentDAO;
import com.example.model.StudentModel;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * StatistiquesView — Tableau de bord des statistiques.
 * Nettoyé de toute logique de hachage/pepper.
 */
public class StatistiquesView {

    private final StudentDAO dao;

    public StatistiquesView(StudentDAO dao) {
        this.dao = dao;
    }

    public Node build() {
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        VBox root = new VBox(20);
        root.setPadding(new Insets(20));

        // Titre
        Label title = new Label("📊 Statistiques de la classe");
        title.setFont(Font.font("System", FontWeight.BOLD, 22));
        title.setStyle("-fx-text-fill: " + StyleFactory.C_PRIMARY + ";");

        try {
            List<StudentModel> students = dao.getAllStudents();
            Stats stats = computeStats(students);

            // ── Cartes KPI (Indicateurs clés)
            HBox kpiRow = new HBox(14);
            kpiRow.setFillHeight(true);

            kpiRow.getChildren().addAll(
                    kpi("👥 Étudiants", String.valueOf(stats.total), StyleFactory.C_ACCENT, "au total"),
                    kpi("Moyenne des Étudiants", String.format("%.1f", stats.avgAge), StyleFactory.C_PRIMARY, ""),
                    kpi("Note minimale", String.format("%d", stats.minAge), StyleFactory.C_SUCCESS, ""),
                    kpi("Note maximale", String.format("%d", stats.maxAge), StyleFactory.C_WARNING, "")
            );

            // ── Graphique de répartition
            HBox charts = new HBox(16);
            charts.setFillHeight(true);

            VBox ageCard = buildAgeBarChart(stats.ageDist);
            HBox.setHgrow(ageCard, Priority.ALWAYS);
            charts.getChildren().add(ageCard);

            root.getChildren().addAll(title, kpiRow, charts);

        } catch (SQLException ex) {
            Label err = new Label("Erreur lors du chargement des statistiques : " + ex.getMessage());
            err.setStyle("-fx-text-fill: " + StyleFactory.C_DANGER + ";");
            root.getChildren().addAll(title, err);
        }

        scroll.setContent(root);
        return scroll;
    }

    /**
     * Calcule les statistiques à partir de la liste des étudiants.
     */
    private Stats computeStats(List<StudentModel> students) {
        int total = students.size();
        
        if (total == 0) {
            return new Stats(0, 0, 0, 0, Map.of());
        }

        List<Integer> ages = students.stream()
                .filter(s -> s.getBirthDate() != null)
                .map(s -> Period.between(s.getBirthDate(), LocalDate.now()).getYears())
                .collect(Collectors.toList());

        if (ages.isEmpty()) return new Stats(total, 0, 0, 0, Map.of());

        int minAge = ages.stream().mapToInt(Integer::intValue).min().orElse(0);
        int maxAge = ages.stream().mapToInt(Integer::intValue).max().orElse(0);
        double avgAge = ages.stream().mapToInt(Integer::intValue).average().orElse(0);

        // Répartition par tranches
        Map<String, Integer> ageDist = ages.stream()
                .collect(Collectors.groupingBy(
                    age -> {
                        if (age < 18) return "< 18 ans";
                        if (age < 25) return "18-24 ans";
                        if (age < 30) return "25-29 ans";
                        return "30 ans +";
                    },
                    Collectors.summingInt(e -> 1)
                ));

        return new Stats(total, avgAge, minAge, maxAge, ageDist);
    }

    private VBox kpi(String label, String value, String color, String sub) {
        VBox card = new VBox(4);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        card.setMinWidth(150);
        HBox.setHgrow(card, Priority.ALWAYS);

        Label lblTitle = new Label(label);
        lblTitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #7f8c8d;");

        Label lblVal = new Label(value);
        lblVal.setFont(Font.font("System", FontWeight.BOLD, 24));
        lblVal.setStyle("-fx-text-fill: " + color + ";");

        Label lblSub = new Label(sub);
        lblSub.setStyle("-fx-font-size: 11px; -fx-text-fill: #95a5a6;");

        card.getChildren().addAll(lblTitle, lblVal, lblSub);
        return card;
    }

    private VBox buildAgeBarChart(Map<String, Integer> ageDist) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        Label lbl = new Label("Répartition par tranches d'âge");
        lbl.setFont(Font.font("System", FontWeight.BOLD, 16));

        VBox barsContainer = new VBox(12);
        
        int totalStudents = ageDist.values().stream().mapToInt(Integer::intValue).sum();
        if (totalStudents == 0) totalStudents = 1;

        String[] colors = {StyleFactory.C_PRIMARY, StyleFactory.C_SUCCESS, StyleFactory.C_WARNING, StyleFactory.C_DANGER};
        int i = 0;

        for (Map.Entry<String, Integer> entry : ageDist.entrySet()) {
            double progress = (double) entry.getValue() / totalStudents;
            String color = colors[i % colors.length];

            VBox row = new VBox(5);
            HBox labels = new HBox();
            Label name = new Label(entry.getKey());
            Label count = new Label(entry.getValue() + " étudiant(s)");
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            labels.getChildren().addAll(name, spacer, count);

            ProgressBar pb = new ProgressBar(progress);
            pb.setMaxWidth(Double.MAX_VALUE);
            pb.setPrefHeight(15);
            // On applique la couleur via CSS inline
            pb.setStyle("-fx-accent: " + color + ";");

            row.getChildren().addAll(labels, pb);
            barsContainer.getChildren().add(row);
            i++;
        }

        card.getChildren().addAll(lbl, barsContainer);
        return card;
    }

    /**
     * Structure de données pour stocker les calculs.
     */
    private static class Stats {
        final int total, minAge, maxAge;
        final double avgAge;
        final Map<String, Integer> ageDist;

        Stats(int total, double avgAge, int minAge, int maxAge, Map<String, Integer> ageDist) {
            this.total = total;
            this.avgAge = avgAge;
            this.minAge = minAge;
            this.maxAge = maxAge;
            this.ageDist = ageDist;
        }
    }
}