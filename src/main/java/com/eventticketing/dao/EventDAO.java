package com.eventticketing.dao;

import com.eventticketing.db.DBConnection;
import com.eventticketing.model.Event;
import com.eventticketing.model.Seat;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventDAO {

    private static final String BASE_SELECT =
        "SELECT e.*, v.name AS venue_name, c.name AS category_name " +
        "FROM events e " +
        "JOIN venues v ON e.venue_id = v.venue_id " +
        "JOIN categories c ON e.category_id = c.category_id ";

    public List<Event> getAllEvents() {
        List<Event> list = new ArrayList<>();
        String sql = BASE_SELECT + "ORDER BY e.event_datetime";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Search events by keyword (title) and/or category. Pass null/empty to skip a filter. */
    public List<Event> searchEvents(String keyword, Integer categoryId) {
        List<Event> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(BASE_SELECT + "WHERE 1=1 ");
        if (keyword != null && !keyword.isBlank()) sql.append("AND e.title LIKE ? ");
        if (categoryId != null) sql.append("AND e.category_id = ? ");
        sql.append("ORDER BY e.event_datetime");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (keyword != null && !keyword.isBlank()) ps.setString(idx++, "%" + keyword + "%");
            if (categoryId != null) ps.setInt(idx++, categoryId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Event getEventById(int eventId) {
        String sql = BASE_SELECT + "WHERE e.event_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Seat> getAvailableSeats(int eventId) {
        List<Seat> seats = new ArrayList<>();
        String sql = "SELECT * FROM seats WHERE event_id = ? AND status = 'AVAILABLE' ORDER BY seat_number";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                seats.add(new Seat(rs.getInt("seat_id"), rs.getInt("event_id"), rs.getString("section"),
                        rs.getString("seat_number"), rs.getDouble("price"), rs.getString("status")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seats;
    }

    /** Creates a new event and auto-generates its "General" seats. */
    public int addEvent(String title, String description, LocalDateTime dateTime,
                         int venueId, int categoryId, int totalSeats, double pricePerSeat) {
        String insertEvent = "INSERT INTO events (title, description, event_datetime, venue_id, category_id, total_seats, available_seats) " +
                              "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            int eventId;
            try (PreparedStatement ps = conn.prepareStatement(insertEvent, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, title);
                ps.setString(2, description);
                ps.setTimestamp(3, Timestamp.valueOf(dateTime));
                ps.setInt(4, venueId);
                ps.setInt(5, categoryId);
                ps.setInt(6, totalSeats);
                ps.setInt(7, totalSeats);
                ps.executeUpdate();
                ResultSet keys = ps.getGeneratedKeys();
                keys.next();
                eventId = keys.getInt(1);
            }
            String insertSeat = "INSERT INTO seats (event_id, section, seat_number, price, status) VALUES (?, 'General', ?, ?, 'AVAILABLE')";
            try (PreparedStatement ps = conn.prepareStatement(insertSeat)) {
                for (int i = 1; i <= totalSeats; i++) {
                    ps.setInt(1, eventId);
                    ps.setString(2, "S-" + String.format("%03d", i));
                    ps.setDouble(3, pricePerSeat);
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            conn.commit();
            conn.setAutoCommit(true);
            return eventId;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public boolean updateEvent(int eventId, String title, String description, LocalDateTime dateTime) {
        String sql = "UPDATE events SET title = ?, description = ?, event_datetime = ? WHERE event_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, description);
            ps.setTimestamp(3, Timestamp.valueOf(dateTime));
            ps.setInt(4, eventId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteEvent(int eventId) {
        String sql = "DELETE FROM events WHERE event_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Event mapRow(ResultSet rs) throws SQLException {
        Event e = new Event();
        e.setEventId(rs.getInt("event_id"));
        e.setTitle(rs.getString("title"));
        e.setDescription(rs.getString("description"));
        Timestamp dt = rs.getTimestamp("event_datetime");
        e.setDateTime(dt != null ? dt.toLocalDateTime() : null);
        e.setVenueId(rs.getInt("venue_id"));
        e.setCategoryId(rs.getInt("category_id"));
        e.setTotalSeats(rs.getInt("total_seats"));
        e.setAvailableSeats(rs.getInt("available_seats"));
        e.setImageUrl(rs.getString("image_url"));
        Timestamp ca = rs.getTimestamp("created_at");
        e.setCreatedAt(ca != null ? ca.toLocalDateTime() : null);
        e.setVenueName(rs.getString("venue_name"));
        e.setCategoryName(rs.getString("category_name"));
        return e;
    }
}
