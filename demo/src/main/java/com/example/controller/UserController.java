package com.example.controller;

import com.example.DAO.UserDAO;
import com.example.model.UserModel;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * JavaFX Controller responsible for handling user actions
 * such as login, registration, deletion, and password update.
 */
public class UserController {

    @FXML
    public TextField emailField;

    @FXML
    public PasswordField passwordField;

    @FXML
    public CheckBox adminCheckBox;

    @FXML
    public Label messageLabel;

    // DAO rendu public pour permettre l'injection dans les tests
    public UserDAO userDAO = new UserDAO();

    @FXML
    public void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();
        String username = email.contains("@") ? email.split("@")[0] : email;

        try {
            UserModel user = userDAO.login(username, password);

            if (user != null) {
                messageLabel.setText("Login successful");
            } else {
                messageLabel.setText("Invalid username or password");
            }

        } catch (Exception e) {
            messageLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    public void handleRegister() {
        String email = emailField.getText();
        String password = passwordField.getText();
        boolean isAdmin = adminCheckBox.isSelected();
        String username = email.contains("@") ? email.split("@")[0] : email;

        try {
            if (userDAO.usernameExists(username)) {
                messageLabel.setText("Username already in use");
                return;
            }
            if (userDAO.emailExists(email)) {
                messageLabel.setText("Email already in use");
                return;
            }

            boolean success = userDAO.register(username, email, password, isAdmin);

            if (success) {
                messageLabel.setText("Account created");
            } else {
                messageLabel.setText("Registration failed");
            }

        } catch (Exception e) {
            messageLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    public void handleDelete() {
        String email = emailField.getText();

        try {
            boolean deleted = userDAO.deleteUser(email);

            if (deleted) {
                messageLabel.setText("User deleted");
            } else {
                messageLabel.setText("User not found");
            }

        } catch (Exception e) {
            messageLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    public void handleUpdatePassword() {
        String email = emailField.getText();
        String newPassword = passwordField.getText();

        try {
            boolean updated = userDAO.updatePassword(email, newPassword);

            if (updated) {
                messageLabel.setText("Password updated");
            } else {
                messageLabel.setText("User not found");
            }

        } catch (Exception e) {
            messageLabel.setText("Error: " + e.getMessage());
        }
    }
}
