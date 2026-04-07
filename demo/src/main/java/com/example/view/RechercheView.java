package com.example.view;


import com.studentmanager.model.Student;
import com.studentmanager.service.StudentService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

/**
 * RechercheView — recherche simple par ID et recherche avancée multi-critères.
 */
public class RechercheView {

    private final StudentService service;

    private TableView<Student> table;
    private Label              lblResultCount;

    public RechercheView(StudentService service) {
        this.service = service;
    }

    public Node build() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(0));

        // Titre
        Label title = new Label("🔎 Recherche");
        title.setFont(Font.font("System", FontWeight.BOLD, 22));
        title.setStyle(StyleFactory.titleStyle());

        // ── Bloc Recherche par ID ──────────────────────────────────────
        VBox cardById = new VBox(12);
        cardById.setPadding(new Insets(18, 20, 18, 20));
        cardById.setStyle(StyleFactory.cardBg());

        Label lblById = new Label("Recherche par ID");
        lblById.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;"
                + "-fx-text-fill: " + StyleFactory.C_PRIMARY + ";");

        HBox rowId = new HBox(10);
        rowId.setAlignment(Pos.CENTER_LEFT);
        TextField tfId = new TextField();
        tfId.setPromptText("Entrez l'ID de l'étudiant");
        tfId.setStyle(StyleFactory.textFieldStyle());
        tfId.setPrefHeight(40);
        tfId.setPrefWidth(220);
        tfId.focusedProperty().addListener((obs, o, f) ->
                tfId.setStyle(f ? StyleFactory.textFieldFocusStyle() : StyleFactory.textFieldStyle()));

        Button btnSearchId = StyleFactory.primaryBtn("🔍 Rechercher");
        btnSearchId.setOnAction(e -> searchById(tfId.getText().trim()));
        tfId.setOnAction(e -> searchById(tfId.getText().trim()));

        rowId.getChildren().addAll(tfId, btnSearchId);
        cardById.getChildren().addAll(lblById, rowId);

        // ── Bloc Recherche avancée ──────────────────────────────────────
        VBox cardAdv = new VBox(14);
        cardAdv.setPadding(new Insets(18, 20, 18, 20));
        cardAdv.setStyle(StyleFactory.cardBg());

        Label lblAdv = new Label("Recherche avancée");
        lblAdv.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;"
                + "-fx-text-fill: " + StyleFactory.C_PRIMARY + ";");

        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(10);

        TextField tfKeyword = advField("Mot-clé (nom / prénom)");
        TextField tfMinAge  = advField("Âge min");
        TextField tfMaxAge  = advField("Âge max");
        TextField tfMinNote = advField("Note min (0-20)");
        TextField tfMaxNote = advField("Note max (0-20)");

        grid.add(fieldGroup("Mot-clé",   tfKeyword), 0, 0, 2, 1);
        grid.add(fieldGroup("Âge min",   tfMinAge),  0, 1);
        grid.add(fieldGroup("Âge max",   tfMaxAge),  1, 1);
        grid.add(fieldGroup("Note min",  tfMinNote), 0, 2);
        grid.add(fieldGroup("Note max",  tfMaxNote), 1, 2);

        ColumnConstraints c1 = new ColumnConstraints(); c1.setPercentWidth(50);
        ColumnConstraints c2 = new ColumnConstraints(); c2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(c1, c2);

        HBox advButtons = new HBox(10);
        advButtons.setAlignment(Pos.CENTER_RIGHT);

        Button btnReset  = StyleFactory.secondaryBtn("🔄 Réinitialiser");
        Button btnSearch = StyleFactory.primaryBtn("🔍 Rechercher");

        btnReset.setOnAction(e -> {
            tfKeyword.clear(); tfMinAge.clear(); tfMaxAge.clear();
            tfMinNote.clear(); tfMaxNote.clear();
            refreshAll();
        });
        btnSearch.setOnAction(e -> advancedSearch(
                tfKeyword.getText().trim(),
                parseIntOrNull(tfMinAge.getText()),
                parseIntOrNull(tfMaxAge.getText()),
                parseDblOrNull(tfMinNote.getText()),
                parseDblOrNull(tfMaxNote.getText())
        ));

        advButtons.getChildren().addAll(btnReset, btnSearch);
        cardAdv.getChildren().addAll(lblAdv, grid, advButtons);

        // ── Résultats ──────────────────────────────────────────────────
        lblResultCount = new Label();
        lblResultCount.setStyle(StyleFactory.subtitleStyle());

        table = buildTable();

        root.getChildren().addAll(title, cardById, cardAdv, lblResultCount, table);
        VBox.setVgrow(table, Priority.ALWAYS);

        refreshAll();
        return root;
    }

    // ── Actions ───────────────────────────────────────────────────────────

    private void searchById(String raw) {
        if (raw.isEmpty()) { refreshAll(); return; }
        try {
            int id = Integer.parseInt(raw);
            service.findById(id).ifPresentOrElse(
                    s -> {
                        table.setItems(FXCollections.observableArrayList(s));
                        lblResultCount.setText("1 résultat.");
                    },
                    () -> {
                        table.setItems(FXCollections.emptyObservableList());
                        lblResultCount.setText("Aucun étudiant avec l'ID " + id);
                    }
            );
        } catch (NumberFormatException e) {
            showAlert("L'ID doit être un nombre entier.");
        } catch (Exception ex) {
            showAlert("Erreur : " + ex.getMessage());
        }
    }

    private void advancedSearch(String keyword, Integer minAge, Integer maxAge,
                                Double minNote, Double maxNote) {
        try {
            List<Student> results = service.advancedSearch(
                    minAge, maxAge, minNote, maxNote,
                    keyword.isEmpty() ? null : keyword);
            table.setItems(FXCollections.observableArrayList(results));
            lblResultCount.setText(results.size() + " résultat(s) trouvé(s).");
        } catch (Exception ex) {
            showAlert("Erreur recherche : " + ex.getMessage());
        }
    }

    private void refreshAll() {
        try {
            List<Student> all = service.getAllStudents("id", true);
            table.setItems(FXCollections.observableArrayList(all));
            lblResultCount.setText(all.size() + " étudiant(s) au total.");
        } catch (Exception ex) {
            showAlert("Erreur : " + ex.getMessage());
        }
    }

    // ── Tableau ───────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private TableView<Student> buildTable() {
        TableView<Student> tv = new TableView<>();
        tv.setStyle(StyleFactory.tableStyle());
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tv.setPlaceholder(new Label("Aucun résultat."));

        TableColumn<Student, Integer> colId    = simpleCol("ID",     "id",        60);
        TableColumn<Student, String>  colPrenom= simpleCol("Prénom", "firstName", 140);
        TableColumn<Student, String>  colNom   = simpleCol("Nom",    "lastName",  140);
        TableColumn<Student, Integer> colAge   = simpleCol("Âge",    "age",       70);

        TableColumn<Student, String> colNote = new TableColumn<>("Note");
        colNote.setCellValueFactory(d ->
                new SimpleStringProperty(String.format("%.2f / 20", d.getValue().getGrade())));
        colNote.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                Student s = getTableView().getItems().get(getIndex());
                setStyle(s.getGrade() >= 10
                        ? "-fx-text-fill:" + StyleFactory.C_SUCCESS + ";-fx-font-weight:bold;"
                        : "-fx-text-fill:" + StyleFactory.C_DANGER  + ";-fx-font-weight:bold;");
            }
        });
        colNote.setPrefWidth(120);

        TableColumn<Student, String> colMention = new TableColumn<>("Mention");
        colMention.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getMention()));
        colMention.setPrefWidth(110);

        TableColumn<Student, String> colEmail = simpleCol("Email", "email", 200);

        tv.getColumns().addAll(colId, colPrenom, colNom, colAge, colNote, colMention, colEmail);
        return tv;
    }

    private <T> TableColumn<Student, T> simpleCol(String header, String prop, int w) {
        TableColumn<Student, T> c = new TableColumn<>(header);
        c.setCellValueFactory(new PropertyValueFactory<>(prop));
        c.setPrefWidth(w);
        return c;
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private TextField advField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle(StyleFactory.textFieldStyle());
        tf.setPrefHeight(38);
        tf.focusedProperty().addListener((obs, o, f) ->
                tf.setStyle(f ? StyleFactory.textFieldFocusStyle() : StyleFactory.textFieldStyle()));
        return tf;
    }

    private VBox fieldGroup(String label, Node ctrl) {
        Label lbl = new Label(label);
        lbl.setStyle(StyleFactory.labelStyle());
        VBox b = new VBox(4, lbl, ctrl);
        b.setFillWidth(true);
        return b;
    }

    private Integer parseIntOrNull(String s) {
        try { return s.isEmpty() ? null : Integer.parseInt(s.trim()); }
        catch (NumberFormatException e) { return null; }
    }

    private Double parseDblOrNull(String s) {
        try { return s.isEmpty() ? null : Double.parseDouble(s.trim().replace(',','.')); }
        catch (NumberFormatException e) { return null; }
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        a.setHeaderText(null); a.showAndWait();
    }
}
