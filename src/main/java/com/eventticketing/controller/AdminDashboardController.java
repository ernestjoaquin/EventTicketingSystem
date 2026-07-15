package com.eventticketing.controller;

import com.eventticketing.dao.*;
import com.eventticketing.model.*;
import com.eventticketing.util.SceneManager;
import com.eventticketing.util.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdminDashboardController {

    // ---- Top bar ----
    @FXML private Label userLabel;

    // ---- Manage Events ----
    @FXML private TextField eventTitleField;
    @FXML private ComboBox<Category> eventCategoryCombo;
    @FXML private ComboBox<Venue> eventVenueCombo;
    @FXML private DatePicker eventDatePicker;
    @FXML private TextField eventTimeField;
    @FXML private TextField eventSeatsField;
    @FXML private TextField eventPriceField;
    @FXML private TextArea eventDescField;
    @FXML private Label eventFormStatus;
    @FXML private TableView<Event> eventsTable;
    @FXML private TableColumn<Event, Number> colEventId;
    @FXML private TableColumn<Event, String> colEventTitle;
    @FXML private TableColumn<Event, String> colEventDate;
    @FXML private TableColumn<Event, String> colEventVenue;
    @FXML private TableColumn<Event, String> colEventCategory;
    @FXML private TableColumn<Event, Number> colEventSeats;
    @FXML private TableColumn<Event, Void> colEventActions;

    // ---- Manage Categories ----
    @FXML private TextField categoryNameField;
    @FXML private TextField categoryDescField;
    @FXML private TableView<Category> categoriesTable;
    @FXML private TableColumn<Category, Number> colCatId;
    @FXML private TableColumn<Category, String> colCatName;
    @FXML private TableColumn<Category, String> colCatDesc;
    @FXML private TableColumn<Category, Void> colCatActions;

    // ---- Manage Users ----
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, Number> colUserId;
    @FXML private TableColumn<User, String> colUserName;
    @FXML private TableColumn<User, String> colUserEmail;
    @FXML private TableColumn<User, String> colUserPhone;
    @FXML private TableColumn<User, Void> colUserActions;

    // ---- Bookings & Reports ----
    @FXML private Label statTotalBookings;
    @FXML private Label statConfirmed;
    @FXML private Label statCancelled;
    @FXML private Label statRevenue;
    @FXML private TableView<Booking> bookingsTable;
    @FXML private TableColumn<Booking, Number> colBookingId;
    @FXML private TableColumn<Booking, String> colBookingCustEvent;
    @FXML private TableColumn<Booking, String> colBookingDate;
    @FXML private TableColumn<Booking, String> colBookingAmount;
    @FXML private TableColumn<Booking, String> colBookingStatus;

    private final EventDAO eventDAO = new EventDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final VenueDAO venueDAO = new VenueDAO();
    private final UserDAO userDAO = new UserDAO();
    private final BookingDAO bookingDAO = new BookingDAO();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a");

    @FXML
    public void initialize() {
        User current = SessionManager.getInstance().getCurrentUser();
        if (current != null) userLabel.setText("Logged in as " + current.getName());

        setupEventsTab();
        setupCategoriesTab();
        setupUsersTab();
        setupBookingsTab();

        refreshAll();
    }

    private void refreshAll() {
        loadCategories();
        loadVenues();
        loadEvents();
        loadCategoriesTable();
        loadUsers();
        loadBookings();
    }

    // ================= EVENTS =================
    private void setupEventsTab() {
        colEventId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getEventId()));
        colEventTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colEventDate.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDateTime() != null ? c.getValue().getDateTime().format(FMT) : ""));
        colEventVenue.setCellValueFactory(new PropertyValueFactory<>("venueName"));
        colEventCategory.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        colEventSeats.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getAvailableSeats()));

        colEventActions.setCellFactory(col -> new TableCell<>() {
            private final Button deleteBtn = new Button("Delete");
            {
                deleteBtn.getStyleClass().add("btn-danger");
                deleteBtn.setOnAction(e -> {
                    Event event = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                            "Delete event \"" + event.getTitle() + "\"? This cannot be undone.",
                            ButtonType.YES, ButtonType.NO);
                    confirm.showAndWait().ifPresent(res -> {
                        if (res == ButtonType.YES) {
                            eventDAO.deleteEvent(event.getEventId());
                            loadEvents();
                        }
                    });
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteBtn);
            }
        });
    }

    private void loadCategories() {
        eventCategoryCombo.setItems(FXCollections.observableArrayList(categoryDAO.getAllCategories()));
    }

    private void loadVenues() {
        eventVenueCombo.setItems(FXCollections.observableArrayList(venueDAO.getAllVenues()));

        // Lets the admin either pick an existing venue from the dropdown
        // or just type a brand-new venue name straight into the box.
        eventVenueCombo.setConverter(new javafx.util.StringConverter<Venue>() {
            @Override
            public String toString(Venue venue) {
                return venue == null ? "" : venue.getName();
            }

            @Override
            public Venue fromString(String text) {
                if (text == null || text.trim().isEmpty()) return null;
                String trimmed = text.trim();
                // Reuse the existing venue if the typed name matches one already in the list.
                for (Venue v : eventVenueCombo.getItems()) {
                    if (v.getName().equalsIgnoreCase(trimmed)) return v;
                }
                // Otherwise, treat it as a brand-new venue (venueId 0 = not yet saved).
                return new Venue(0, trimmed, "", 0);
            }
        });
    }

    private void loadEvents() {
        eventsTable.setItems(FXCollections.observableArrayList(eventDAO.getAllEvents()));
    }

    @FXML
    private void handleAddEvent() {
        String title = eventTitleField.getText().trim();
        Category category = eventCategoryCombo.getValue();
        Venue venue = eventVenueCombo.getValue();
        LocalDate date = eventDatePicker.getValue();
        String timeStr = eventTimeField.getText().trim();
        String seatsStr = eventSeatsField.getText().trim();
        String priceStr = eventPriceField.getText().trim();
        String desc = eventDescField.getText().trim();

        if (title.isEmpty() || category == null || venue == null || date == null
                || timeStr.isEmpty() || seatsStr.isEmpty() || priceStr.isEmpty()) {
            showEventFormError("Please fill in all fields.");
            return;
        }

        try {
            LocalTime time = LocalTime.parse(timeStr);
            LocalDateTime dateTime = LocalDateTime.of(date, time);
            int seats = Integer.parseInt(seatsStr);
            double price = Double.parseDouble(priceStr);

            // If the admin typed a venue name that isn't in the dropdown yet,
            // save it as a new venue first so we have a real venue_id to use.
            if (venue.getVenueId() == 0) {
                Venue existing = venueDAO.findByName(venue.getName());
                if (existing != null) {
                    venue = existing;
                } else {
                    int newVenueId = venueDAO.addVenueReturnId(venue.getName(), "", 0);
                    if (newVenueId == -1) {
                        showEventFormError("Failed to save the new venue. Please try again.");
                        return;
                    }
                    venue.setVenueId(newVenueId);
                }
            }

            int id = eventDAO.addEvent(title, desc, dateTime, venue.getVenueId(), category.getCategoryId(), seats, price);
            if (id == -1) {
                showEventFormError("Failed to add event. Please check your inputs.");
                return;
            }

            eventTitleField.clear();
            eventDescField.clear();
            eventSeatsField.clear();
            eventPriceField.clear();
            eventTimeField.clear();
            eventDatePicker.setValue(null);
            eventVenueCombo.setValue(null);
            eventVenueCombo.getEditor().clear();
            eventFormStatus.setVisible(false);
            eventFormStatus.setManaged(false);
            loadEvents();
            loadVenues();
        } catch (java.time.format.DateTimeParseException ex) {
            showEventFormError("Time must be in HH:MM 24-hour format, e.g. 19:00");
        } catch (NumberFormatException ex) {
            showEventFormError("Seats and price must be numeric.");
        }
    }

    private void showEventFormError(String msg) {
        eventFormStatus.setText(msg);
        eventFormStatus.setManaged(true);
        eventFormStatus.setVisible(true);
    }

    // ================= CATEGORIES =================
    private void setupCategoriesTab() {
        colCatId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getCategoryId()));
        colCatName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCatDesc.setCellValueFactory(new PropertyValueFactory<>("description"));

        colCatActions.setCellFactory(col -> new TableCell<>() {
            private final Button deleteBtn = new Button("Delete");
            {
                deleteBtn.getStyleClass().add("btn-danger");
                deleteBtn.setOnAction(e -> {
                    Category cat = getTableView().getItems().get(getIndex());
                    categoryDAO.deleteCategory(cat.getCategoryId());
                    loadCategoriesTable();
                    loadCategories();
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteBtn);
            }
        });
    }

    private void loadCategoriesTable() {
        categoriesTable.setItems(FXCollections.observableArrayList(categoryDAO.getAllCategories()));
    }

    @FXML
    private void handleAddCategory() {
        String name = categoryNameField.getText().trim();
        String desc = categoryDescField.getText().trim();
        if (name.isEmpty()) return;
        categoryDAO.addCategory(name, desc);
        categoryNameField.clear();
        categoryDescField.clear();
        loadCategoriesTable();
        loadCategories();
    }

    // ================= USERS =================
    private void setupUsersTab() {
        colUserId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getUserId()));
        colUserName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colUserEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colUserPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));

        colUserActions.setCellFactory(col -> new TableCell<>() {
            private final Button deleteBtn = new Button("Delete");
            {
                deleteBtn.getStyleClass().add("btn-danger");
                deleteBtn.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                            "Delete customer \"" + user.getName() + "\"? This also removes their bookings.",
                            ButtonType.YES, ButtonType.NO);
                    confirm.showAndWait().ifPresent(res -> {
                        if (res == ButtonType.YES) {
                            userDAO.deleteUser(user.getUserId());
                            loadUsers();
                        }
                    });
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteBtn);
            }
        });
    }

    private void loadUsers() {
        usersTable.setItems(FXCollections.observableArrayList(userDAO.getAllCustomers()));
    }

    // ================= BOOKINGS / REPORTS =================
    private void setupBookingsTab() {
        colBookingId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getBookingId()));
        colBookingCustEvent.setCellValueFactory(new PropertyValueFactory<>("eventTitle"));
        colBookingDate.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBookingDate().format(FMT)));
        colBookingAmount.setCellValueFactory(c -> new SimpleStringProperty("₱" + String.format("%,.2f", c.getValue().getTotalAmount())));
        colBookingStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadBookings() {
        List<Booking> bookings = bookingDAO.getAllBookings();
        bookingsTable.setItems(FXCollections.observableArrayList(bookings));

        long confirmed = bookings.stream().filter(b -> "CONFIRMED".equals(b.getStatus())).count();
        long cancelled = bookings.stream().filter(b -> "CANCELLED".equals(b.getStatus())).count();
        double revenue = bookings.stream().filter(b -> "CONFIRMED".equals(b.getStatus()))
                .mapToDouble(Booking::getTotalAmount).sum();

        statTotalBookings.setText(String.valueOf(bookings.size()));
        statConfirmed.setText(String.valueOf(confirmed));
        statCancelled.setText(String.valueOf(cancelled));
        statRevenue.setText("₱" + String.format("%,.2f", revenue));
    }

    @FXML
    private void handleLogout() {
        SessionManager.getInstance().logout();
        SceneManager.switchTo("/com/eventticketing/view/Login.fxml", "Login");
    }
}
