package com.eventticketing.controller;

import com.eventticketing.dao.BookingDAO;
import com.eventticketing.model.Booking;
import com.eventticketing.model.Customer;
import com.eventticketing.model.Seat;
import com.eventticketing.util.SceneManager;
import com.eventticketing.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class CheckoutController {

    @FXML private Label eventLabel;
    @FXML private Label itemsLabel;
    @FXML private Label totalLabel;
    @FXML private ComboBox<String> methodCombo;
    @FXML private TextField cardField;
    @FXML private TextField expiryField;
    @FXML private TextField cvvField;
    @FXML private Label statusLabel;

    private final BookingDAO bookingDAO = new BookingDAO();

    @FXML
    public void initialize() {
        SessionManager session = SessionManager.getInstance();
        if (session.getCart().isEmpty()) {
            SceneManager.switchTo("/com/eventticketing/view/Cart.fxml", "Your Cart");
            return;
        }
        eventLabel.setText("Event: " + session.getCartEvent().getTitle());
        itemsLabel.setText(session.getCart().size() + " ticket(s)");
        totalLabel.setText("₱" + String.format("%,.2f", session.getCartTotal()));

        methodCombo.getItems().addAll("Credit Card", "Debit Card", "GCash", "PayMaya");
        methodCombo.getSelectionModel().selectFirst();
    }

    @FXML
    private void handlePay() {
        SessionManager session = SessionManager.getInstance();

        if (cardField.getText().isBlank() || expiryField.getText().isBlank() || cvvField.getText().isBlank()) {
            showStatus("Please fill in all payment details.", true);
            return;
        }

        Customer customer = (Customer) session.getCurrentUser();
        java.util.List<Seat> seats = session.getCart();
        int eventId = session.getCartEvent().getEventId();
        String method = methodCombo.getValue();

        Booking booking = bookingDAO.checkout(customer.getCustomerId(), eventId, seats, method);

        if (booking == null) {
            showStatus("Payment failed — one or more seats may have just been taken. Please try again.", true);
            return;
        }

        session.clearCart();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Booking Confirmed");
        alert.setHeaderText("🎉 Your e-ticket has been generated!");
        alert.setContentText("Booking #" + booking.getBookingId() + " confirmed.\n" +
                "A confirmation email has been sent to you.\nView it anytime under 'My Tickets'.");
        alert.showAndWait();

        SceneManager.switchTo("/com/eventticketing/view/MyTickets.fxml", "My Tickets");
    }

    private void showStatus(String msg, boolean isError) {
        statusLabel.setText(msg);
        statusLabel.getStyleClass().removeAll("error-text", "success-text");
        statusLabel.getStyleClass().add(isError ? "error-text" : "success-text");
        statusLabel.setManaged(true);
        statusLabel.setVisible(true);
    }

    @FXML
    private void goToCart() {
        SceneManager.switchTo("/com/eventticketing/view/Cart.fxml", "Your Cart");
    }

    @FXML
    private void goToBrowse() {
        SceneManager.switchTo("/com/eventticketing/view/BrowseEvents.fxml", "Browse Events");
    }
}
