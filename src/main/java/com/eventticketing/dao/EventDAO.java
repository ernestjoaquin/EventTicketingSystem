package com.eventticketing.dao;

import com.eventticketing.datastore.DataStore;
import com.eventticketing.model.Category;
import com.eventticketing.model.Event;
import com.eventticketing.model.Seat;
import com.eventticketing.model.Venue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EventDAO {

    private final DataStore store = DataStore.getInstance();

    public List<Event> getAllEvents() {
        List<Event> list = new ArrayList<>();
        for (Event e : store.events) {
            list.add(withJoinedNames(e));
        }
        list.sort(Comparator.comparing(Event::getDateTime, Comparator.nullsLast(Comparator.naturalOrder())));
        return list;
    }

    /** Search events by keyword (title) and/or category. Pass null/empty to skip a filter. */
    public List<Event> searchEvents(String keyword, Integer categoryId) {
        List<Event> list = new ArrayList<>();
        boolean hasKeyword = keyword != null && !keyword.isBlank();
        String needle = hasKeyword ? keyword.toLowerCase() : null;

        for (Event e : store.events) {
            if (hasKeyword && !e.getTitle().toLowerCase().contains(needle)) continue;
            if (categoryId != null && e.getCategoryId() != categoryId) continue;
            list.add(withJoinedNames(e));
        }
        list.sort(Comparator.comparing(Event::getDateTime, Comparator.nullsLast(Comparator.naturalOrder())));
        return list;
    }

    public Event getEventById(int eventId) {
        for (Event e : store.events) {
            if (e.getEventId() == eventId) return withJoinedNames(e);
        }
        return null;
    }

    public List<Seat> getAvailableSeats(int eventId) {
        List<Seat> result = new ArrayList<>();
        for (Seat s : store.seats) {
            if (s.getEventId() == eventId && "AVAILABLE".equalsIgnoreCase(s.getStatus())) {
                result.add(s);
            }
        }
        result.sort(Comparator.comparing(Seat::getSeatNumber));
        return result;
    }

    /** Creates a new event and auto-generates its "General" seats. */
    public int addEvent(String title, String description, LocalDateTime dateTime,
                         int venueId, int categoryId, int totalSeats, double pricePerSeat) {
        Event event = new Event(store.eventIdSeq.incrementAndGet(), title, description, dateTime,
                venueId, categoryId, totalSeats, totalSeats, null, LocalDateTime.now());
        store.events.add(event);

        for (int i = 1; i <= totalSeats; i++) {
            store.seats.add(new Seat(store.seatIdSeq.incrementAndGet(), event.getEventId(), "General",
                    String.format("S-%03d", i), pricePerSeat, "AVAILABLE"));
        }
        return event.getEventId();
    }

    public boolean updateEvent(int eventId, String title, String description, LocalDateTime dateTime) {
        for (Event e : store.events) {
            if (e.getEventId() == eventId) {
                e.updateDetails(title, description, dateTime);
                return true;
            }
        }
        return false;
    }

    public boolean deleteEvent(int eventId) {
        boolean removed = store.events.removeIf(e -> e.getEventId() == eventId);
        if (removed) {
            store.seats.removeIf(s -> s.getEventId() == eventId);
        }
        return removed;
    }

    /** Fills in the venue/category display names, mirroring the SQL JOINs the old queries used. */
    private Event withJoinedNames(Event e) {
        for (Venue v : store.venues) {
            if (v.getVenueId() == e.getVenueId()) {
                e.setVenueName(v.getName());
                break;
            }
        }
        for (Category c : store.categories) {
            if (c.getCategoryId() == e.getCategoryId()) {
                e.setCategoryName(c.getName());
                break;
            }
        }
        return e;
    }
}
