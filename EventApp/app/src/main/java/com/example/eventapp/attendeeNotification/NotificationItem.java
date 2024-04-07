package com.example.eventapp.attendeeNotification;

/**
 * NotificationItem represents a single notification item with a title, timestamp, and content. It encapsulates the
 * data structure for a notification, providing a convenient way to store and retrieve notification details.
 *
 * <p>This class is typically used in conjunction with an adapter, such as {@link AttendeeNotifAdapter}, to display
 * a list of notifications in the user interface. Each NotificationItem object corresponds to one notification in the list.</p>
 *
 * <p>Attributes:</p>
 * <ul>
 *     <li>Title: A brief headline or summary of the notification.</li>
 *     <li>Timestamp: A string representation of the date and/or time the notification was issued.</li>
 *     <li>Content: The detailed message or information conveyed by the notification.</li>
 * </ul>
 */

public class NotificationItem {
    private String title;
    private String timestamp;
    private String content;

    /**
     * Constructs a new NotificationItem with the specified title, timestamp, and content.
     *
     * @param title     The title or summary of the notification.
     * @param timestamp The date and/or time the notification was issued, represented as a String.
     * @param content   The detailed content or message of the notification.
     */

    public NotificationItem(String title, String timestamp, String content) {
        this.title = title;
        this.timestamp = timestamp;
        this.content = content;
    }

    /**
     * Returns the title of the notification.
     *
     * @return The title or summary of the notification.
     */

    public String getTitle() {
        return title;
    }

    /**
     * Returns the timestamp of the notification.
     *
     * @return The date and/or time the notification was issued, represented as a String.
     */

    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Returns the content of the notification.
     *
     * @return The detailed content or message of the notification.
     */

    public String getContent() {
        return content;
    }
}
