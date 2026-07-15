package com.eventticketing.controller;

import com.eventticketing.dao.UserDAO;
import com.eventticketing.util.SceneManager;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {

    @FXML
    private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    private void handleRegister() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showError("Please fill in all required fields.");
            return;
        }
        if (!email.contains("@")) {
            showError("Please enter a valid email address.");
            return;
        }
        if (password.length() < 6) {
            showError("Password must be at least 6 characters.");
            return;
        }
        if (!password.equals(confirm)) {
            showError("Passwords do not match.");
            return;
        }

        int id = userDAO.register(name, email, phone, password);
        if (id == -1) {
            showError("Registration failed. That email may already be in use.");
            return;
        }

        SceneManager.switchTo("/com/eventticketing/view/Login.fxml", "Login");
    }

    @FXML
    private void goToLogin() {
        SceneManager.switchTo("/com/eventticketing/view/Login.fxml", "Login");
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setManaged(true);
        errorLabel.setVisible(true);
    }
}
