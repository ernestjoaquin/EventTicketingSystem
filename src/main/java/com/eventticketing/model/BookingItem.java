package com.eventticketing.model;

public class BookingItem {
    private int itemId;
    private int bookingId;
    private int eventId;
    private int seatId;
    private double price;
    private String status;

    // convenience joined fields
    private String seatLabel;

    public BookingItem() {}

    public BookingItem(int itemId, int bookingId, int eventId, int seatId, double price, String status) {
        this.itemId = itemId;
        this.bookingId = bookingId;
        this.eventId = eventId;
        this.seatId = seatId;
        this.price = price;
        this.status = status;
    }

    public double getSubtotal() { return price; }

    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }
    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }
    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }
    public int getSeatId() { return seatId; }
    public void setSeatId(int seatId) { this.seatId = seatId; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getSeatLabel() { return seatLabel; }
    public void setSeatLabel(String seatLabel) { this.seatLabel = seatLabel; }
}
