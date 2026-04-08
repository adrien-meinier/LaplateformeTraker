package com.example.view;



import com.example.Model.StudentModel;
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
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

/**
 * EtudiantsView — tableau paginé de tous les étudiants.
 *
 * Permet de visualiser, modifier et supprimer les étudiants.
 * Le tri se fait en cliquant sur les en-têtes de colonne.
 */
public class EtudiantsView {

    private final StudentService service;

    // Pagination
    private int currentPage = 1;
    private int pageSize    = 10;
    private String sortBy   = "id";
    private boolean asc     = true;

    // UI state
    private TableView<Student> table;
    private Label lblPagination;
    private Button btnPrev, btnNext;

    public EtudiantsView(StudentService service) {
        this.service = service;
    }

    public Node build() {
        VBox root = new VBox(16);
        root.setPadding(new Insets(0));

        // ── En-tête 
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("👨‍🎓 Étudiants");
        title.setStyle(StyleFactory.titleStyle());
        title.setFont(Font.font("System", FontWeight.BOLD, 22));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Sélecteur nombre de lignes
        Label lblPage = new Label("Par page :");
        lblPage.setStyle(StyleFactory.subtitleStyle());
        ComboBox<Integer> cbPageSize = new ComboBox<>(
                FXCollections.observableArrayList(5, 10, 20, 50));
        cbPageSize.setValue(pageSize);
        cbPageSize.setPrefWidth(80);
        cbPageSize.setOnAction(e -> {
            pageSize = cbPageSize.getValue();
            currentPage = 1;
            refresh();
        });

        Button btnAdd = StyleFactory.successBtn("➕ Ajouter");
        btnAdd.setOnAction(e -> openForm(null));

        header.getChildren().addAll(title, spacer, lblPage, cbPageSize, btnAdd);

        // ── Tableau 
        table = buildTable();

        // ── Pagination 
        HBox pagination = new HBox(12);
        pagination.setAlignment(Pos.CENTER_RIGHT);

        btnPrev = StyleFactory.secondaryBtn("◀ Précédent");
        btnPrev.setOnAction(e -> { if (currentPage > 1) { currentPage--; refresh(); } });

        lblPagination = new Label();
        lblPagination.setStyle(StyleFactory.subtitleStyle());

        btnNext = StyleFactory.secondaryBtn("Suivant ▶");
        btnNext.setOnAction(e -> { currentPage++; refresh(); });

        pagination.getChildren().addAll(btnPrev, lblPagination, btnNext);

        root.getChildren().addAll(header, table, pagination);
        VBox.setVgrow(table, Priority.ALWAYS);

        refresh();
        return root;
    }

    // ── Tableau 

    @SuppressWarnings("unchecked")
    private TableView<Student> buildTable() {
        TableView<Student> tv = new TableView<>();
        tv.setStyle(StyleFactory.tableStyle());
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tv.setPlaceholder(new Label("Aucun étudiant trouvé."));

        TableColumn<Student, Integer> colId = col("ID", "id", 60);
        TableColumn<Student, String> colPrenom = col("Prénom", "firstName", 140);
        TableColumn<Student, String> colNom = col("Nom", "lastName", 140);
        TableColumn<Student, LocalDate> colBirthDate = col("Date de Naissance", "birthDate", 120);

        // Colonne note avec couleur
        TableColumn<Student, String> colNote = new TableColumn<>("Note");
        colNote.setCellValueFactory(data ->
                new SimpleStringProperty(String.format("%.2f / 20", data.getValue().getGrade())));
        colNote.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                Student s = getTableView().getItems().get(getIndex());
                setStyle(s.getGrade() >= 10
                        ? "-fx-text-fill: " + StyleFactory.C_SUCCESS + "; -fx-font-weight: bold;"
                        : "-fx-text-fill: " + StyleFactory.C_DANGER  + "; -fx-font-weight: bold;");
            }
        });
        colNote.setPrefWidth(120);

        // Colonne mention
        TableColumn<Student, String> colMention = new TableColumn<>("Mention");
        colMention.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getMention()));
        colMention.setPrefWidth(110);

        TableColumn<Student, String> colEmail = col("Email", "email", 200);

        // Colonne actions
        TableColumn<Student, Void> colActions = new TableColumn<>("Actions");
        colActions.setPrefWidth(150);
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit   = StyleFactory.primaryBtn("✏️");
            private final Button btnDelete = StyleFactory.dangerBtn("🗑️");
            private final HBox box = new HBox(6, btnEdit, btnDelete);
            {
                box.setAlignment(Pos.CENTER);
                btnEdit.setPrefHeight(30);
                btnDelete.setPrefHeight(30);
                btnEdit.setOnAction(e -> {
                    Student s = getTableView().getItems().get(getIndex());
                    openForm(s);
                });
                btnDelete.setOnAction(e -> {
                    Student s = getTableView().getItems().get(getIndex());
                    confirmDelete(s);
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        // Tri par colonne
        colId.setOnEditStart(null);
        tv.getSortOrder().clear();
        colId.setSortable(true);
        colPrenom.setSortable(true);
        colNom.setSortable(true);
        colAge.setSortable(true);
        colNote.setSortable(false);

        tv.getColumns().addAll(colId, colPrenom, colNom, colDatedeNaissance, colNote, colMention, colEmail, colActions);

        // Tri côté BDD au clic sur en-tête
        tv.setOnSort(e -> {
            if (!tv.getSortOrder().isEmpty()) {
                TableColumn<?,?> sorted = tv.getSortOrder().get(0);
                sortBy = switch (sorted.getText()) {
                    case "Prénom" -> "first_name";
                    case "Nom"    -> "last_name";
                    case "Âge"   -> "age";
                    default       -> "id";
                };
                asc = sorted.getSortType() == TableColumn.SortType.ASCENDING;
                currentPage = 1;
                refresh();
            }
        });

        return tv;
    }

    private <T> TableColumn<Student, T> col(String header, String prop, int width) {
        TableColumn<Student, T> c = new TableColumn<>(header);
        c.setCellValueFactory(new PropertyValueFactory<>(prop));
        c.setPrefWidth(width);
        return c;
    }

    // ── Refresh 

    private void refresh() {
        try {
            List<Student> page = service.getStudentsPaged(currentPage, pageSize, sortBy, asc);
            int total      = service.countStudents();
            int totalPages = (int) Math.ceil((double) total / pageSize);
            if (totalPages == 0) totalPages = 1;

            table.setItems(FXCollections.observableArrayList(page));
            lblPagination.setText("Page " + currentPage + " / " + totalPages
                    + "  (" + total + " étudiant(s))");
            btnPrev.setDisable(currentPage <= 1);
            btnNext.setDisable(currentPage >= totalPages);

        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Erreur", ex.getMessage());
        }
    }

    // ── Formulaire 

    private void openForm(Student existing) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(existing == null ? "Ajouter un étudiant" : "Modifier l'étudiant");
        dialog.setResizable(false);

        Node form = new EtudiantFormView(service, existing, () -> {
            dialog.close();
            refresh();
        }).build();

        Scene sc = new javafx.scene.Scene(new StackPane(form), 480, 500);
        dialog.setScene(sc);
        dialog.showAndWait();
    }

    // ── Suppression 

    private void confirmDelete(Student s) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer " + s.getFirstName() + " " + s.getLastName() + " ?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                try {
                    service.deleteStudent(s.getId());
                    refresh();
                } catch (Exception ex) {
                    showAlert(Alert.AlertType.ERROR, "Erreur suppression", ex.getMessage());
                }
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type, msg, ButtonType.OK);
        a.setTitle(title); a.setHeaderText(null); a.showAndWait();
    }
}