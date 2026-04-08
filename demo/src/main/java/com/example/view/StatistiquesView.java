package com.example.view;

import com.example.controller.StudentDAO;
import com.example.model.StudentModel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

/**
 * StatistiquesView — tableau de bord des statistiques de la classe.
 */
public class StatistiquesView {

    private final StudentDAO dao;

    public StatistiquesView(StudentDAO dao) {
        this.dao = dao;
    }

    @SuppressWarnings("unchecked")
    public Node build() {
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        VBox root = new VBox(20);
        root.setPadding(new Insets(0, 0, 20, 0));

        // Titre
        Label title = new Label("📊 Statistiques");
        title.setFont(Font.font("System", FontWeight.BOLD, 22));
        title.setStyle(StyleFactory.titleStyle());

        try {
            List<StudentModel> students = dao.getAllStudents();
            Stats stats = computeStats(students);

            // ── KPI Cards 
            HBox kpiRow = new HBox(14);
            kpiRow.setFillHeight(true);

            kpiRow.getChildren().addAll(
                    kpi("👥 Étudiants", String.valueOf(stats.total), StyleFactory.C_ACCENT, "au total"),
                    kpi("🎂 Âge moyen", String.format("%.1f ans", stats.avgAge), StyleFactory.C_PRIMARY, ""),
                    kpi("👶 Plus jeune", String.format("%d ans", stats.minAge), StyleFactory.C_SUCCESS, ""),
                    kpi("👴 Plus âgé", String.format("%d ans", stats.maxAge), StyleFactory.C_WARNING, "")
            );

            // ── Diagramme répartition par âge
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

    private Stats computeStats(List<StudentModel> students) {
        int total = students.size();
        
        if (total == 0) {
            return new Stats(0, 0, 0, 0, Map.of());
        }

        List<Integer> ages = students.stream()
                .map(s -> Period.between(s.getBirthDate(), LocalDate.now()).getYears())
                .collect(Collectors.toList());

        int minAge = ages.stream().mapToInt(Integer::intValue).min().orElse(0);
        int maxAge = ages.stream().mapToInt(Integer::intValue).max().orElse(0);
        double avgAge = ages.stream().mapToInt(Integer::intValue).average().orElse(0);

        // Distribution par tranches d'âge
        var ageDist = ages.stream()
                .collect(Collectors.groupingBy(
                    age -> {
                        if (age < 18) return "<18";
                        if (age < 25) return "18-24";
                        if (age < 30) return "25-29";
                        return "30+";
                    },
                    Collectors.summingInt(e -> 1)
                ));

        return new Stats(total, avgAge, minAge, maxAge, ageDist);
    }

    // ── KPI card 
    private VBox kpi(String label, String value, String color, String sub) {
        VBox card = new VBox(4);
        card.setPadding(new Insets(18, 20, 18, 20));
        card.setStyle("-fx-background-color: white;" +
                "-fx-background-radius: 10;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.07), 8, 0, 0, 2);");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setMinWidth(160);
        HBox.setHgrow(card, Priority.ALWAYS);

        Label lblTitle = new Label(label);
        lblTitle.setStyle("-fx-font-size: 12px; -fx-text-fill: " + StyleFactory.C_TEXT_GREY + ";");

        Label lblVal = new Label(value);
        lblVal.setFont(Font.font("System", FontWeight.BOLD, 26));
        lblVal.setStyle("-fx-text-fill: " + color + ";");

        Label lblSub = new Label(sub);
        lblSub.setStyle("-fx-font-size: 11px; -fx-text-fill: " + StyleFactory.C_TEXT_GREY + ";");

        card.getChildren().addAll(lblTitle, lblVal, lblSub);
        return card;
    }

    // ── Barres distribution par âge 
    private VBox buildAgeBarChart(java.util.Map<String, Integer> ageDist) {
        VBox card = new VBox(14);
        card.setPadding(new Insets(18, 20, 18, 20));
        card.setStyle("-fx-background-color: white;" +
                "-fx-background-radius: 10;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.07), 8, 0, 0, 2);");

        Label lbl = new Label("Répartition par tranche d'âge");
        lbl.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;" +
                "-fx-text-fill: " + StyleFactory.C_PRIMARY + ";");

        int maxVal = ageDist.values().stream().mapToInt(Integer::intValue).max().orElse(1);

        VBox bars = new VBox(10);
        String[] colors = {StyleFactory.C_ACCENT, StyleFactory.C_SUCCESS,
                          StyleFactory.C_WARNING, StyleFactory.C_DANGER};
        int ci = 0;
        for (var e : ageDist.entrySet()) {
            String key = e.getKey();
            int val = e.getValue();
            double pct = (double) val / maxVal;
            String col = colors[ci % colors.length];
            ci++;

            Label lblKey = new Label(key);
            lblKey.setMinWidth(50);
            lblKey.setStyle("-fx-font-size: 12px; -fx-text-fill: " + StyleFactory.C_TEXT_DARK + ";");

            StackPane barBg = new StackPane();
            barBg.setStyle("-fx-background-color: " + StyleFactory.C_LIGHT + ";" +
                    "-fx-background-radius: 4;");
            barBg.setMaxHeight(18); barBg.setPrefHeight(18);
            HBox.setHgrow(barBg, Priority.ALWAYS);

            StackPane barFill = new StackPane();
            barFill.setStyle("-fx-background-color: " + col + ";" +
                    "-fx-background-radius: 4;");
            barFill.setPrefWidth(pct * 200);
            barFill.setMaxWidth(pct * 200);
            barFill.setPrefHeight(18);
            barBg.getChildren().add(barFill);
            StackPane.setAlignment(barFill, Pos.CENTER_LEFT);

            Label lblVal = new Label(String.valueOf(val));
            lblVal.setMinWidth(25);
            lblVal.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;" +
                    "-fx-text-fill: " + col + ";");

            HBox row = new HBox(10, lblKey, barBg, lblVal);
            row.setAlignment(Pos.CENTER_LEFT);
            bars.getChildren().add(row);
        }

        card.getChildren().addAll(lbl, bars);
        return card;
    }

    // Classe interne pour les stats
    private static class Stats {
        final int total, minAge, maxAge;
        final double avgAge;
        final java.util.Map<String, Integer> ageDist;

        Stats(int total, double avgAge, int minAge, int maxAge, 
              java.util.Map<String, Integer> ageDist) {
            this.total = total;
            this.avgAge = avgAge;
            this.minAge = minAge;
            this.maxAge = maxAge;
            this.ageDist = ageDist;
        }
    }
}