package com.eventticketing.dao;

import com.eventticketing.db.DBConnection;
import com.eventticketing.model.Booking;
import com.eventticketing.model.BookingItem;
import com.eventticketing.model.Payment;
import com.eventticketing.model.Seat;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements the "Book Tickets" flow from the sequence/activity diagrams:
 * Create Booking (Pending) -> Request/Process Payment -> Update Booking (Confirmed)
 * -> decrement seat availability -> (Generate E-Ticket handled at the UI layer).
 */
public class BookingDAO {

    /**
     * Creates a booking + booking_items for the given seats, then simulates payment.
     * Returns the confirmed Booking, or null if it failed (e.g. a seat got taken).
     */
    public Booking checkout(int customerId, int eventId, List<Seat> seats, String paymentMethod) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            double total = seats.stream().mapToDouble(Seat::getPrice).sum();

            // 1. Create booking (PENDING)
            int bookingId;
            String insertBooking = "INSERT INTO bookings (customer_id, total_amount, status) VALUES (?, ?, 'PENDING')";
            try (PreparedStatement ps = conn.prepareStatement(insertBooking, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, customerId);
                ps.setDouble(2, total);
                ps.executeUpdate();
                ResultSet keys = ps.getGeneratedKeys();
                keys.next();
                bookingId = keys.getInt(1);
            }

            // 2. Lock & verify + reserve each seat, insert booking_items
            String checkSeat = "SELECT status FROM seats WHERE seat_id = ? FOR UPDATE";
            String reserveSeat = "UPDATE seats SET status = 'SOLD' WHERE seat_id = ?";
            String insertItem = "INSERT INTO booking_items (booking_id, event_id, seat_id, price, status) VALUES (?, ?, ?, ?, 'ACTIVE')";

            for (Seat seat : seats) {
                try (PreparedStatement ps = conn.prepareStatement(checkSeat)) {
                    ps.setInt(1, seat.getSeatId());
                    ResultSet rs = ps.executeQuery();
                    if (!rs.next() || !"AVAILABLE".equals(rs.getString("status"))) {
                        conn.rollback();
                        return null; // seat no longer available
                    }
                }
                try (PreparedStatement ps = conn.prepareStatement(reserveSeat)) {
                    ps.setInt(1, seat.getSeatId());
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement(insertItem)) {
                    ps.setInt(1, bookingId);
                    ps.setInt(2, eventId);
                    ps.setInt(3, seat.getSeatId());
                    ps.setDouble(4, seat.getPrice());
                    ps.executeUpdate();
                }
            }

            // 3. Decrement event available_seats
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE events SET available_seats = available_seats - ? WHERE event_id = ?")) {
                ps.setInt(1, seats.size());
                ps.setInt(2, eventId);
                ps.executeUpdate();
            }

            // 4. Process payment (simulated gateway call)
            Payment payment = new Payment();
            payment.setBookingId(bookingId);
            payment.setAmount(total);
            payment.setMethod(paymentMethod);
            boolean success = payment.processPayment();

            String insertPayment = "INSERT INTO payments (booking_id, amount, method, status, paid_at, transaction_id) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertPayment)) {
                ps.setInt(1, bookingId);
                ps.setDouble(2, total);
                ps.setString(3, paymentMethod);
                ps.setString(4, payment.getStatus());
                ps.setTimestamp(5, payment.getPaidAt() != null ? Timestamp.valueOf(payment.getPaidAt()) : null);
                ps.setString(6, payment.getTransactionId());
                ps.executeUpdate();
            }

            if (!success) {
                conn.rollback();
                return null;
            }

            // 5. Update booking -> CONFIRMED
            try (PreparedStatement ps = conn.prepareStatement("UPDATE bookings SET status = 'CONFIRMED' WHERE booking_id = ?")) {
                ps.setInt(1, bookingId);
                ps.executeUpdate();
            }

            conn.commit();

            Booking booking = new Booking(bookingId, customerId, LocalDateTime.now(), total, "CONFIRMED");
            return booking;

        } catch (SQLException e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return null;
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    /** Returns all bookings (with their event & seat info) for a given customer, for "My Tickets". */
    public List<Booking> getBookingsForCustomer(int customerId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.*, e.title AS event_title, e.event_datetime FROM bookings b " +
                     "JOIN booking_items bi ON b.booking_id = bi.booking_id " +
                     "JOIN events e ON bi.event_id = e.event_id " +
                     "WHERE b.customer_id = ? GROUP BY b.booking_id ORDER BY b.booking_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Booking b = new Booking(rs.getInt("booking_id"), rs.getInt("customer_id"),
                        rs.getTimestamp("booking_date").toLocalDateTime(),
                        rs.getDouble("total_amount"), rs.getString("status"));
                b.setEventTitle(rs.getString("event_title"));
                Timestamp edt = rs.getTimestamp("event_datetime");
                b.setEventDateTime(edt != null ? edt.toLocalDateTime() : null);
                b.setItems(getBookingItems(b.getBookingId()));
                bookings.add(b);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    public List<BookingItem> getBookingItems(int bookingId) {
        List<BookingItem> items = new ArrayList<>();
        String sql = "SELECT bi.*, CONCAT(s.section, ' - ', s.seat_number) AS seat_label " +
                     "FROM booking_items bi JOIN seats s ON bi.seat_id = s.seat_id WHERE bi.booking_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                BookingItem item = new BookingItem(rs.getInt("item_id"), rs.getInt("booking_id"),
                        rs.getInt("event_id"), rs.getInt("seat_id"), rs.getDouble("price"), rs.getString("status"));
                item.setSeatLabel(rs.getString("seat_label"));
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    /** Cancels a booking: frees the seats and marks the booking CANCELLED (mirrors <<extend>> Cancel Ticket). */
    public boolean cancelBooking(int bookingId) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            int eventId = 0, seatCount = 0;
            String getItems = "SELECT event_id, seat_id FROM booking_items WHERE booking_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(getItems)) {
                ps.setInt(1, bookingId);
                ResultSet rs = ps.executeQuery();
                String freeSeat = "UPDATE seats SET status = 'AVAILABLE' WHERE seat_id = ?";
                while (rs.next()) {
                    eventId = rs.getInt("event_id");
                    int seatId = rs.getInt("seat_id");
                    try (PreparedStatement psFree = conn.prepareStatement(freeSeat)) {
                        psFree.setInt(1, seatId);
                        psFree.executeUpdate();
                    }
                    seatCount++;
                }
            }

            if (eventId != 0) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE events SET available_seats = available_seats + ? WHERE event_id = ?")) {
                    ps.setInt(1, seatCount);
                    ps.setInt(2, eventId);
                    ps.executeUpdate();
                }
            }

            try (PreparedStatement ps = conn.prepareStatement("UPDATE bookings SET status = 'CANCELLED' WHERE booking_id = ?")) {
                ps.setInt(1, bookingId);
                ps.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return false;
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    /** Admin: all bookings across all customers, for reports. */
    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.*, u.name AS customer_name, e.title AS event_title, e.event_datetime " +
                     "FROM bookings b " +
                     "JOIN users u ON b.customer_id = u.user_id " +
                     "JOIN booking_items bi ON b.booking_id = bi.booking_id " +
                     "JOIN events e ON bi.event_id = e.event_id " +
                     "GROUP BY b.booking_id ORDER BY b.booking_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Booking b = new Booking(rs.getInt("booking_id"), rs.getInt("customer_id"),
                        rs.getTimestamp("booking_date").toLocalDateTime(),
                        rs.getDouble("total_amount"), rs.getString("status"));
                b.setEventTitle(rs.getString("customer_name") + " — " + rs.getString("event_title"));
                Timestamp edt = rs.getTimestamp("event_datetime");
                b.setEventDateTime(edt != null ? edt.toLocalDateTime() : null);
                bookings.add(b);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }
}
