package com.eventticketing.dao;

import com.eventticketing.db.DBConnection;
import com.eventticketing.model.Admin;
import com.eventticketing.model.Customer;
import com.eventticketing.model.User;
import com.eventticketing.util.PasswordUtil;

import java.sql.*;

public class UserDAO {

    /** Registers a new customer. Returns the generated user id, or -1 on failure (e.g. duplicate email). */
    public int register(String name, String email, String phone, String plainPassword) {
        String sql = "INSERT INTO users (name, email, phone, password, role) VALUES (?, ?, ?, ?, 'CUSTOMER')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setString(4, PasswordUtil.hash(plainPassword));
            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLIntegrityConstraintViolationException dup) {
            System.err.println("Email already registered.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /** Authenticates a user by email/password. Returns a Customer or Admin instance, or null if invalid. */
    public User login(String email, String plainPassword) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String storedHash = rs.getString("password");
                if (PasswordUtil.verify(plainPassword, storedHash)) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateProfile(int userId, String name, String phone) {
        String sql = "UPDATE users SET name = ?, phone = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, phone);
            ps.setInt(3, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public java.util.List<User> getAllCustomers() {
        java.util.List<User> list = new java.util.ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'CUSTOMER' ORDER BY name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("user_id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        String phone = rs.getString("phone");
        String password = rs.getString("password");
        Timestamp ts = rs.getTimestamp("created_at");
        java.time.LocalDateTime createdAt = ts != null ? ts.toLocalDateTime() : null;
        String role = rs.getString("role");

        if ("ADMIN".equalsIgnoreCase(role)) {
            return new Admin(id, name, email, phone, password, createdAt);
        } else {
            return new Customer(id, name, email, phone, password, createdAt);
        }
    }
}
