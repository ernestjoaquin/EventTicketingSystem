package com.eventticketing.controller;

import com.eventticketing.dao.UserDAO;
import com.eventticketing.model.Admin;
import com.eventticketing.model.User;
import com.eventticketing.util.SceneManager;
import com.eventticketing.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Please enter both email and password.");
            return;
        }

        User user = userDAO.login(email, password);
        if (user == null) {
            showError("Invalid email or password.");
            return;
        }

        SessionManager.getInstance().setCurrentUser(user);

        if (user instanceof Admin) {
            SceneManager.switchTo("/com/eventticketing/view/AdminDashboard.fxml", "Admin Dashboard");
        } else {
            SceneManager.switchTo("/com/eventticketing/view/BrowseEvents.fxml", "Browse Events");
        }
    }

    @FXML
    private void goToRegister() {
        SceneManager.switchTo("/com/eventticketing/view/Register.fxml", "Register");
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setManaged(true);
        errorLabel.setVisible(true);
    }
}
