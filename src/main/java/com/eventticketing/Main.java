package com.eventticketing;

import com.eventticketing.model.Admin;
import com.eventticketing.util.SceneManager;
import com.eventticketing.util.SessionManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        SceneManager.setPrimaryStage(primaryStage);

        // Try to restore a session from a previous run using the serialized
        // session.dat file. If a valid session is found, skip the login
        // screen and go straight to the appropriate landing screen.
        String startFxml = "/com/eventticketing/view/Login.fxml";
        String startTitle = "Event Ticketing System - Login";
        if (SessionManager.getInstance().restoreSession()) {
            boolean isAdmin = SessionManager.getInstance().getCurrentUser() instanceof Admin;
            startFxml = isAdmin ? "/com/eventticketing/view/AdminDashboard.fxml"
                                 : "/com/eventticketing/view/BrowseEvents.fxml";
            startTitle = isAdmin ? "Admin Dashboard" : "Browse Events";
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource(startFxml));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/com/eventticketing/css/style.css").toExternalForm());

        primaryStage.setTitle(startTitle);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
