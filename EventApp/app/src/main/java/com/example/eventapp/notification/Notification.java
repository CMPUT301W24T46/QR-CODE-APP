package com.example.eventapp.notification;

/**
 * Represents a notification with a title, message, and timestamp.
 * This class can be used for creating notification objects to be stored or processed within an application.
 */

public class Notification {
    private String title;
    private String message;
    private String timestamp;


    /**
     * Default constructor required for Firestore data mapping.
     */

    // Constructors
    public Notification() {
        // Firestore
    }

    /**
     * Constructs a new {@link Notification} instance with specified title, message, and timestamp.
     *
     * @param title     The title of the notification.
     * @param message   The message body of the notification.
     * @param timestamp The timestamp when the notification was created or received.
     */

    public Notification(String title, String message, String timestamp) {
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
    }

    /**
     * Gets the title of the notification.
     *
     * @return The title of the notification.
     */

    // Getters and setters
    public String getTitle() { return title; }

    /**
     * Sets the title of the notification.
     *
     * @param title The title to set for the notification.
     */

    public void setTitle(String title) { this.title = title; }

    /**
     * Gets the message body of the notification.
     *
     * @return The message body of the notification.
     */

    public String getMessage() { return message; }

    /**
     * Sets the message body of the notification.
     *
     * @param message The message body to set for the notification.
     */

    public void setMessage(String message) { this.message = message; }

    /**
     * Gets the timestamp of the notification.
     *
     * @return The timestamp of the notification.
     */

    public String getTimestamp() { return timestamp; }

    /**
     * Sets the timestamp of the notification.
     *
     * @param timestamp The timestamp to set for the notification.
     */

    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
