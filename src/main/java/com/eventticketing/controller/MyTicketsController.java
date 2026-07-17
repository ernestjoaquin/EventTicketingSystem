package com.eventticketing.controller;

import com.eventticketing.dao.BookingDAO;
import com.eventticketing.model.Booking;
import com.eventticketing.model.BookingItem;
import com.eventticketing.model.Customer;
import com.eventticketing.util.SceneManager;
import com.eventticketing.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class MyTicketsController {

    @FXML private VBox ticketsBox;
    @FXML private Button cartButton;

    private final BookingDAO bookingDAO = new BookingDAO();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a");

    @FXML
    public void initialize() {
        loadTickets();
        updateCartButton();
    }

    private void updateCartButton() {
        int count = SessionManager.getInstance().getCart().size();
        cartButton.setText(count > 0 ? "🛒 Cart (" + count + ")" : "🛒 Cart");
    }

    @FXML
    private void goToCart() {
        SceneManager.switchTo("/com/eventticketing/view/Cart.fxml", "Your Cart");
    }

    private void loadTickets() {
        ticketsBox.getChildren().clear();
        Customer customer = (Customer) SessionManager.getInstance().getCurrentUser();
        List<Booking> bookings = bookingDAO.getBookingsForCustomer(customer.getCustomerId());

        if (bookings.isEmpty()) {
            Label empty = new Label("You have no bookings yet. Browse events to get started!");
            empty.getStyleClass().add("subtle-text");
            ticketsBox.getChildren().add(empty);
            return;
        }

        for (Booking b : bookings) {
            ticketsBox.getChildren().add(buildBookingCard(b));
        }
    }

    private VBox buildBookingCard(Booking b) {
        VBox card = new VBox(8);
        card.getStyleClass().add("card");

        HBox header = new HBox(10);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label title = new Label(b.getEventTitle());
        title.getStyleClass().add("event-card-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Label status = new Label(b.getStatus());
        status.getStyleClass().add(statusStyleClass(b.getStatus()));

        header.getChildren().addAll(title, spacer, status);

        Label date = new Label("🗓 " + (b.getEventDateTime() != null ? b.getEventDateTime().format(FMT) : "TBA"));
        date.getStyleClass().add("event-card-meta");

        Label bookingInfo = new Label("Booking #" + b.getBookingId() + " • Booked on " + b.getBookingDate().format(FMT));
        bookingInfo.getStyleClass().add("subtle-text");

        StringBuilder seatsStr = new StringBuilder("Seats: ");
        for (BookingItem item : b.getItems()) {
            seatsStr.append(item.getSeatLabel()).append("  ");
        }
        Label seatsLabel = new Label(seatsStr.toString());
        seatsLabel.setWrapText(true);

        Label total = new Label("Total paid: ₱" + String.format("%,.2f", b.getTotalAmount()));
        total.getStyleClass().add("event-card-price");

        HBox actions = new HBox(10);
        actions.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        if ("CONFIRMED".equals(b.getStatus())) {
            Button cancelBtn = new Button("Cancel Ticket");
            cancelBtn.getStyleClass().add("btn-danger");
            cancelBtn.setOnAction(e -> handleCancel(b));
            actions.getChildren().add(cancelBtn);
        }

        card.getChildren().addAll(header, date, bookingInfo, seatsLabel, total, actions);
        return card;
    }

    private String statusStyleClass(String status) {
        return switch (status) {
            case "CONFIRMED" -> "status-confirmed";
            case "CANCELLED" -> "status-cancelled";
            default -> "status-pending";
        };
    }

    private void handleCancel(Booking b) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Cancel booking #" + b.getBookingId() + " for " + b.getEventTitle() + "?",
                ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            boolean ok = bookingDAO.cancelBooking(b.getBookingId());
            if (ok) {
                new Alert(Alert.AlertType.INFORMATION, "Booking cancelled. Your seats have been released.").showAndWait();
                loadTickets();
            } else {
                new Alert(Alert.AlertType.ERROR, "Could not cancel this booking. Please try again.").showAndWait();
            }
        }
    }

    @FXML
    private void refreshView() { loadTickets(); }

    @FXML
    private void goToBrowse() {
        SceneManager.switchTo("/com/eventticketing/view/BrowseEvents.fxml", "Browse Events");
    }

    @FXML
    private void handleLogout() {
        SessionManager.getInstance().logout();
        SceneManager.switchTo("/com/eventticketing/view/Login.fxml", "Login");
    }
}
