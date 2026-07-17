package com.eventticketing.controller;

import com.eventticketing.dao.CategoryDAO;
import com.eventticketing.dao.EventDAO;
import com.eventticketing.model.Category;
import com.eventticketing.model.Event;
import com.eventticketing.util.SceneManager;
import com.eventticketing.util.SessionManager;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class BrowseEventsController {

    @FXML private FlowPane eventsFlowPane;
    @FXML private TextField searchField;
    @FXML private ComboBox<Category> categoryCombo;
    @FXML private Label userLabel;
    @FXML private Button cartButton;

    private final EventDAO eventDAO = new EventDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a");

    @FXML
    public void initialize() {
        if (SessionManager.getInstance().getCurrentUser() != null) {
            userLabel.setText("Hi, " + SessionManager.getInstance().getCurrentUser().getName());
        }

        List<Category> categories = categoryDAO.getAllCategories();
        categoryCombo.getItems().add(null); // "All"
        categoryCombo.getItems().addAll(categories);
        categoryCombo.setPromptText("All Categories");

        loadEvents(eventDAO.getAllEvents());
        updateCartButton();
    }

    private void updateCartButton() {
        int count = SessionManager.getInstance().getCart().size();
        cartButton.setText(count > 0 ? "🛒 Cart (" + count + ")" : "🛒 Cart");
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText();
        Category selected = categoryCombo.getValue();
        Integer categoryId = selected != null ? selected.getCategoryId() : null;
        loadEvents(eventDAO.searchEvents(keyword, categoryId));
    }

    private void loadEvents(List<Event> events) {
        eventsFlowPane.getChildren().clear();
        if (events.isEmpty()) {
            Label empty = new Label("No events found. Try a different search.");
            empty.getStyleClass().add("subtle-text");
            eventsFlowPane.getChildren().add(empty);
            return;
        }
        for (Event e : events) {
            eventsFlowPane.getChildren().add(buildEventCard(e));
        }
    }

    private VBox buildEventCard(Event e) {
        VBox card = new VBox(8);
        card.getStyleClass().add("card");
        card.setPrefWidth(260);
        card.setPrefHeight(190);

        Label badge = new Label(e.getCategoryName());
        badge.getStyleClass().add("badge");

        Label title = new Label(e.getTitle());
        title.getStyleClass().add("event-card-title");
        title.setWrapText(true);

        Label venue = new Label("📍 " + e.getVenueName());
        venue.getStyleClass().add("event-card-meta");
        venue.setWrapText(true);

        Label date = new Label("🗓 " + (e.getDateTime() != null ? e.getDateTime().format(FMT) : "TBA"));
        date.getStyleClass().add("event-card-meta");

        Label seats = new Label(e.getAvailableSeats() > 0
                ? e.getAvailableSeats() + " seats available"
                : "Sold out");
        seats.getStyleClass().add("event-card-meta");

        Region spacer = new Region();
        VBox.setVgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Button viewBtn = new Button("View Details");
        viewBtn.getStyleClass().add("btn-primary");
        viewBtn.setMaxWidth(Double.MAX_VALUE);
        viewBtn.setOnAction(ev -> openEventDetails(e.getEventId()));

        card.getChildren().addAll(badge, title, venue, date, seats, spacer, viewBtn);
        return card;
    }

    private void openEventDetails(int eventId) {
        EventDetailsController.setSelectedEventId(eventId);
        SceneManager.switchTo("/com/eventticketing/view/EventDetails.fxml", "Event Details");
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
