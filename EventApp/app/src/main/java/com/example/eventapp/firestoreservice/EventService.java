package com.example.eventapp.firestoreservice;

import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EventService {

    private final CollectionReference eventRef ;
    private String date ;
    private String eventName ;

    private  String userId ;

    private  String eventId ;

    private String imageUrl ;
    private String eventDescription ;
    public EventService(CollectionReference eventRef , String date , String eventName , String eventDescription , String eventId, String imageUrl) {
        this.date = date;
        this.eventName = eventName ;
        this.eventDescription = eventDescription ;
        this.userId = userId ;
        this.eventRef = eventRef;
        this.imageUrl = imageUrl ;
        this.eventId = eventId ;
    }

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
