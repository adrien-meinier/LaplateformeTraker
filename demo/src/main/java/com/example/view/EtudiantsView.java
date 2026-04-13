package com.example.view;

import com.example.controller.BulletinController;
import com.example.controller.ExportController;
import com.example.controller.StudentDAO;
import com.example.model.StudentModel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class EtudiantsView {

    private final StudentDAO         dao;
    private final BulletinController bulletinCtrl = new BulletinController();
    private final ExportController   exportCtrl   = new ExportController();

    private int currentPage = 1;
    private final int pageSize = 10;

    private TableView<StudentModel> table;
    private Label  lblPagination;
    private Button btnPrev, btnNext;

    public EtudiantsView(StudentDAO dao) {
        this.dao = dao;
    }

    public Node build() {
        VBox root = new VBox(16);
        root.setPadding(new Insets(10));

        // ── En-tête ──────────────────────────────────────────────────────
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("👨‍🎓 Étudiants");
        title.setFont(Font.font("System", FontWeight.BOLD, 20));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Bouton export global — style via StyleFactory.exportBtn()
        Button btnExport = StyleFactory.exportBtn("📥 Exporter tout");
        btnExport.setOnAction(e -> exportCtrl.exporterTousLesEtudiants());

        // Bouton ajout — style via StyleFactory.successBtn()
        Button btnAdd = StyleFactory.successBtn("➕ Ajouter");
        btnAdd.setOnAction(e -> openForm(null));

        header.getChildren().addAll(title, spacer, btnExport, btnAdd);

        // ── Tableau ───────────────────────────────────────────────────────
        table = buildTable();

        // ── Pagination ────────────────────────────────────────────────────
        HBox pagination = new HBox(10);
        pagination.setAlignment(Pos.CENTER);

        btnPrev = StyleFactory.secondaryBtn("◀");
        btnNext = StyleFactory.secondaryBtn("▶");
        lblPagination = new Label();
        lblPagination.setStyle(StyleFactory.subtitleStyle());

        btnPrev.setOnAction(e -> { if (currentPage > 1) { currentPage--; refresh(); } });
        btnNext.setOnAction(e -> { currentPage++; refresh(); });

        pagination.getChildren().addAll(btnPrev, lblPagination, btnNext);

        root.getChildren().addAll(header, table, pagination);
        VBox.setVgrow(table, Priority.ALWAYS);

        refresh();
        return root;
    }

    // ── Tableau ───────────────────────────────────────────────────────────

    private TableView<StudentModel> buildTable() {
        TableView<StudentModel> tv = new TableView<>();
        tv.setStyle(StyleFactory.tableStyle());
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tv.setPlaceholder(new Label("Aucun étudiant trouvé."));

        TableColumn<StudentModel, Integer>       colId       = col("ID",               "id",               60);
        TableColumn<StudentModel, String>        colPrenom   = col("Prénom",            "firstName",       150);
        TableColumn<StudentModel, String>        colNom      = col("Nom",               "lastName",        150);
        TableColumn<StudentModel, LocalDate>     colBirth    = col("Date de naissance", "birthDate",       150);
        TableColumn<StudentModel, LocalDateTime> colCreated  = col("Créé le",           "creationDate",    160);
        TableColumn<StudentModel, LocalDateTime> colModified = col("Modifié le",        "lastModifiedDate",160);

        // ── Colonne Actions : ✏️ | 📄 Bulletin | 🗑️ ─────────────────────
        TableColumn<StudentModel, Void> colActions = new TableColumn<>("Actions");
        colActions.setPrefWidth(240);
        colActions.setSortable(false);
        colActions.setCellFactory(col -> new TableCell<>() {

            // Tous les boutons passent par StyleFactory
            private final Button btnEdit     = StyleFactory.primaryBtn("✏️");
            private final Button btnBulletin = StyleFactory.bulletinBtn("📄 Bulletin");
            private final Button btnDelete   = StyleFactory.dangerBtn("🗑️");
            private final HBox   box         = new HBox(6, btnEdit, btnBulletin, btnDelete);

            {
                box.setAlignment(Pos.CENTER);
                btnEdit.setPrefHeight(30);
                btnDelete.setPrefHeight(30);

                btnEdit.setOnAction(e -> {
                    StudentModel s = getTableRow() == null ? null : getTableRow().getItem();
                    if (s != null) openForm(s);
                });

                // Bulletin individuel via StyleFactory.bulletinBtn
                btnBulletin.setOnAction(e -> {
                    StudentModel s = getTableRow() == null ? null : getTableRow().getItem();
                    if (s != null) bulletinCtrl.telechargerBulletin(s);
                });

                btnDelete.setOnAction(e -> {
                    StudentModel s = getTableRow() == null ? null : getTableRow().getItem();
                    if (s != null) confirmDelete(s);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        tv.getColumns().addAll(colId, colPrenom, colNom, colBirth, colCreated, colModified, colActions);
        return tv;
    }

    private <T> TableColumn<StudentModel, T> col(String title, String property, int width) {
        TableColumn<StudentModel, T> col = new TableColumn<>(title);
        col.setCellValueFactory(new PropertyValueFactory<>(property));
        col.setPrefWidth(width);
        return col;
    }

    // ── Refresh ───────────────────────────────────────────────────────────

    private void refresh() {
        try {
            List<StudentModel> students = dao.getAllStudents();
            int total      = students.size();
            int totalPages = Math.max(1, (int) Math.ceil((double) total / pageSize));

            if (currentPage > totalPages) currentPage = totalPages;
            if (currentPage < 1)          currentPage = 1;

            int from = (currentPage - 1) * pageSize;
            int to   = Math.min(from + pageSize, total);

            ObservableList<StudentModel> data = FXCollections.observableArrayList(
                    total == 0 ? List.of() : students.subList(from, to));

            table.setItems(data);
            lblPagination.setText("Page " + currentPage + " / " + totalPages
                    + "  (" + total + " étudiant(s))");
            btnPrev.setDisable(currentPage == 1);
            btnNext.setDisable(currentPage == totalPages);

        } catch (SQLException e) {
            showAlert("Erreur SQL", e.getMessage());
        } catch (Exception e) {
            showAlert("Erreur", e.getMessage());
        }
    }

    // ── Formulaire ────────────────────────────────────────────────────────

    private void openForm(StudentModel student) {
        Dialog<StudentModel> dialog = new Dialog<>();
        dialog.setTitle(student == null ? "Ajouter" : "Modifier");
        dialog.setHeaderText(student == null ? "Ajouter un étudiant" : "Modifier un étudiant");

        ButtonType btnSave = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnSave, ButtonType.CANCEL);

        TextField  tfPrenom = new TextField();
        TextField  tfNom    = new TextField();
        DatePicker dpBirth  = new DatePicker();

        if (student != null) {
            tfPrenom.setText(student.getFirstName());
            tfNom.setText(student.getLastName());
            dpBirth.setValue(student.getBirthDate());
        }

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(10));
        grid.addRow(0, new Label("Prénom :"),    tfPrenom);
        grid.addRow(1, new Label("Nom :"),       tfNom);
        grid.addRow(2, new Label("Naissance :"), dpBirth);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(button -> {
            if (button == btnSave) {
                if (tfPrenom.getText().isBlank() || tfNom.getText().isBlank()
                        || dpBirth.getValue() == null) return null;
                return new StudentModel(
                        student == null ? 0 : student.getId(),
                        tfPrenom.getText().trim(),
                        tfNom.getText().trim(),
                        dpBirth.getValue(),
                        student == null ? LocalDateTime.now() : student.getCreationDate(),
                        LocalDateTime.now()
                );
            }
            return null;
        });

        Optional<StudentModel> result = dialog.showAndWait();
        result.ifPresent(s -> {
            try {
                if (student == null) dao.addStudent(s.getFirstName(), s.getLastName(), s.getBirthDate());
                else                 dao.updateStudent(s.getId(), s.getFirstName(), s.getLastName(), s.getBirthDate());
                refresh();
            } catch (SQLException e) {
                showAlert("Erreur SQL", e.getMessage());
            }
        });
    }

    // ── Suppression ───────────────────────────────────────────────────────

    private void confirmDelete(StudentModel s) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer " + s.getFirstName() + " " + s.getLastName() + " ?",
                ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                try {
                    dao.deleteStudent(s.getId());
                    refresh();
                } catch (SQLException e) {
                    showAlert("Erreur SQL", e.getMessage());
                }
            }
        });
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}