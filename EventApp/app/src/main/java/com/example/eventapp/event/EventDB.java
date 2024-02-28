package com.example.eventapp.event;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EventDB {
    private FirebaseFirestore db;
    private CollectionReference eventsCollection;

    public EventDB(FirebaseFirestore db) {
        this.db = db;
        this.eventsCollection = db.collection("Events");
    }
    public void addEvent(String eventName, String imageDescription, String imagePhoto) {
        Map<String, Object> event = new HashMap<>();
        event.put("eventName", eventName);
        event.put("imageDescription", imageDescription);
        event.put("imagePhoto", imagePhoto);

        eventsCollection
                .add(event)
                .addOnSuccessListener(documentReference -> {
                    Log.d("EventDB", "Event added with ID: " + documentReference.getId());
                })
                .addOnFailureListener(error -> {
                    Log.w("EventDB", "Cannot Add event", error);
                });
    }

    public void deleteEvent(String eventID) {
        db.collection("Events")
                .document(eventID)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            System.out.println("Event '" + eventID + "' deleted.");
                        } else {
                            System.out.println("Fail delete event '" + eventID + "': " + task.getException());
                        }
                    }
                });
    }
    public void getEventInfo(String eventID) {
        db.collection("Events")
                .document(eventID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("EventDB", "Event info: " + eventID);
                            String eventName = document.getString("eventName");
                            String imageDescription = document.getString("imageDescription");
                            //photo
                            Log.d("EventDB", "Event Name: " + eventName);
                            Log.d("EventDB", "Image Description: " + imageDescription);
                        } else {
                            Log.d("EventDB", "No such event: " + eventID);
                        }
                    } else {
                        Log.d("EventDB", "Error getting event: " + eventID, task.getException());
                    }
                });
    }

}
