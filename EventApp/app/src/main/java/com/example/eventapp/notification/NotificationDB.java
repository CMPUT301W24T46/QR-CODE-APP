package com.example.eventapp.notification;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotificationDB {
    private FirebaseFirestore db;
    private CollectionReference notificationsCollection;

    public NotificationDB(FirebaseFirestore db) {
        this.db = db;
        this.notificationsCollection = db.collection("Notifications");
    }

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


    public interface NotificationRetrievalListener {
        void onNotificationsRetrieved(List<Notification> notifications);
        void onError(String errorMessage);
    }
}
