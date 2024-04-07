package com.example.eventapp.firestoreservice;

import com.example.eventapp.event.Event;

/**
 * EventCallback is an interface defining methods to handle the outcomes of Firestore operations related to events.
 * Implementations of this interface can manage responses from creating a new event, retrieving event details,
 * and handling any errors that may occur during these operations.
 */

public interface EventCallback {

    /**
     * Called when a new event has been successfully created in Firestore. This method should contain
     * the logic that needs to be executed after the successful creation of an event.
     */

    void eventCreated() ;

    /**
     * Called when event details have been successfully retrieved from Firestore. Implementations of
     * this method should handle the use of the retrieved event name or other details as needed.
     *
     * @param eventName The name of the event that was retrieved.
     */

    void eventRetrieved(String eventName) ;

    /**
     * Called when an error occurs during a Firestore operation related to events. Implementations
     * of this method should handle the error, such as by logging it or displaying a user-friendly message.
     *
     * @param e The exception representing the error that occurred.
     */

    void onError(Exception e);
}
