package com.eventticketing.controller;

import com.eventticketing.dao.EventDAO;
import com.eventticketing.model.Event;
import com.eventticketing.model.Seat;
import com.eventticketing.util.SceneManager;
import com.eventticketing.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EventDetailsController {

    // simple static hand-off from the events list -> details screen
    private static int selectedEventId;
    public static void setSelectedEventId(int id) { selectedEventId = id; }

    @FXML private Label badgeLabel;
    @FXML private Label titleLabel;
    @FXML private Label venueLabel;
    @FXML private Label dateLabel;
    @FXML private Label descriptionLabel;
    @FXML private FlowPane seatsFlowPane;
    @FXML private Label noSeatsLabel;
    @FXML private Label selectedCountLabel;
    @FXML private Label totalLabel;
    @FXML private Button cartButton;

    private final EventDAO eventDAO = new EventDAO();
    private Event currentEvent;
    private final List<Seat> selectedSeats = new ArrayList<>();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("EEEE, MMM d, yyyy 'at' h:mm a");

    @FXML
    public void initialize() {
        currentEvent = eventDAO.getEventById(selectedEventId);
        if (currentEvent == null) return;

        badgeLabel.setText(currentEvent.getCategoryName());
        titleLabel.setText(currentEvent.getTitle());
        venueLabel.setText("📍 " + currentEvent.getVenueName());
        dateLabel.setText("🗓 " + (currentEvent.getDateTime() != null ? currentEvent.getDateTime().format(FMT) : "TBA"));
        descriptionLabel.setText(currentEvent.getDescription() != null ? currentEvent.getDescription() : "No description available.");

        loadSeats();
        updateSummary();
        updateCartButton();
    }

    private void updateCartButton() {
        int count = SessionManager.getInstance().getCart().size();
        cartButton.setText(count > 0 ? "🛒 Cart (" + count + ")" : "🛒 Cart");
    }

    private void loadSeats() {
        seatsFlowPane.getChildren().clear();
        List<Seat> seats = eventDAO.getAvailableSeats(currentEvent.getEventId());
        if (seats.isEmpty()) {
            noSeatsLabel.setManaged(true);
            noSeatsLabel.setVisible(true);
            return;
        }
        for (Seat seat : seats) {
            Button seatBtn = new Button(seat.getSeatNumber());
            seatBtn.getStyleClass().addAll("seat-btn", "seat-available");
            seatBtn.setOnAction(e -> toggleSeat(seat, seatBtn));
            seatsFlowPane.getChildren().add(seatBtn);
        }
    }

    private void toggleSeat(Seat seat, Button btn) {
        if (selectedSeats.contains(seat)) {
            selectedSeats.remove(seat);
            btn.getStyleClass().remove("seat-selected");
            btn.getStyleClass().add("seat-available");
        } else {
            selectedSeats.add(seat);
            btn.getStyleClass().remove("seat-available");
            btn.getStyleClass().add("seat-selected");
        }
        updateSummary();
    }

    private void updateSummary() {
        selectedCountLabel.setText(selectedSeats.size() + " seat(s)");
        double total = selectedSeats.stream().mapToDouble(Seat::getPrice).sum();
        totalLabel.setText("₱" + String.format("%,.2f", total));
    }

    @FXML
    private void handleAddToCart() {
        if (SessionManager.getInstance().getCurrentUser() == null) {
            SceneManager.switchTo("/com/eventticketing/view/Login.fxml", "Login");
            return;
        }
        if (selectedSeats.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please select at least one seat.").showAndWait();
            return;
        }
        for (Seat seat : selectedSeats) {
            SessionManager.getInstance().addToCart(currentEvent, seat);
        }
        new Alert(Alert.AlertType.INFORMATION, selectedSeats.size() + " ticket(s) added to your cart.").showAndWait();
        SceneManager.switchTo("/com/eventticketing/view/Cart.fxml", "Your Cart");
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
    private void goToCart() {
        SceneManager.switchTo("/com/eventticketing/view/Cart.fxml", "Your Cart");
    }

    @FXML
    private void handleLogout() {
        SessionManager.getInstance().logout();
        SceneManager.switchTo("/com/eventticketing/view/Login.fxml", "Login");
    }
}
