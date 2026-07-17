package com.eventticketing.dao;

import com.eventticketing.db.DBConnection;
import com.eventticketing.model.Venue;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VenueDAO {

    public List<Venue> getAllVenues() {
        List<Venue> list = new ArrayList<>();
        String sql = "SELECT * FROM venues ORDER BY name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Venue(rs.getInt("venue_id"), rs.getString("name"),
                        rs.getString("address"), rs.getInt("capacity")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean addVenue(String name, String address, int capacity) {
        return addVenueReturnId(name, address, capacity) != -1;
    }

    /**
     * Inserts a new venue and returns its generated venue_id, or -1 on failure.
     * Used when the admin types a brand-new venue name instead of picking
     * one from the existing list.
     */
    public int addVenueReturnId(String name, String address, int capacity) {
        String sql = "INSERT INTO venues (name, address, capacity) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, address);
            ps.setInt(3, capacity);
            int rows = ps.executeUpdate();
            if (rows == 0) return -1;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
            return -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Finds an existing venue by name (case-insensitive), ignoring
     * surrounding whitespace. Returns null if no match is found.
     */
    public Venue findByName(String name) {
        String sql = "SELECT * FROM venues WHERE LOWER(name) = LOWER(?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Venue(rs.getInt("venue_id"), rs.getString("name"),
                            rs.getString("address"), rs.getInt("capacity"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
