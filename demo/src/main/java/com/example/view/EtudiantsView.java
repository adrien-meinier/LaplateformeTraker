package com.example.view;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.time.format.DateTimeFormatter;

import com.example.controller.BulletinController;
import com.example.controller.ExportController;
import com.example.controller.ImportController;
import com.example.DAO.StudentDAO;
import com.example.model.StudentModel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class EtudiantsView {

    private final StudentDAO dao;
    private final BulletinController bulletinCtrl = new BulletinController();
    private final ExportController exportCtrl = new ExportController();
    private final ImportController importCtrl = new ImportController();

    private int currentPage = 1;
    private final int pageSize = 10;

    private TableView<StudentModel> table;
    private Label lblPagination;
    private Button btnPrev, btnNext;

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
        DateTimeFormatter.ofPattern("dd-MM-yyyy 'à' HH:mm");

    public EtudiantsView(StudentDAO dao) {
        this.dao = dao;
    }

    public Node build() {
        VBox root = new VBox(16);
        root.setPadding(new Insets(10));

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("👨‍🎓 Étudiants");
        title.setFont(Font.font("System", FontWeight.BOLD, 20));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // 📂 Importer CSV
        Button btnImport = StyleFactory.warningBtn("📂 Importer CSV");
        btnImport.setOnAction(e -> {
            importCtrl.importerEtudiantsDepuisBulletin();
            refresh();
        });

        Button btnExport = StyleFactory.exportBtn("📥 Exporter tout");
        btnExport.setOnAction(e -> exportCtrl.exporterTousLesEtudiants());

        Button btnAdd = StyleFactory.successBtn("➕ Ajouter");
        btnAdd.setOnAction(e -> openForm(null));

        header.getChildren().addAll(title, spacer, btnImport, btnExport, btnAdd);

        table = buildTable();

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

    private TableView<StudentModel> buildTable() {
        TableView<StudentModel> tv = new TableView<>();
        tv.setStyle(StyleFactory.tableStyle());
        tv.setPlaceholder(new Label("Aucun étudiant trouvé."));

        TableColumn<StudentModel, Integer> colId = col("ID", "id", 60);
        TableColumn<StudentModel, String> colPrenom = col("Prénom", "firstName", 150);
        TableColumn<StudentModel, String> colNom = col("Nom", "lastName", 150);
        TableColumn<StudentModel, Double> colAverage = col("Moyenne", "averageGrade", 100);
        TableColumn<StudentModel, LocalDate> colBirth = col("Date de naissance", "birthDate", 150);
        TableColumn<StudentModel, LocalDateTime> colCreated = col("Créé le", "creationDate", 160);
        TableColumn<StudentModel, LocalDateTime> colModified = col("Modifié le", "lastModifiedDate", 160);

        colCreated.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.format(DATE_TIME_FORMATTER));
            }
        });

        colModified.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.format(DATE_TIME_FORMATTER));
            }
        });

        TableColumn<StudentModel, Void> colActions = new TableColumn<>("Actions");
        colActions.setPrefWidth(240);
        colActions.setSortable(false);
        colActions.setCellFactory(col -> new TableCell<>() {

            private final Button btnEdit = StyleFactory.primaryBtn("✏️");
            private final Button btnBulletin = StyleFactory.bulletinBtn("📄 Bulletin");
            private final Button btnDelete = StyleFactory.dangerBtn("🗑️");
            private final HBox box = new HBox(6, btnEdit, btnBulletin, btnDelete);

            {
                box.setAlignment(Pos.CENTER);

                btnEdit.setOnAction(e -> {
                    StudentModel s = getTableRow().getItem();
                    if (s != null) openForm(s);
                });

                btnBulletin.setOnAction(e -> {
                    StudentModel s = getTableRow().getItem();
                    if (s != null) bulletinCtrl.telechargerBulletin(s);
                });

                btnDelete.setOnAction(e -> {
                    StudentModel s = getTableRow().getItem();
                    if (s != null) confirmDelete(s);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        tv.getColumns().addAll(colId, colPrenom, colNom, colAverage, colBirth, colCreated, colModified, colActions);
        return tv;
    }

    private <T> TableColumn<StudentModel, T> col(String title, String property, int width) {
        TableColumn<StudentModel, T> col = new TableColumn<>(title);
        col.setCellValueFactory(new PropertyValueFactory<>(property));
        col.setPrefWidth(width);
        return col;
    }

    private void refresh() {
        try {
            List<StudentModel> students = dao.getAllStudents();
            int total = students.size();
            int totalPages = Math.max(1, (int) Math.ceil((double) total / pageSize));

            if (currentPage > totalPages) currentPage = totalPages;
            if (currentPage < 1) currentPage = 1;

            int from = (currentPage - 1) * pageSize;
            int to = Math.min(from + pageSize, total);

            ObservableList<StudentModel> data = FXCollections.observableArrayList(
                    total == 0 ? List.of() : students.subList(from, to));

            table.setItems(data);
            lblPagination.setText("Page " + currentPage + " / " + totalPages + "  (" + total + " étudiant(s))");
            btnPrev.setDisable(currentPage == 1);
            btnNext.setDisable(currentPage == totalPages);

        } catch (SQLException e) {
            showAlert("Erreur SQL", e.getMessage());
        }
    }

    private void openForm(StudentModel student) {
        Stage stage = new Stage();
        stage.setTitle(student == null ? "Ajouter un étudiant" : "Modifier un étudiant");
        stage.initModality(Modality.APPLICATION_MODAL);

        EtudiantFormView view = new EtudiantFormView(dao, student, () -> {
            stage.close();
            refresh();
        });

        Scene scene = new Scene((Pane) view.build());
        stage.setScene(scene);
        stage.showAndWait();
    }

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
