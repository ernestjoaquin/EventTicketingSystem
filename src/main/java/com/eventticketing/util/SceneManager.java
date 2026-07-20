package com.eventticketing.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/** Centralizes FXML scene switching for the primary application Stage. */
public class SceneManager {

    private static Stage primaryStage;
    private static final String LOGIN_FXML = "/com/eventticketing/view/Login.fxml";
    private static final String REGISTER_FXML = "/com/eventticketing/view/Register.fxml";

    public static void setPrimaryStage(Stage stage) { primaryStage = stage; }

    public static void switchTo(String fxmlPath, String title) {
        // Every protected screen is validated against the serialized session
        // (session.dat) before it is shown. If the session file is missing
        // or no user is loaded (e.g. it was deleted, or expired), the user
        // is bounced back to the login screen instead of being allowed to
        // navigate further into the app.
        boolean isAuthScreen = LOGIN_FXML.equals(fxmlPath) || REGISTER_FXML.equals(fxmlPath);
        if (!isAuthScreen && !SessionManager.getInstance().isSessionValid()) {
            fxmlPath = LOGIN_FXML;
            title = "Login";
        }
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(SceneManager.class.getResource("/com/eventticketing/css/style.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle(title);
            primaryStage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static FXMLLoader getLoader(String fxmlPath) {
        return new FXMLLoader(SceneManager.class.getResource(fxmlPath));
    }
}
