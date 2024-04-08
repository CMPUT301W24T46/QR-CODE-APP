package com.example.eventapp.event;

import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * EventDB stores all the event information inside firebase event collections.
 */
public class EventDB {
    private FirebaseFirestore db;
    private CollectionReference eventsCollection;

    /**
     * Constructs a new EventDB instance with the given FirebaseFirestore instance.
     * Initializes the events collection reference using the provided database.
     *
     * @param db The instance of Firebase to use for database operations.
     */
    public EventDB(FirebaseFirestore db) {
        this.db = db;
        this.eventsCollection = db.collection("Events");
    }
    /**
     * Add new event to the firebase.
     *
     * @param eventName        The name of the event to be added.
     * @param imageDescription The description of the event image.
     * @param imagePhoto       The URL or path of the event image.
     * @param creatorId
     */
    public void addEvent(String eventName, String imageDescription, String imagePhoto, String creatorId) {
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

    /**
     * Adds a new organizer event to the Firestore database. Records event details including name, date, image URL,
     * and creator ID into the "Events" collection.
     *
     * @param eventName Name of the event.
     * @param eventDate Date of the event.
     * @param imageURL URL for the event's image.
     * @param creatorId ID of the user who created the event.
     */
    public void addorganizerEvent(String eventName, String eventDate, String imageURL, String creatorId) {
        Map<String, Object> event = new HashMap<>();
        event.put("eventName", eventName);
        event.put("eventDate", eventDate);
        event.put("imageURL", imageURL);
        event.put("creatorId", creatorId);

        eventsCollection.add(event)
                .addOnSuccessListener(documentReference -> Log.d("EventDB", "Event successfully added!"))
                .addOnFailureListener(e -> Log.w("EventDB", "Error adding event", e));
    }


    /**
     * Delete event from the firebase.
     *
     * @param eventID The ID of the event to be deleted.
     */
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

    /**
     * Retrieves information of a specific event from firebase.
     *
     * @param eventID The ID of the event to retrieve information.
     */
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

    public void getAllEventsForUser(String userId, EventRetrievalListener listener) {
        db.collection("Events")
                .whereEqualTo("creatorId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Event> events = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String eventId = document.getId();
                        Event event = document.toObject(Event.class);
                        event.setEventId(eventId);
                        events.add(event);
                    }
                    listener.onEventsRetrieved(events);
                })
                .addOnFailureListener(e -> {
                    listener.onError("Failed to retrieve events: " + e.getMessage());
                });
    }


    public interface EventRetrievalListener {
        void onEventsRetrieved(List<Event> events);
        void onError(String errorMessage);
    }


}