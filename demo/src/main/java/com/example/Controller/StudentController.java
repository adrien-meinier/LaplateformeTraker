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
    @FXML private TextField firstNameField;       // Input field for student's first name
    @FXML private TextField lastNameField;        // Input field for student's last name
    @FXML private DatePicker birthDatePicker;     // DatePicker for student's birth date

    @FXML private TableView<StudentModel> studentTable;  // Table to display all students

    // Table columns
    @FXML private TableColumn<StudentModel, Integer> colId;
    @FXML private TableColumn<StudentModel, String> colFirstName;
    @FXML private TableColumn<StudentModel, String> colLastName;
    @FXML private TableColumn<StudentModel, LocalDate> colBirthDate;
    @FXML private TableColumn<StudentModel, LocalDate> colCreationDate;
    @FXML private TableColumn<StudentModel, LocalDate> colLastModifiedDate;

    // DAO object to interact with the database
    private StudentDAO studentDAO = new StudentDAO();

    
    // Initialize method called automatically when the view is loaded
    
    @FXML
    public void initialize() {

        // Bind each table column to the corresponding property in StudentModel
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colBirthDate.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getBirthDate())
        );

        // For creationDate and lastModifiedDate, convert LocalDateTime (from DB) to LocalDate
        colCreationDate.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getCreationDate().toLocalDate())
        );
        colLastModifiedDate.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(
                        cellData.getValue().getLastModifiedDate() != null ?
                        cellData.getValue().getLastModifiedDate().toLocalDate() : null
                )
        );

        // Listener: when a student is selected in the table, populate the input fields
        studentTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        firstNameField.setText(newSelection.getFirstName());
                        lastNameField.setText(newSelection.getLastName());
                        birthDatePicker.setValue(newSelection.getBirthDate());
                    }
                }
        );

        // Load all students from the database when UI starts
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
            // Validate that all fields are filled
            if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty() || birthDatePicker.getValue() == null) {
                showAlert("All fields are required!");
                return;
            }

            // Call DAO to insert the new student into the database
            int newId = studentDAO.addStudent(
                    firstNameField.getText(),
                    lastNameField.getText(),
                    birthDatePicker.getValue()
            );

            // Clear input fields and refresh table
            clearFields();
            loadStudents();

        } catch (Exception e) {
            showError(e);
        }
    }

    
    // READ: Load all students from database and display in TableView

    public void loadStudents() throws Exception {
        // Get all students from DAO
        ObservableList<StudentModel> list = FXCollections.observableArrayList(studentDAO.getAllStudents());
        // Set data in TableView
        studentTable.setItems(list);
    }


    // UPDATE: Modify selected student
    @FXML
    public void handleUpdate() {
        // Get the selected student from the table
        StudentModel selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No student selected!");
            return;
        }

        try {
            // Validate input fields
            if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty() || birthDatePicker.getValue() == null) {
                showAlert("All fields are required!");
                return;
            }

            // Call DAO to update the student in the database
            studentDAO.updateStudent(
                    selected.getId(),
                    firstNameField.getText(),
                    lastNameField.getText(),
                    birthDatePicker.getValue()
            );

            // Refresh table and clear input fields
            loadStudents();
            clearFields();

        } catch (Exception e) {
            showError(e);
        }
    }


    // DELETE: Remove selected student
    @FXML
    public void handleDelete() {
        // Get selected student
        StudentModel selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No student selected!");
            return;
        }

        try {
            // Show confirmation dialog before deleting
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmation");
            confirm.setContentText("Delete this student?");
            Optional<ButtonType> result = confirm.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Delete student from database
                studentDAO.deleteStudent(selected.getId());
                loadStudents();
                clearFields();
            }

        } catch (Exception e) {
            showError(e);
        }
    }

    // Helper Methods

    // Clear all input fields
    private void clearFields() {
        firstNameField.clear();
        lastNameField.clear();
        birthDatePicker.setValue(null);
    }

    // Show a warning alert
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Show an error alert and print stack trace
    private void showError(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(e.getMessage());
        alert.showAndWait();
        e.printStackTrace();
    }
}