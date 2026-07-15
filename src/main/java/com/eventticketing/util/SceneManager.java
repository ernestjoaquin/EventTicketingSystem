package com.eventticketing.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/** Centralizes FXML scene switching for the primary application Stage. */
public class SceneManager {

    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) { primaryStage = stage; }

    public static void switchTo(String fxmlPath, String title) {
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
