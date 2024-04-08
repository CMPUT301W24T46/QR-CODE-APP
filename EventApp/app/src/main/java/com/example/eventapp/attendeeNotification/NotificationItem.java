package com.example.eventapp.attendeeNotification;

/**
 * Represents a notification item in the event application context. Each notification item includes
 * a title, a timestamp, and the content of the notification.
 */

public class NotificationItem {
    private String title;
    private String timestamp;
    private String content;

    /**
     * Constructs a new NotificationItem with the specified title, timestamp, and content.
     *
     * @param title     The title of the notification. This value cannot be null.
     * @param timestamp The timestamp of the notification, formatted as a String. This value cannot be null.
     * @param content   The detailed content of the notification. This value cannot be null.
     */

    public NotificationItem(String title, String timestamp, String content) {
        this.title = title;
        this.timestamp = timestamp;
        this.content = content;
    }

    /**
     * Returns the title of the notification.
     *
     * @return The title of the notification.
     */

    public String getTitle() {
        return title;
    }

    /**
     * Returns the timestamp of the notification.
     *
     * @return The timestamp of the notification, formatted as a String.
     */

    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Returns the content of the notification.
     *
     * @return The detailed content or body of the notification.
     */

    public String getContent() {
        return content;
    }
}
