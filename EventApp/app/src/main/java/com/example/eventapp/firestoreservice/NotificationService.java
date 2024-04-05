package com.example.eventapp.firestoreservice;

import com.google.firebase.firestore.CollectionReference;

public class NotificationService {
    private final CollectionReference notificationRef ;
    private String notificationId ;

    public NotificationService (CollectionReference notificationRef) {
        this.notificationRef = notificationRef;
    }

    public void getNotifications(){

    }

    public void createNotification(){
        
    }
}
