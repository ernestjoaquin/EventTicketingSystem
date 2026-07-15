package com.eventticketing.model;

import java.time.LocalDateTime;

public class Admin extends User {
    private int adminId;

    public Admin() { super(); }

    public Admin(int userId, String name, String email, String phone, String password, LocalDateTime createdAt) {
        super(userId, name, email, phone, password, createdAt);
        this.adminId = userId;
    }

    public int getAdminId() { return adminId; }
    public void setAdminId(int adminId) { this.adminId = adminId; }

    @Override
    public String getRole() { return "ADMIN"; }

    // addEvent(), updateEvent(), deleteEvent(), manageCategories(),
    // manageUsers(), viewBookings(), viewReports() are implemented via DAOs
}
