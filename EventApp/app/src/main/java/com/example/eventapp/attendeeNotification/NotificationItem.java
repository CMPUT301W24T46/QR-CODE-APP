package com.example.eventapp.attendeeNotification;

public class NotificationItem {
    private String title;
    private String timestamp;
    private String content;

    public NotificationItem(String title, String timestamp, String content) {
        this.title = title;
        this.timestamp = timestamp;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getContent() {
        return content;
    }
}
