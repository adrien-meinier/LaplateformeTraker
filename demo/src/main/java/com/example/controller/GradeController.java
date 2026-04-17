package com.example.controller;

import java.sql.SQLException;
import java.util.List;

import com.example.model.GradeModel;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * JavaFX Controller responsible for handling grade-related actions.
 * Interacts with GradeDAO to perform CRUD operations.
 */
public class GradeController {

    @FXML
    private TextField studentIdField;

    @FXML
    private TextField gradeField;

    @FXML
    private TextField subjectField;

    @FXML
    private TextField gradeIdField;

    @FXML
    private TextArea resultArea;

    @FXML
    private Label messageLabel;

    // DAO used for database operations
    private GradeDAO gradeDAO = new GradeDAO();

    //Handles adding a new grade.
    @FXML
    private void handleAddGrade() {
        try {
            int studentId = Integer.parseInt(studentIdField.getText());
            int grade = Integer.parseInt(gradeField.getText());
            String subject = subjectField.getText();

            int id = gradeDAO.addGrade(studentId, grade, subject);

            messageLabel.setText("Grade added with ID: " + id);

        } catch (NumberFormatException e) {
            messageLabel.setText("Invalid number format");
        } catch (SQLException e) {
            messageLabel.setText("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //Handles updating an existing grade.
    @FXML
    private void handleUpdateGrade() {
        try {
            int id = Integer.parseInt(gradeIdField.getText());
            int grade = Integer.parseInt(gradeField.getText());
            String subject = subjectField.getText();

            boolean updated = gradeDAO.updateGrade(id, grade, subject);

            if (updated) {
                messageLabel.setText("Grade updated");
            } else {
                messageLabel.setText("Grade not found");
            }

        } catch (NumberFormatException e) {
            messageLabel.setText("Invalid number format");
        } catch (SQLException e) {
            messageLabel.setText("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }


   //Handles deleting a grade by ID.
    @FXML
    private void handleDeleteGrade() {
        try {
            int id = Integer.parseInt(gradeIdField.getText());

            boolean deleted = gradeDAO.deleteGrade(id);

            if (deleted) {
                messageLabel.setText("Grade deleted");
            } else {
                messageLabel.setText("Grade not found");
            }

        } catch (NumberFormatException e) {
            messageLabel.setText("Invalid ID format");
        } catch (SQLException e) {
            messageLabel.setText("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }


    //Retrieves and displays a grade by ID.
    @FXML
    private void handleGetGradeById() {
        try {
            int id = Integer.parseInt(gradeIdField.getText());

            GradeModel grade = gradeDAO.getGradeById(id);
            // Display the grade details or a not found message

            if (grade != null) {
                resultArea.setText(formatGrade(grade));
                messageLabel.setText("Grade found");
            } else {
                resultArea.clear();
                messageLabel.setText("Grade not found");
            }

        } catch (NumberFormatException e) {
            messageLabel.setText("Invalid ID format");
        } catch (SQLException e) {
            messageLabel.setText("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }


    //Retrieves and displays all grades for a student.
    @FXML
    private void handleGetGradesByStudent() {
        try {
            int studentId = Integer.parseInt(studentIdField.getText());

            List<GradeModel> grades = gradeDAO.getGradesByStudentId(studentId);
// Display the grades or a not found message
            if (grades.isEmpty()) {
                resultArea.setText("");
                messageLabel.setText("No grades found");
            } else {
                StringBuilder sb = new StringBuilder();
                for (GradeModel g : grades) {
                    sb.append(formatGrade(g)).append("\n\n");
                }
                resultArea.setText(sb.toString());
                messageLabel.setText("Grades loaded");
            }

        } catch (NumberFormatException e) {
            messageLabel.setText("Invalid student ID");
        } catch (SQLException e) {
            messageLabel.setText("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }


    //Retrieves and displays all grades.
    @FXML
    private void handleGetAllGrades() {
        try {
            List<GradeModel> grades = gradeDAO.getAllGrades();

            if (grades.isEmpty()) {
                resultArea.setText("");
                messageLabel.setText("No grades available");
            } else {
                StringBuilder sb = new StringBuilder();
                for (GradeModel g : grades) {
                    sb.append(formatGrade(g)).append("\n\n");
                }
                resultArea.setText(sb.toString());
                messageLabel.setText("All grades loaded");
            }

        } catch (SQLException e) {
            messageLabel.setText("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Formats a GradeModel into a readable string.
    private String formatGrade(GradeModel g) {
        return "ID: " + g.getId() +
               "\nStudent ID: " + g.getStudentId() +
               "\nGrade: " + g.getGrade() +
               "\nSubject: " + g.getSubject() +
               "\nCreated: " + g.getCreationDate() +
               "\nUpdated: " + g.getLastModifiedDate();
    }
}