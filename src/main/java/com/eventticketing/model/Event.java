package com.eventticketing.model;

import java.time.LocalDateTime;

public class Event {
    private int eventId;
    private String title;
    private String description;
    private LocalDateTime dateTime;
    private int venueId;
    private int categoryId;
    private int totalSeats;
    private int availableSeats;
    private String imageUrl;
    private LocalDateTime createdAt;

    // convenience joined fields (from SQL joins), not in the raw table
    private String venueName;
    private String categoryName;

    public Event() {}

    public Event(int eventId, String title, String description, LocalDateTime dateTime,
                 int venueId, int categoryId, int totalSeats, int availableSeats,
                 String imageUrl, LocalDateTime createdAt) {
        this.eventId = eventId;
        this.title = title;
        this.description = description;
        this.dateTime = dateTime;
        this.venueId = venueId;
        this.categoryId = categoryId;
        this.totalSeats = totalSeats;
        this.availableSeats = availableSeats;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
    }

    public int getAvailableSeatsCount() { return availableSeats; }
    public void updateDetails(String title, String description, LocalDateTime dateTime) {
        this.title = title;
        this.description = description;
        this.dateTime = dateTime;
    }

    // getters / setters
    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }
    public int getVenueId() { return venueId; }
    public void setVenueId(int venueId) { this.venueId = venueId; }
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }
    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getVenueName() { return venueName; }
    public void setVenueName(String venueName) { this.venueName = venueName; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    @Override
    public String toString() { return title; }
}
