package com.eventticketing.dao;

import com.eventticketing.datastore.DataStore;
import com.eventticketing.model.Booking;
import com.eventticketing.model.BookingItem;
import com.eventticketing.model.Event;
import com.eventticketing.model.Payment;
import com.eventticketing.model.Seat;
import com.eventticketing.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Implements the "Book Tickets" flow from the sequence/activity diagrams:
 * Create Booking (Pending) -> Request/Process Payment -> Update Booking (Confirmed)
 * -> decrement seat availability -> (Generate E-Ticket handled at the UI layer).
 *
 * This version keeps everything in memory (via DataStore) instead of a real
 * database, but the booking/payment/seat-locking logic works the same way.
 */
public class BookingDAO {

    private final DataStore store = DataStore.getInstance();

    /**
     * Creates a booking + booking_items for the given seats, then simulates payment.
     * Returns the confirmed Booking, or null if it failed (e.g. a seat got taken).
     */
    public synchronized Booking checkout(int customerId, int eventId, List<Seat> seats, String paymentMethod) {
        // 1. Verify every seat is still available (mirrors the SELECT ... FOR UPDATE check)
        for (Seat seat : seats) {
            Seat current = findSeat(seat.getSeatId());
            if (current == null || !"AVAILABLE".equalsIgnoreCase(current.getStatus())) {
                return null; // seat no longer available
            }
        }

        double total = seats.stream().mapToDouble(Seat::getPrice).sum();

        // 2. Create booking (PENDING)
        int bookingId = store.bookingIdSeq.incrementAndGet();
        Booking booking = new Booking(bookingId, customerId, LocalDateTime.now(), total, "PENDING");
        store.bookings.add(booking);

        // 3. Reserve each seat, create booking_items
        for (Seat seat : seats) {
            Seat current = findSeat(seat.getSeatId());
            current.setStatus("SOLD");

            BookingItem item = new BookingItem(store.bookingItemIdSeq.incrementAndGet(), bookingId,
                    eventId, seat.getSeatId(), seat.getPrice(), "ACTIVE");
            item.setSeatLabel(current.getSection() + " - " + current.getSeatNumber());
            store.bookingItems.add(item);
        }

        // 4. Decrement event available_seats
        Event event = findEvent(eventId);
        if (event != null) {
            event.setAvailableSeats(event.getAvailableSeats() - seats.size());
        }

        // 5. Process payment (simulated gateway call)
        Payment payment = new Payment();
        payment.setPaymentId(store.paymentIdSeq.incrementAndGet());
        payment.setBookingId(bookingId);
        payment.setAmount(total);
        payment.setMethod(paymentMethod);
        boolean success = payment.processPayment();
        store.payments.add(payment);

        if (!success) {
            // roll back: free the seats, restore availability, cancel booking
            for (Seat seat : seats) {
                Seat current = findSeat(seat.getSeatId());
                if (current != null) current.setStatus("AVAILABLE");
            }
            if (event != null) event.setAvailableSeats(event.getAvailableSeats() + seats.size());
            booking.setStatus("CANCELLED");
            return null;
        }

        // 6. Update booking -> CONFIRMED
        booking.setStatus("CONFIRMED");
        return booking;
    }

    /** Returns all bookings (with their event & seat info) for a given customer, for "My Tickets". */
    public List<Booking> getBookingsForCustomer(int customerId) {
        List<Booking> result = new ArrayList<>();
        for (Booking b : store.bookings) {
            if (b.getCustomerId() != customerId) continue;
            enrichWithEventInfo(b, false);
            b.setItems(getBookingItems(b.getBookingId()));
            result.add(b);
        }
        result.sort(Comparator.comparing(Booking::getBookingDate, Comparator.nullsLast(Comparator.reverseOrder())));
        return result;
    }

    public List<BookingItem> getBookingItems(int bookingId) {
        List<BookingItem> items = new ArrayList<>();
        for (BookingItem item : store.bookingItems) {
            if (item.getBookingId() == bookingId) items.add(item);
        }
        return items;
    }

    /** Cancels a booking: frees the seats and marks the booking CANCELLED (mirrors <<extend>> Cancel Ticket). */
    public synchronized boolean cancelBooking(int bookingId) {
        Booking booking = findBooking(bookingId);
        if (booking == null) return false;

        int eventId = 0;
        int seatCount = 0;
        for (BookingItem item : store.bookingItems) {
            if (item.getBookingId() != bookingId) continue;
            eventId = item.getEventId();
            Seat seat = findSeat(item.getSeatId());
            if (seat != null) seat.setStatus("AVAILABLE");
            seatCount++;
        }

        if (eventId != 0) {
            Event event = findEvent(eventId);
            if (event != null) event.setAvailableSeats(event.getAvailableSeats() + seatCount);
        }

        booking.setStatus("CANCELLED");
        return true;
    }

    /** Admin: all bookings across all customers, for reports. */
    public List<Booking> getAllBookings() {
        List<Booking> result = new ArrayList<>();
        for (Booking b : store.bookings) {
            enrichWithEventInfo(b, true);
            result.add(b);
        }
        result.sort(Comparator.comparing(Booking::getBookingDate, Comparator.nullsLast(Comparator.reverseOrder())));
        return result;
    }

    // ---- helpers ----

    private void enrichWithEventInfo(Booking b, boolean prefixCustomerName) {
        for (BookingItem item : store.bookingItems) {
            if (item.getBookingId() != b.getBookingId()) continue;
            Event event = findEvent(item.getEventId());
            if (event != null) {
                String label = prefixCustomerName
                        ? customerName(b.getCustomerId()) + " — " + event.getTitle()
                        : event.getTitle();
                b.setEventTitle(label);
                b.setEventDateTime(event.getDateTime());
            }
            break; // one representative event per booking, same as the old GROUP BY query
        }
    }

    private String customerName(int customerId) {
        User user = store.users.stream().filter(u -> u.getUserId() == customerId).findFirst().orElse(null);
        return user != null ? user.getName() : "Unknown";
    }

    private Seat findSeat(int seatId) {
        return store.seats.stream().filter(s -> s.getSeatId() == seatId).findFirst().orElse(null);
    }

    private Event findEvent(int eventId) {
        return store.events.stream().filter(e -> e.getEventId() == eventId).findFirst().orElse(null);
    }

    private Booking findBooking(int bookingId) {
        return store.bookings.stream().filter(b -> b.getBookingId() == bookingId).findFirst().orElse(null);
    }
}
