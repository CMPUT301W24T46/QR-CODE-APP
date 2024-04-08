package com.example.eventapp.firestoreservice;

import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * EventService handles operations related to events in Firestore, including retrieving and updating event information.
 * It provides methods to get detailed information about an event and to update event details in Firestore.
 * This class uses a callback mechanism to communicate the results of Firestore operations back to the calling context.
 */

public class EventService {

    private final CollectionReference eventRef ;
    private String date ;
    private String eventName ;

    private  String userId ;

    private  String eventId ;

    private String imageUrl ;
    private String eventDescription ;

    /**
     * Constructs a new EventService with specified event details.
     *
     * @param eventRef The Firestore collection reference for events.
     * @param date The date of the event.
     * @param eventName The name of the event.
     * @param eventDescription A description of the event.
     * @param eventId The unique Firestore ID of the event.
     * @param imageUrl The URL of the event's image.
     */

    public EventService(CollectionReference eventRef , String date , String eventName , String eventDescription , String eventId, String imageUrl) {
        this.date = date;
        this.eventName = eventName ;
        this.eventDescription = eventDescription ;
        this.userId = userId ;
        this.eventRef = eventRef;
        this.imageUrl = imageUrl ;
        this.eventId = eventId ;
    }

    /**
     * Retrieves information about an event from Firestore and communicates the result through a callback interface.
     *
     * @param eventCallback The callback interface to handle the result of the Firestore operation.
     */

    public void getEventInformation(EventCallback eventCallback){
        eventRef.document(eventId).get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            // Assuming the user's name is stored under a field called "name"
                            String eventName = document.getString("eventName");
                            eventCallback.eventRetrieved(eventName);
                        } else {
                            // Document does not exist
                            eventCallback.onError(new Exception("Document does not exist"));
                        }
                    }else{
                        eventCallback.onError(task.getException());
                    }
                });
    }

    /**
     * Updates event information in Firestore with the current details held by this EventService instance.
     *
     * @param eventCallback The callback interface to handle the result of the Firestore operation.
     */

    public void updateEventInformation(EventCallback eventCallback){
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("eventName", eventName);
        eventMap.put("eventDate", date);
        eventMap.put("imageURL", imageUrl);
        eventMap.put("eventId", eventId);
        eventMap.put("eventDescription", eventDescription);

        eventRef.document(eventId).update(eventMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                eventCallback.eventCreated();
            } else {
                // Task failed with an exception
                eventCallback.onError(task.getException());}});
    }
}
