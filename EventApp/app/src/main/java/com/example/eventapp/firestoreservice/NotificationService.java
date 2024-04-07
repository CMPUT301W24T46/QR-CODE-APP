package com.example.eventapp.firestoreservice;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

/**
 * NotificationService is responsible for managing notifications within the application. It interacts with
 * Firestore to retrieve existing notifications and to create new notifications. The service is initialized with
 * a reference to the notifications collection in Firestore, allowing it to perform database operations related to notifications.
 */

public class NotificationService {
    private final CollectionReference notificationRef ;

    private String notificationId ;


    /**
     * Constructs a new NotificationService with the specified Firestore collection reference.
     *
     * @param notificationRef The Firestore collection reference for notifications.
     */

    public NotificationService (CollectionReference notificationRef) {

    private String notificationTitle ;

    private String message ;

    public NotificationService(CollectionReference notificationRef, String notificationId, String notificationTitle, String message) {

        this.notificationRef = notificationRef;
        this.notificationId = notificationId;
        this.notificationTitle = notificationTitle;
        this.message = message;
    }


    /**
     * Retrieves notifications from Firestore. This method is intended to fetch all notifications
     * or a subset based on certain criteria defined within the method's implementation.
     */

    public void getNotifications(){

    }

    /**
     * Creates a new notification in Firestore. This method is intended to add a new notification document
     * to the Firestore notifications collection with relevant notification details.
     */

    public void createNotification(){
        

    public void getUserInfo(NotificationCallback callback){
        notificationRef.document(notificationId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    // Assuming the user's name is stored under a field called "name"
                    String id = document.getString("notificationId");
                    callback.onCallback(id);
                } else {
                    // Document does not exist
                    callback.onError(new Exception("Document does not exist"));
                }
            } else {
                // Task failed with an exception
                callback.onError(task.getException());
            }
        });
    }

    public void updateUserInfo(NotificationCallback callback){
        Map<String , Object > updateFields = new HashMap<>();
        updateFields.put("notificationId" , notificationId) ;
        updateFields.put("notificationTitle" , notificationTitle) ;
        updateFields.put("message" , message) ;
        notificationRef.document(notificationId).update(updateFields).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onSuccessfulUpdate(true);
            } else {
                // Task failed with an exception
                callback.onError(task.getException());
            }
        });

    }
}
