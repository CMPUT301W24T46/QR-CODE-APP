package com.example.eventapp.firestoreservice;

/**
 * NotificationCallback provides an interface for handling responses and outcomes from operations related to notifications.
 * This could include actions such as the successful delivery of a notification, failure to send a notification, or receiving data
 * from a notification. Implementations of this interface can define specific methods to manage these events.
 */

public interface NotificationCallback {
    void onCallback(String userName);
    void onError(Exception e);

    void onSuccessfulUpdate(boolean success) ;
}
