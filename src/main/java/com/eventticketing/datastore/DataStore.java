package com.eventticketing.datastore;

import com.eventticketing.model.*;
import com.eventticketing.util.PasswordUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * In-memory replacement for the MySQL database.
 *
 * This class holds all "tables" as plain Java collections instead of rows in
 * a database. It exists so the DAO classes can keep their exact same method
 * signatures (and the controllers don't need to change at all) while the
 * project runs with no database/XAMPP/MySQL setup required.
 *
 * Data only lives as long as the application is running — closing the app
 * resets everything back to the seed data below. Once a real database is
 * ready, only the DAO classes need to be swapped back to JDBC; nothing in
 * the controllers, models, or FXML needs to change.
 */
public class DataStore {

    private static final DataStore INSTANCE = new DataStore();

    public static DataStore getInstance() { return INSTANCE; }

    // ---- "tables" ----
    public final List<User> users = new ArrayList<>();
    public final List<Venue> venues = new ArrayList<>();
    public final List<Category> categories = new ArrayList<>();
    public final List<Event> events = new ArrayList<>();
    public final List<Seat> seats = new ArrayList<>();
    public final List<Booking> bookings = new ArrayList<>();
    public final List<BookingItem> bookingItems = new ArrayList<>();
    public final List<Payment> payments = new ArrayList<>();


    // ---- auto-increment counters (mirrors AUTO_INCREMENT columns) ----
    public final AtomicInteger userIdSeq = new AtomicInteger(0);
    public final AtomicInteger venueIdSeq = new AtomicInteger(0);
    public final AtomicInteger categoryIdSeq = new AtomicInteger(0);
    public final AtomicInteger eventIdSeq = new AtomicInteger(0);
    public final AtomicInteger seatIdSeq = new AtomicInteger(0);
    public final AtomicInteger bookingIdSeq = new AtomicInteger(0);
    public final AtomicInteger bookingItemIdSeq = new AtomicInteger(0);
    public final AtomicInteger paymentIdSeq = new AtomicInteger(0);

    private DataStore() {
        seed();
    }

    /** Loads the same starting data that used to live in database/schema.sql. */
    private void seed() {
        // Default admin account -> password: admin123
        Admin admin = new Admin(userIdSeq.incrementAndGet(), "System Admin", "admin@ticketing.com",
                "09171234567", PasswordUtil.hash("admin123"), LocalDateTime.now());
        users.add(admin);

        Customer customers = new Customer(userIdSeq.incrementAndGet(), "Ernest Abella", "ernestabella@gmail.com",
                "09123456789", PasswordUtil.hash("123456"), LocalDateTime.now());
        users.add(customers);

        Category concert = new Category(categoryIdSeq.incrementAndGet(), "Concert", "Live music concerts and performances");
        Category conference = new Category(categoryIdSeq.incrementAndGet(), "Conference", "Business and tech conferences");
        Category sports = new Category(categoryIdSeq.incrementAndGet(), "Sports", "Sporting events and matches");
        Category theater = new Category(categoryIdSeq.incrementAndGet(), "Theater", "Theatrical plays and musicals");
        categories.add(concert);
        categories.add(conference);
        categories.add(sports);
        categories.add(theater);

        Venue mallOfAsia = new Venue(venueIdSeq.incrementAndGet(), "SM Mall of Asia Arena", "Pasay City, Metro Manila", 20000);
        Venue araneta = new Venue(venueIdSeq.incrementAndGet(), "Araneta Coliseum", "Cubao, Quezon City", 16000);
        Venue bacolod = new Venue(venueIdSeq.incrementAndGet(), "Bacolod Convention Center", "Bacolod City, Negros Occidental", 3000);
        venues.add(mallOfAsia);
        venues.add(araneta);
        venues.add(bacolod);

        Event musicFest = addSeedEvent("Summer Music Fest", "A night of live bands and top artists.",
                LocalDateTime.of(2026, 8, 15, 19, 0), mallOfAsia, concert, 100, 1500.00);
        Event techConf = addSeedEvent("Tech Innovators Conference", "Talks from leading tech innovators.",
                LocalDateTime.of(2026, 9, 5, 9, 0), bacolod, conference, 60, 2500.00);
        Event basketball = addSeedEvent("Championship Basketball Finals", "The biggest basketball showdown of the year.",
                LocalDateTime.of(2026, 8, 28, 18, 30), araneta, sports, 80, 800.00);
    }

    private Event addSeedEvent(String title, String description, LocalDateTime dateTime,
                                Venue venue, Category category, int totalSeats, double pricePerSeat) {
        Event event = new Event(eventIdSeq.incrementAndGet(), title, description, dateTime,
                venue.getVenueId(), category.getCategoryId(), totalSeats, totalSeats, null, LocalDateTime.now());
        event.setVenueName(venue.getName());
        event.setCategoryName(category.getName());
        events.add(event);

        for (int i = 1; i <= totalSeats; i++) {
            seats.add(new Seat(seatIdSeq.incrementAndGet(), event.getEventId(), "General",
                    String.format("S-%03d", i), pricePerSeat, "AVAILABLE"));
        }
        return event;
    }
}
