package com.example.eventapp.notification;

public class Notification {
    private String title;
    private String message;
    private String timestamp;
    private String creatorId;

    // Constructors
    public Notification() {
        // Firestore
    }

    public Notification(String title, String message, String timestamp, String creatorId) {
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.creatorId = creatorId;
    }

    // Getters and setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }
}
