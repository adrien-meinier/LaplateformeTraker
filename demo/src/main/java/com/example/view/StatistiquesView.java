package com.example.view;



import com.studentmanager.service.StudentService;
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
import javafx.scene.text.TextAlignment;

import java.util.List;
import java.util.Map;

/**
 * StatistiquesView — tableau de bord des statistiques de la classe.
 *
 * Affiche des KPI cards, un mini-diagramme en barres (distribution des notes)
 * et la répartition par tranche d'âge.
 */
public class StatistiquesView {

    private final StudentService service;

    public StatistiquesView(StudentService service) {
        this.service = service;
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
            Map<String, Object> stats = service.getStatistics();

            int    total    = (int)    stats.get("Nombre d'étudiants");
            double avgGrade = (double) stats.get("Moyenne des notes");
            double minGrade = (double) stats.get("Note minimale");
            double maxGrade = (double) stats.get("Note maximale");
            double stddev   = (double) stats.get("Écart-type des notes");
            double avgAge   = (double) stats.get("Âge moyen");
            int    passing  = (int)    stats.get("Étudiants admis (≥10)");
            int    failing  = (int)    stats.get("Étudiants refusés (<10)");

            Map<String, Integer> ageDist =
                    (Map<String, Integer>) stats.get("Distribution par âge");

            // ── KPI Cards ────────────────────────────────────────────────
            HBox kpiRow = new HBox(14);
            kpiRow.setFillHeight(true);

            kpiRow.getChildren().addAll(
                    kpi("👥 Étudiants",   String.valueOf(total),
                            StyleFactory.C_ACCENT,   "au total"),
                    kpi("📈 Moyenne",     String.format("%.2f / 20", avgGrade),
                            StyleFactory.C_PRIMARY,  avgGrade >= 10 ? "Classe admise" : "Classe en difficulté"),
                    kpi("✅ Admis",       String.valueOf(passing),
                            StyleFactory.C_SUCCESS,  total > 0
                                    ? String.format("%.0f%%", passing * 100.0 / total) : "—"),
                    kpi("❌ Non admis",   String.valueOf(failing),
                            StyleFactory.C_DANGER,   total > 0
                                    ? String.format("%.0f%%", failing * 100.0 / total) : "—")
            );

            // ── Ligne détails ─────────────────────────────────────────────
            HBox detailRow = new HBox(14);

            detailRow.getChildren().addAll(
                    kpi("⬇ Note min",     String.format("%.2f", minGrade),
                            StyleFactory.C_DANGER,  ""),
                    kpi("⬆ Note max",     String.format("%.2f", maxGrade),
                            StyleFactory.C_SUCCESS, ""),
                    kpi("📐 Écart-type",  String.format("%.2f", stddev),
                            StyleFactory.C_WARNING, "dispersion"),
                    kpi("🎂 Âge moyen",   String.format("%.1f ans", avgAge),
                            StyleFactory.C_PRIMARY, "")
            );

            // ── Diagramme admis/refusés ───────────────────────────────────
            HBox charts = new HBox(16);
            charts.setFillHeight(true);

            VBox donutCard = buildDonutCard(passing, failing, total);
            VBox ageCard   = buildAgeBarChart(ageDist);

            HBox.setHgrow(donutCard, Priority.ALWAYS);
            HBox.setHgrow(ageCard,   Priority.ALWAYS);
            charts.getChildren().addAll(donutCard, ageCard);

            root.getChildren().addAll(title, kpiRow, detailRow, charts);

        } catch (Exception ex) {
            Label err = new Label("Erreur lors du chargement des statistiques : " + ex.getMessage());
            err.setStyle("-fx-text-fill: " + StyleFactory.C_DANGER + ";");
            root.getChildren().addAll(title, err);
        }

        scroll.setContent(root);
        return scroll;
    }

    // ── KPI card ──────────────────────────────────────────────────────────

    private VBox kpi(String label, String value, String color, String sub) {
        VBox card = new VBox(4);
        card.setPadding(new Insets(18, 20, 18, 20));
        card.setStyle("-fx-background-color: white;"
                + "-fx-background-radius: 10;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.07), 8, 0, 0, 2);");
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

    // ── Donut admis/refusés ───────────────────────────────────────────────

    private VBox buildDonutCard(int passing, int failing, int total) {
        VBox card = new VBox(14);
        card.setPadding(new Insets(18, 20, 18, 20));
        card.setStyle("-fx-background-color: white;"
                + "-fx-background-radius: 10;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.07), 8, 0, 0, 2);");
        card.setAlignment(Pos.TOP_LEFT);

        Label lbl = new Label("Répartition admis / non admis");
        lbl.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;"
                + "-fx-text-fill: " + StyleFactory.C_PRIMARY + ";");

        // Dessin manuel donut
        Pane canvas = new Pane();
        canvas.setPrefSize(200, 200);
        canvas.setMaxSize(200, 200);

        double cx = 100, cy = 100, r = 70, stroke = 24;
        double passDeg = total > 0 ? (passing * 360.0 / total) : 0;
        double failDeg = 360.0 - passDeg;

        // Arc vert (admis)
        Arc arcPass = new Arc(cx, cy, r, r, 90, -passDeg);
        arcPass.setType(ArcType.OPEN);
        arcPass.setFill(Color.TRANSPARENT);
        arcPass.setStroke(Color.web(StyleFactory.C_SUCCESS));
        arcPass.setStrokeWidth(stroke);

        // Arc rouge (refusés)
        Arc arcFail = new Arc(cx, cy, r, r, 90 - passDeg, -failDeg);
        arcFail.setType(ArcType.OPEN);
        arcFail.setFill(Color.TRANSPARENT);
        arcFail.setStroke(Color.web(StyleFactory.C_DANGER));
        arcFail.setStrokeWidth(stroke);

        // Texte centre
        Text center = new Text(String.format("%.0f%%",
                total > 0 ? passing * 100.0 / total : 0));
        center.setFont(Font.font("System", FontWeight.BOLD, 22));
        center.setFill(Color.web(StyleFactory.C_SUCCESS));
        center.setX(cx - 18);
        center.setY(cy + 8);

        Text centerSub = new Text("admis");
        centerSub.setFont(Font.font(11));
        centerSub.setFill(Color.GRAY);
        centerSub.setX(cx - 14);
        centerSub.setY(cy + 22);

        canvas.getChildren().addAll(arcPass, arcFail, center, centerSub);

        // Légende
        HBox legend = new HBox(16);
        legend.setAlignment(Pos.CENTER);
        legend.getChildren().addAll(
                legendItem(StyleFactory.C_SUCCESS, "Admis ("    + passing + ")"),
                legendItem(StyleFactory.C_DANGER,  "Non admis (" + failing + ")")
        );

        card.getChildren().addAll(lbl, canvas, legend);
        return card;
    }

    private HBox legendItem(String color, String text) {
        Rectangle rect = new Rectangle(12, 12, Color.web(color));
        rect.setArcWidth(3); rect.setArcHeight(3);
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-size: 12px;");
        HBox b = new HBox(6, rect, lbl);
        b.setAlignment(Pos.CENTER_LEFT);
        return b;
    }

    // ── Barres distribution par âge ───────────────────────────────────────

    private VBox buildAgeBarChart(Map<String, Integer> ageDist) {
        VBox card = new VBox(14);
        card.setPadding(new Insets(18, 20, 18, 20));
        card.setStyle("-fx-background-color: white;"
                + "-fx-background-radius: 10;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.07), 8, 0, 0, 2);");

        Label lbl = new Label("Répartition par tranche d'âge");
        lbl.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;"
                + "-fx-text-fill: " + StyleFactory.C_PRIMARY + ";");

        int maxVal = ageDist.values().stream().mapToInt(Integer::intValue).max().orElse(1);

        VBox bars = new VBox(10);
        String[] colors = {StyleFactory.C_ACCENT, StyleFactory.C_SUCCESS,
                           StyleFactory.C_WARNING, StyleFactory.C_DANGER};
        int ci = 0;
        for (Map.Entry<String, Integer> e : ageDist.entrySet()) {
            String  key = e.getKey();
            int     val = e.getValue();
            double  pct = (double) val / maxVal;
            String  col = colors[ci % colors.length];
            ci++;

            Label lblKey = new Label(key);
            lblKey.setMinWidth(50);
            lblKey.setStyle("-fx-font-size: 12px; -fx-text-fill: " + StyleFactory.C_TEXT_DARK + ";");

            StackPane barBg = new StackPane();
            barBg.setStyle("-fx-background-color: " + StyleFactory.C_LIGHT + ";"
                    + "-fx-background-radius: 4;");
            barBg.setMaxHeight(18); barBg.setPrefHeight(18);
            HBox.setHgrow(barBg, Priority.ALWAYS);

            StackPane barFill = new StackPane();
            barFill.setStyle("-fx-background-color: " + col + ";"
                    + "-fx-background-radius: 4;");
            barFill.setPrefWidth(pct * 200);
            barFill.setMaxWidth(pct * 200);
            barFill.setPrefHeight(18);
            barBg.getChildren().add(barFill);
            StackPane.setAlignment(barFill, Pos.CENTER_LEFT);

            Label lblVal = new Label(String.valueOf(val));
            lblVal.setMinWidth(25);
            lblVal.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;"
                    + "-fx-text-fill: " + col + ";");

            HBox row = new HBox(10, lblKey, barBg, lblVal);
            row.setAlignment(Pos.CENTER_LEFT);
            bars.getChildren().add(row);
        }

        card.getChildren().addAll(lbl, bars);
        return card;
    }
}