package com.example.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.example.DAO.StudentDAO;
import com.example.model.StudentModel;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 *StudentController — manages the display and actions on students (CRUD).  
 *  Cleaned of all hash/pepper logic.
 */
public class StudentController {

    //  UI components linked via FXML
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private DatePicker birthDatePicker;

    @FXML private TableView<StudentModel> studentTable;

    // Colonnes de la TableView
    @FXML private TableColumn<StudentModel, Integer> colId;
    @FXML private TableColumn<StudentModel, String> colFirstName;
    @FXML private TableColumn<StudentModel, String> colLastName;
    @FXML private TableColumn<StudentModel, LocalDate> colBirthDate;
    @FXML private TableColumn<StudentModel, LocalDate> colCreationDate;
    @FXML private TableColumn<StudentModel, LocalDate> colLastModifiedDate;

    //DAO to access student data
    private final StudentDAO studentDAO = new StudentDAO();

    /**
     * Method automatically called by JavaFX after the FXML is loaded.
     */
    @FXML
    public void initialize() {
        // 1. Column configuration: each column is linked to a property of the model
        colId.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getId()));
        colFirstName.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getFirstName()));
        colLastName.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getLastName()));
        colBirthDate.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getBirthDate()));

        // Secure conversion of LocalDateTime to LocalDate for display
        colCreationDate.setCellValueFactory(data -> {
            var date = data.getValue().getCreationDate();
            return new SimpleObjectProperty<>(date != null ? date.toLocalDate() : null);
        });

        colLastModifiedDate.setCellValueFactory(data -> {
            var date = data.getValue().getLastModifiedDate();
            return new SimpleObjectProperty<>(date != null ? date.toLocalDate() : null);
        });

        // 2. Selection listener: fills in the form when a row is clicked
        studentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                firstNameField.setText(newSelection.getFirstName());
                lastNameField.setText(newSelection.getLastName());
                birthDatePicker.setValue(newSelection.getBirthDate());
            }
        });

        // 3. load files from database
        refreshTable();
    }

    //  Actions Boutons 

    @FXML
    public void handleAdd() {
        String firstName = firstNameField.getText().trim();
        String lastName  = lastNameField.getText().trim();
        LocalDate birth  = birthDatePicker.getValue();

        // Field-level validation
        List<String> errors = StudentValidator.validate(firstName, lastName, birth);
        if (!errors.isEmpty()) {
            showAlert("Données invalides", String.join("\n", errors));
            return;
        }

        try {
            // Duplicate check for new students (no excludeId)
            if (studentDAO.existsByNameAndBirthDate(firstName, lastName, birth, null)) {
                showAlert("Doublon détecté", "Un étudiant avec ce nom et cette date de naissance existe déjà.");
                return;
            }
            studentDAO.addStudent(firstName, lastName, birth);
            clearFields();
            refreshTable();
        } catch (Exception e) {
            showError("Erreur lors de l'ajout", e);
        }
    }

    @FXML
    public void handleUpdate() {
        StudentModel selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Sélection requise", "Veuillez sélectionner un étudiant dans la table.");
            return;
        }

        String firstName = firstNameField.getText().trim();
        String lastName  = lastNameField.getText().trim();
        LocalDate birth  = birthDatePicker.getValue();

        List<String> errors = StudentValidator.validate(firstName, lastName, birth);
        if (!errors.isEmpty()) {
            showAlert("Données invalides", String.join("\n", errors));
            return;
        }

        try {
            // Exclude the student being edited to avoid a self-duplicate false positive
            if (studentDAO.existsByNameAndBirthDate(firstName, lastName, birth, selected.getId())) {
                showAlert("Doublon détecté", "Un autre étudiant avec ce nom et cette date de naissance existe déjà.");
                return;
            }
            studentDAO.updateStudent(selected.getId(), firstName, lastName, birth);
            refreshTable();
        } catch (Exception e) {
            showError("Erreur lors de la modification", e);
        }
    }

    @FXML
    public void handleDelete() {
        StudentModel selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Sélection requise", "Veuillez sélectionner un étudiant à supprimer.");
            return;
        }

        Alert confirm = new Alert(AlertType.CONFIRMATION, "Supprimer l'étudiant " + selected.getFirstName() + " ?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            try {
                studentDAO.deleteStudent(selected.getId());
                clearFields();
                refreshTable();
            } catch (Exception e) {
                showError("Erreur lors de la suppression", e);
            }
        }
    }

    //Utility methods for the controller (validation, alerts, refresh)

    /**
     * load students from database and display them in the table
     */
    private void refreshTable() {
        try {
            ObservableList<StudentModel> list = FXCollections.observableArrayList(studentDAO.getAllStudents());
            studentTable.setItems(list);
        } catch (Exception e) {
            showError("Erreur de chargement des données", e);
        }
    }
//  Clears the form fields and deselects any selected student in the table.
    private void clearFields() {
        firstNameField.clear();
        lastNameField.clear();
        birthDatePicker.setValue(null);
        studentTable.getSelectionModel().clearSelection();
    }
    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showError(String title, Exception e) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(title);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
        e.printStackTrace();
    }
}