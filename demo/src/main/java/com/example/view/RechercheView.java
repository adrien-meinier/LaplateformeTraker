package com.example.view;

import com.example.controller.StudentDAO;
import com.example.model.StudentModel;
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

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

/**
 * RechercheView — recherche simple par ID et recherche avancée multi-critères.
 */
public class RechercheView {

    private final StudentDAO dao;
    private List<StudentModel> allStudents;

    private TableView<StudentModel> table;
    private Label lblResultCount;

    public RechercheView(StudentDAO dao) {
        this.dao = dao;
    }

    public Node build() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(0));

        // Titre
        Label title = new Label("🔎 Recherche");
        title.setFont(Font.font("System", FontWeight.BOLD, 22));
        title.setStyle(StyleFactory.titleStyle());

        // ── Bloc Recherche par ID 
        VBox cardById = new VBox(12);
        cardById.setPadding(new Insets(18, 20, 18, 20));
        cardById.setStyle(StyleFactory.cardBg());

        Label lblById = new Label("Recherche par ID");
        lblById.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;" +
                "-fx-text-fill: " + StyleFactory.C_PRIMARY + ";");

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

        // ── Bloc Recherche avancée 
        VBox cardAdv = new VBox(14);
        cardAdv.setPadding(new Insets(18, 20, 18, 20));
        cardAdv.setStyle(StyleFactory.cardBg());

        Label lblAdv = new Label("Recherche avancée");
        lblAdv.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;" +
                "-fx-text-fill: " + StyleFactory.C_PRIMARY + ";");

        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(10);

        TextField tfKeyword = advField("Mot-clé (nom / prénom)");
        TextField tfMinAge = advField("Âge min");
        TextField tfMaxAge = advField("Âge max");

        grid.add(fieldGroup("Mot-clé", tfKeyword), 0, 0, 2, 1);
        grid.add(fieldGroup("Âge min", tfMinAge), 0, 1);
        grid.add(fieldGroup("Âge max", tfMaxAge), 1, 1);

        ColumnConstraints c1 = new ColumnConstraints(); c1.setPercentWidth(50);
        ColumnConstraints c2 = new ColumnConstraints(); c2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(c1, c2);

        HBox advButtons = new HBox(10);
        advButtons.setAlignment(Pos.CENTER_RIGHT);

        Button btnReset = StyleFactory.secondaryBtn("🔄 Réinitialiser");
        Button btnSearch = StyleFactory.primaryBtn("🔍 Rechercher");

        btnReset.setOnAction(e -> {
            tfKeyword.clear(); tfMinAge.clear(); tfMaxAge.clear();
            refreshAll();
        });
        btnSearch.setOnAction(e -> advancedSearch(
                tfKeyword.getText().trim(),
                parseIntOrNull(tfMinAge.getText()),
                parseIntOrNull(tfMaxAge.getText())
        ));

        advButtons.getChildren().addAll(btnReset, btnSearch);
        cardAdv.getChildren().addAll(lblAdv, grid, advButtons);

        // ── Résultats 
        lblResultCount = new Label();
        lblResultCount.setStyle(StyleFactory.subtitleStyle());

        table = buildTable();

        root.getChildren().addAll(title, cardById, cardAdv, lblResultCount, table);
        VBox.setVgrow(table, Priority.ALWAYS);

        refreshAll();
        return root;
    }

    // ── Actions 
    private void searchById(String raw) {
        if (raw.isEmpty()) { refreshAll(); return; }
        try {
            int id = Integer.parseInt(raw);
            var result = allStudents.stream()
                    .filter(s -> s.getId() == id)
                    .collect(Collectors.toList());
            
            table.setItems(FXCollections.observableArrayList(result));
            lblResultCount.setText(result.size() == 1 ? "1 résultat." : 
                                 result.isEmpty() ? "Aucun étudiant avec l'ID " + id : 
                                 result.size() + " résultat(s).");
        } catch (NumberFormatException e) {
            showAlert("L'ID doit être un nombre entier.");
        }
    }

    private void advancedSearch(String keyword, Integer minAge, Integer maxAge) {
        var results = allStudents.stream()
                .filter(s -> matchesKeyword(s, keyword))
                .filter(s -> matchesAge(s, minAge, maxAge))
                .collect(Collectors.toList());
        
        table.setItems(FXCollections.observableArrayList(results));
        lblResultCount.setText(results.size() + " résultat(s) trouvé(s).");
    }

    private boolean matchesKeyword(StudentModel s, String keyword) {
        if (keyword.isEmpty()) return true;
        String lower = keyword.toLowerCase();
        return s.getFirstName().toLowerCase().contains(lower) ||
               s.getLastName().toLowerCase().contains(lower);
    }

    private boolean matchesAge(StudentModel s, Integer minAge, Integer maxAge) {
        int age = Period.between(s.getBirthDate(), LocalDate.now()).getYears();
        if (minAge != null && age < minAge) return false;
        if (maxAge != null && age > maxAge) return false;
        return true;
    }

    private void refreshAll() {
        try {
            allStudents = dao.getAllStudents();
            table.setItems(FXCollections.observableArrayList(allStudents));
            lblResultCount.setText(allStudents.size() + " étudiant(s) au total.");
        } catch (Exception ex) {
            showAlert("Erreur : " + ex.getMessage());
        }
    }

    // ── Tableau 
    @SuppressWarnings("unchecked")
    private TableView<StudentModel> buildTable() {
        TableView<StudentModel> tv = new TableView<>();
        tv.setStyle(StyleFactory.tableStyle());
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tv.setPlaceholder(new Label("Aucun résultat."));

        TableColumn<StudentModel, Integer> colId = simpleCol("ID", "id", 60);
        TableColumn<StudentModel, String> colPrenom = simpleCol("Prénom", "firstName", 140);
        TableColumn<StudentModel, String> colNom = simpleCol("Nom", "lastName", 140);
        TableColumn<StudentModel, Integer> colAge = new TableColumn<>("Âge");
        
        colAge.setCellValueFactory(cellData -> {
        int age = Period.between(cellData.getValue().getBirthDate(), LocalDate.now()).getYears();
        return new javafx.beans.property.SimpleIntegerProperty(age).asObject();
     });

        TableColumn<StudentModel, String> colBirth = simpleCol("Naissance", "birthDate", 120);

        tv.getColumns().addAll(colId, colPrenom, colNom, colAge, colBirth);
        return tv;
    }

    private <T> TableColumn<StudentModel, T> simpleCol(String header, String prop, int w) {
        TableColumn<StudentModel, T> c = new TableColumn<>(header);
        c.setCellValueFactory(new PropertyValueFactory<>(prop));
        c.setPrefWidth(w);
        return c;
    }

    // ── Helpers 
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

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        a.setHeaderText(null); a.showAndWait();
    }
}