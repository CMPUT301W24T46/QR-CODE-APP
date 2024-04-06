package com.example.eventapp.firestoreservice;

import com.example.eventapp.event.Event;

public interface EventCallback {
    void eventCreated() ;

    void eventRetrieved(String eventName) ;

    void onError(Exception e);
}
