package com.eventticketing.model;

public class Seat {
    private int seatId;
    private int eventId;
    private String section;
    private String seatNumber;
    private double price;
    private String status; // AVAILABLE, RESERVED, SOLD

    public Seat() {}

    public Seat(int seatId, int eventId, String section, String seatNumber, double price, String status) {
        this.seatId = seatId;
        this.eventId = eventId;
        this.section = section;
        this.seatNumber = seatNumber;
        this.price = price;
        this.status = status;
    }

    public boolean isAvailable() { return "AVAILABLE".equalsIgnoreCase(status); }

    public int getSeatId() { return seatId; }
    public void setSeatId(int seatId) { this.seatId = seatId; }
    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }
    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }
    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Seat)) return false;
        return seatId == ((Seat) o).seatId;
    }

    @Override
    public int hashCode() { return Integer.hashCode(seatId); }

    @Override
    public String toString() { return section + " - " + seatNumber + " (₱" + price + ")"; }
}
