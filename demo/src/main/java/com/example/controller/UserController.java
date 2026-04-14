package com.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import com.example.model.UserModel;

/**
 * JavaFX Controller responsible for handling user actions
 * such as login, registration, deletion, and password update.
 */
public class UserController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private CheckBox adminCheckBox;

    @FXML
    private Label messageLabel;

    // DAO used to interact with the database
    private UserDAO userDAO = new UserDAO();

    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();
        // The login now authenticates by username; we derive it from the email prefix
        String username = email.contains("@") ? email.split("@")[0] : email;

        try {
            UserModel user = userDAO.login(username, password);

            if (user != null) {
                messageLabel.setText("Login successful");

                if (user.isAdmin()) {
                    System.out.println("Admin logged in: " + user.getUsername());
                } else {
                    System.out.println("User logged in: " + user.getUsername());
                }

            } else {
                messageLabel.setText("Invalid username or password");
            }

        } catch (Exception e) {
            messageLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

     // Handles user registration.
     //Checks if email already exists before creating a new user.
    @FXML
    private void handleRegister() {
        String email = emailField.getText();
        String password = passwordField.getText();
        boolean isAdmin = adminCheckBox.isSelected();
        // Username derived from email prefix as fallback (FXML view has no username field)
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
            e.printStackTrace();
        }
    }


     // Handles user deletion based on email.
    @FXML
    private void handleDelete() {
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
            e.printStackTrace();
        }
    }

    //Handles password update for a given user.
    @FXML
    private void handleUpdatePassword() {
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
            e.printStackTrace();
        }
    }
}