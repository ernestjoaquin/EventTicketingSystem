package com.eventticketing.controller;

import com.eventticketing.model.Seat;
import com.eventticketing.util.SceneManager;
import com.eventticketing.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class CartController {

    @FXML private VBox cartItemsBox;
    @FXML private Label eventNameLabel;
    @FXML private Label totalLabel;
    @FXML private Label emptyLabel;

    @FXML
    public void initialize() {
        refresh();
    }

    private void refresh() {
        cartItemsBox.getChildren().clear();
        SessionManager session = SessionManager.getInstance();

        if (session.getCart().isEmpty()) {
            emptyLabel.setManaged(true);
            emptyLabel.setVisible(true);
            eventNameLabel.setText("");
            totalLabel.setText("₱0.00");
            return;
        }
        emptyLabel.setManaged(false);
        emptyLabel.setVisible(false);

        eventNameLabel.setText(session.getCartEvent().getTitle());

        for (Seat seat : new java.util.ArrayList<>(session.getCart())) {
            HBox row = new HBox(12);
            row.getStyleClass().add("card");
            row.setStyle(row.getStyle() + "-fx-padding: 12;");

            Label label = new Label(seat.getSection() + " - Seat " + seat.getSeatNumber());
            label.getStyleClass().add("event-card-title");
            label.setStyle("-fx-font-size: 13px;");

            Region spacer = new Region();
            HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

            Label price = new Label("₱" + String.format("%,.2f", seat.getPrice()));
            price.getStyleClass().add("event-card-price");

            Button remove = new Button("Remove");
            remove.getStyleClass().add("btn-link");
            remove.setOnAction(e -> {
                session.removeFromCart(seat);
                refresh();
            });

            row.getChildren().addAll(label, spacer, price, remove);
            cartItemsBox.getChildren().add(row);
        }

        totalLabel.setText("₱" + String.format("%,.2f", session.getCartTotal()));
    }

    @FXML
    private void handleCheckout() {
        if (SessionManager.getInstance().getCart().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Your cart is empty.").showAndWait();
            return;
        }
        SceneManager.switchTo("/com/eventticketing/view/Checkout.fxml", "Checkout");
    }

    @FXML
    private void goToBrowse() {
        SceneManager.switchTo("/com/eventticketing/view/BrowseEvents.fxml", "Browse Events");
    }

    @FXML
    private void goToMyTickets() {
        SceneManager.switchTo("/com/eventticketing/view/MyTickets.fxml", "My Tickets");
    }

    @FXML
    private void handleLogout() {
        SessionManager.getInstance().logout();
        SceneManager.switchTo("/com/eventticketing/view/Login.fxml", "Login");
    }
}
