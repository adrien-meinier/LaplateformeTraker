package com.example.controller;

import com.example.model.StudentModel;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.example.controller.StudentDAO;

import java.time.LocalDate;
import java.util.Optional;

public class StudentController {

    // -----------------------------
    // FXML UI Components
    // -----------------------------
    @FXML private TextField firstNameField;       
    @FXML private TextField lastNameField;        
    @FXML private DatePicker birthDatePicker;     

    @FXML private TableView<StudentModel> studentTable;  

    // Table columns
    @FXML private TableColumn<StudentModel, Integer> colId;
    @FXML private TableColumn<StudentModel, String> colFirstName;
    @FXML private TableColumn<StudentModel, String> colLastName;
    @FXML private TableColumn<StudentModel, LocalDate> colBirthDate;
    @FXML private TableColumn<StudentModel, LocalDate> colCreationDate;
    @FXML private TableColumn<StudentModel, LocalDate> colLastModifiedDate;

    // DAO object to interact with the database
    private StudentDAO studentDAO = new StudentDAO();

    @FXML
    public void initialize() {
        // --- Use SimpleObjectProperty for all columns ---
        colId.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getId())
        );
        colFirstName.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getFirstName())
        );
        colLastName.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getLastName())
        );
        colBirthDate.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getBirthDate())
        );
        colCreationDate.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(
                        cellData.getValue().getCreationDate() != null ?
                        cellData.getValue().getCreationDate().toLocalDate() : null
                )
        );
        colLastModifiedDate.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(
                        cellData.getValue().getLastModifiedDate() != null ?
                        cellData.getValue().getLastModifiedDate().toLocalDate() : null
                )
        );

        // Listener: populate input fields when a student is selected
        studentTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        firstNameField.setText(newSelection.getFirstName());
                        lastNameField.setText(newSelection.getLastName());
                        birthDatePicker.setValue(newSelection.getBirthDate());
                    }
                }
        );

        // Load all students
        try {
            loadStudents();
        } catch (Exception e) {
            showError(e);
        }
    }

    // CREATE: Add a new student
    @FXML
    public void handleAdd() {
        try {
            if (!validateFields()) return;

            studentDAO.addStudent(
                    firstNameField.getText().trim(),
                    lastNameField.getText().trim(),
                    birthDatePicker.getValue()
            );

            clearFields();
            loadStudents();

        } catch (Exception e) {
            showError(e);
        }
    }

    // READ: Load all students from database
    public void loadStudents() throws Exception {
        ObservableList<StudentModel> list = FXCollections.observableArrayList(studentDAO.getAllStudents());
        studentTable.setItems(list);
    }

    // UPDATE: Modify selected student
    @FXML
    public void handleUpdate() {
        StudentModel selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No student selected!");
            return;
        }

        try {
            if (!validateFields()) return;

            studentDAO.updateStudent(
                    selected.getId(),
                    firstNameField.getText().trim(),
                    lastNameField.getText().trim(),
                    birthDatePicker.getValue()
            );

            clearFields();
            loadStudents();

        } catch (Exception e) {
            showError(e);
        }
    }

    // DELETE: Remove selected student
    @FXML
    public void handleDelete() {
        StudentModel selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No student selected!");
            return;
        }

        try {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmation");
            confirm.setContentText("Delete this student?");
            Optional<ButtonType> result = confirm.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                studentDAO.deleteStudent(selected.getId());
                clearFields();
                loadStudents();
            }

        } catch (Exception e) {
            showError(e);
        }
    }

    // -----------------------------
    // Helper Methods
    // -----------------------------

    private void clearFields() {
        firstNameField.clear();
        lastNameField.clear();
        birthDatePicker.setValue(null);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(e.getMessage());
        alert.showAndWait();
        e.printStackTrace();
    }

    private boolean validateFields() {
        if (firstNameField.getText().trim().isEmpty() ||
            lastNameField.getText().trim().isEmpty() ||
            birthDatePicker.getValue() == null) {
            showAlert("All fields are required!");
            return false;
        }
        return true;
    }
}