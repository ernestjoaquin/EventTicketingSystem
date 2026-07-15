package com.eventticketing;

import com.eventticketing.util.SceneManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        SceneManager.setPrimaryStage(primaryStage);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/eventticketing/view/Login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/com/eventticketing/css/style.css").toExternalForm());

        primaryStage.setTitle("Event Ticketing System - Login");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
