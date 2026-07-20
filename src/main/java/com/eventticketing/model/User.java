package com.eventticketing.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/** Abstract base class, mirrors <<abstract>> User in the class diagram.
 *  Implements Serializable so a logged-in user can be written to / read
 *  back from the session.dat file used by SessionManager. */
public abstract class User implements Serializable {
    private static final long serialVersionUID = 1L;

    protected int userId;
    protected String name;
    protected String email;
    protected String phone;
    protected String password; // hashed
    protected LocalDateTime createdAt;

    public User() {}

    public User(int userId, String name, String email, String phone, String password, LocalDateTime createdAt) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.createdAt = createdAt;
    }

    public boolean login(String email, String password) {
        return this.email.equalsIgnoreCase(email) && this.password.equals(password);
    }

    public void updateProfile(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public void logout() { /* handled by SessionManager */ }

    // getters / setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public abstract String getRole();
}
