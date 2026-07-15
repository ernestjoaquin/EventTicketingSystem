package com.eventticketing.model;

import java.time.LocalDateTime;
import java.util.List;

public class Customer extends User {
    private int customerId;

    public Customer() { super(); }

    public Customer(int userId, String name, String email, String phone, String password, LocalDateTime createdAt) {
        super(userId, name, email, phone, password, createdAt);
        this.customerId = userId;
    }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    @Override
    public String getRole() { return "CUSTOMER"; }

    // The following operations are implemented via DAOs in this app:
    // browseEvents(), searchEvents(keyword), viewEventDetails(eventId),
    // addToCart(seatId), viewCart(), checkout(), viewMyTickets(), cancelTicket(bookingId)
}
