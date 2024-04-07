package com.example.eventapp.notification;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A class for managing notification-related operations with Firestore.
 */

public class NotificationDB {
    private FirebaseFirestore db;
    private CollectionReference notificationsCollection;

    /**
     * Constructs a new NotificationDB instance with the given FirebaseFirestore instance.
     * Initializes the notifications collection reference using the provided database.
     *
     * @param db The instance of FirebaseFirestore to use for database operations.
     */

    public NotificationDB(FirebaseFirestore db) {
        this.db = db;
        this.notificationsCollection = db.collection("Notifications");
    }

    /**
     * Saves a notification object to Firestore in the "Notifications" collection.
     * Logs the result of the operation.
     *
     * @param notification The notification object to be saved to Firestore.
     */

    public void saveNotificationToFirestore(Notification notification) {
        // Get a reference to the notifications collection in Firestore
        CollectionReference notificationsCollection = db.collection("Notifications");

        // Add the notification data to Firestore
        notificationsCollection.add(notification)
                .addOnSuccessListener(documentReference -> {
                    // Notification added successfully
                    Log.d("NotificationDB", "Notification added with ID: " + documentReference.getId());
                })
                .addOnFailureListener(error -> {
                    // Failed to add notification
                    Log.w("NotificationDB", "Error adding notification", error);
                });
    }

    /**
     * Retrieves all notifications created by a specific user from Firestore and passes them to a callback.
     * The notifications are retrieved from the "Notifications" collection, filtered by the creatorId field.
     *
     * @param userId   The ID of the user whose notifications are to be retrieved.
     * @param listener A listener interface for handling the result of the notification retrieval operation.
     */

    public void getUserNotifications(String userId, NotificationRetrievalListener listener) {
        db.collection("Notifications")
                .whereEqualTo("creatorId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Notification> notifications = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Notification notification = document.toObject(Notification.class);
                            notifications.add(notification);
                        }
                        listener.onNotificationsRetrieved(notifications);
                    } else {
                        listener.onError("Error fetching notifications");
                    }
                });
    }


    /**
     * An interface for callback methods used in notification retrieval operations.
     */

    public interface NotificationRetrievalListener {
        /**
         * Called when notifications are successfully retrieved from Firestore.
         *
         * @param notifications A list of Notification objects retrieved from Firestore.
         */
        void onNotificationsRetrieved(List<Notification> notifications);

        /**
         * Called when an error occurs during the notification retrieval process.
         *
         * @param errorMessage A message describing the error that occurred.
         */

        void onError(String errorMessage);
    }
}
