package com.example.view;

import com.example.controller.StudentDAO;
import com.example.model.StudentModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EtudiantsView {

    private final StudentDAO dao;

    private TableView<StudentModel> table;
    private Label lblPagination;
    private Button btnPrev, btnNext;

    private int currentPage = 1;
    private final int pageSize = 10;

    private final DateTimeFormatter df  = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public EtudiantsView(StudentDAO dao) {
        this.dao = dao;
    }

    public Node build() {

        VBox root = new VBox(20);
        root.setPadding(new Insets(20));

        Label title = new Label("Étudiants");
        title.setFont(Font.font(22));

        Button btnAdd = new Button("+ Ajouter");
        btnAdd.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");

        HBox header = new HBox(title, new Region(), btnAdd);
        HBox.setHgrow(header.getChildren().get(1), Priority.ALWAYS);

        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // ID
        TableColumn<StudentModel, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<StudentModel, Integer>("id"));

        // Prénom
        TableColumn<StudentModel, String> colPrenom = new TableColumn<>("Prénom");
        colPrenom.setCellValueFactory(new PropertyValueFactory<StudentModel, String>("firstName"));

        // Nom
        TableColumn<StudentModel, String> colNom = new TableColumn<>("Nom");
        colNom.setCellValueFactory(new PropertyValueFactory<StudentModel, String>("lastName"));

        // Date de naissance
        TableColumn<StudentModel, LocalDate> colBirth = new TableColumn<>("Date de naissance");
        colBirth.setCellValueFactory(new PropertyValueFactory<StudentModel, LocalDate>("birthDate"));
        colBirth.setCellFactory(col -> new TableCell<StudentModel, LocalDate>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : df.format(item));
            }
        });

        // Créé le
        TableColumn<StudentModel, LocalDateTime> colCreation = new TableColumn<>("Créé le");
        colCreation.setCellValueFactory(new PropertyValueFactory<StudentModel, LocalDateTime>("creationDate"));
        colCreation.setCellFactory(col -> new TableCell<StudentModel, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : dtf.format(item));
            }
        });

        // Modifié le
        TableColumn<StudentModel, LocalDateTime> colModif = new TableColumn<>("Modifié le");
        colModif.setCellValueFactory(new PropertyValueFactory<StudentModel, LocalDateTime>("lastModifiedDate"));
        colModif.setCellFactory(col -> new TableCell<StudentModel, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : dtf.format(item));
            }
        });

        table.getColumns().addAll(colId, colPrenom, colNom, colBirth, colCreation, colModif);

        // Pagination
        btnPrev = new Button("◀");
        btnNext = new Button("▶");

        btnPrev.setOnAction(e -> { currentPage--; refresh(); });
        btnNext.setOnAction(e -> { currentPage++; refresh(); });

        lblPagination = new Label();

        HBox pagination = new HBox(10, btnPrev, lblPagination, btnNext);
        pagination.setAlignment(Pos.CENTER);

        root.getChildren().addAll(header, table, pagination);

        refresh();
        return root;
    }

    private void refresh() {
        try {
            List<StudentModel> all = dao.getAllStudents();
            int total = all.size();

            if (total == 0) {
                table.setItems(FXCollections.observableArrayList());
                lblPagination.setText("Page 1 / 1");
                btnPrev.setDisable(true);
                btnNext.setDisable(true);
                return;
            }

            int totalPages = (int) Math.ceil((double) total / pageSize);

            if (currentPage < 1) currentPage = 1;
            if (currentPage > totalPages) currentPage = totalPages;

            int from = (currentPage - 1) * pageSize;
            int to = Math.min(from + pageSize, total);

            ObservableList<StudentModel> page =
                    FXCollections.observableArrayList(all.subList(from, to));

            table.setItems(page);

            lblPagination.setText("Page " + currentPage + " / " + totalPages);
            btnPrev.setDisable(currentPage == 1);
            btnNext.setDisable(currentPage == totalPages);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
