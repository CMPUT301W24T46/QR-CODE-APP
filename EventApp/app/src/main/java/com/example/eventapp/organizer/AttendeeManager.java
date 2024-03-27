package com.example.eventapp.organizer;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AttendeeManager {

    // Interface for callback when attendees are retrieved
    public interface AttendeeRetrievalListener {
        void onAttendeesRetrieved(List<String> attendees);
        void onError(String errorMessage);
    }

    // Method to retrieve all attendees for a given event ID
    public void getAllAttendeesForEvent(String eventId, AttendeeRetrievalListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<String> attendees = new ArrayList<>();

        db.collection("Events").document(eventId).collection("checkIns").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String attendeeId = documentSnapshot.getId();
                        attendees.add(attendeeId);
                    }
                    listener.onAttendeesRetrieved(attendees);
                })
                .addOnFailureListener(e -> {
                    Log.e("AttendeeManager", "Error fetching attendees: " + e.getMessage());
                    listener.onError(e.getMessage());
                });
    }
}
