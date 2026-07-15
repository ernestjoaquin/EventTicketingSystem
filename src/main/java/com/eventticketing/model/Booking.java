package com.eventticketing.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Booking {
    private int bookingId;
    private int customerId;
    private LocalDateTime bookingDate;
    private double totalAmount;
    private String status; // PENDING, CONFIRMED, CANCELLED
    private List<BookingItem> items = new ArrayList<>();

    // convenience joined fields
    private String eventTitle;
    private LocalDateTime eventDateTime;

    public Booking() {}

    public Booking(int bookingId, int customerId, LocalDateTime bookingDate, double totalAmount, String status) {
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.bookingDate = bookingDate;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    public LocalDateTime getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDateTime bookingDate) { this.bookingDate = bookingDate; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<BookingItem> getItems() { return items; }
    public void setItems(List<BookingItem> items) { this.items = items; }
    public String getEventTitle() { return eventTitle; }
    public void setEventTitle(String eventTitle) { this.eventTitle = eventTitle; }
    public LocalDateTime getEventDateTime() { return eventDateTime; }
    public void setEventDateTime(LocalDateTime eventDateTime) { this.eventDateTime = eventDateTime; }
}
