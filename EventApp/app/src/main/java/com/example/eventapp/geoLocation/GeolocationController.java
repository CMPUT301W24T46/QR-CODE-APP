package com.example.eventapp.geoLocation;

import android.util.Log;

import com.example.eventapp.checkIn.CheckIn;
import com.example.eventapp.checkIn.CheckInAdapter;
import com.example.eventapp.event.Event;
import com.example.eventapp.event.EventAdapter;
import com.example.eventapp.users.User;
import com.example.eventapp.users.UserAdapter;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GeolocationController {

    private final CollectionReference eventRef;
    private final Map<String, GeoPoint> attendeeLocationRefMap;


    public GeolocationController() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        this.eventRef = db.collection("Events");
        this.attendeeLocationRefMap = new HashMap<>();
    }


    /**
     * Subscribes to real-time updates of the user database in Firestore.
     * Updates the provided UserAdapter with the latest data.
     *
     * @param adapter The adapter that needs to be updated with the fetched data.
     */
    public void subscribeToEventCheckIns(String eventId, CheckInAdapter adapter) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // CheckIns subcollection for a specific event
        CollectionReference checkInRef = eventRef.document(eventId).collection("CheckIns");

        checkInRef.orderBy("checkInTime").addSnapshotListener((querySnapshots, error) -> {
            if (error != null) {
                Log.e("Firestore", error.toString());
                return;
            }

            if (querySnapshots != null) {
                ArrayList<CheckIn> checkIns = new ArrayList<>();
                for (QueryDocumentSnapshot doc : querySnapshots) {
                    String attendeeId = doc.getString("attendeeId");
                    GeoPoint checkInLocation = doc.getGeoPoint("checkInLocation");
                    Timestamp checkInTime = doc.getTimestamp("checkInTime");

                    checkIns.add(new CheckIn(attendeeId, checkInLocation, checkInTime));
                }
                adapter.setFilter(checkIns);
                adapter.notifyDataSetChanged(); // Make sure to notify the adapter of the dataset change
            }
        });
    }

    public void getCurrentCheckIns(String eventId, CheckInAdapter adapter) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference checkInRef = eventRef.document(eventId).collection("CheckIns");

        checkInRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            ArrayList<CheckIn> checkIns = new ArrayList<>();
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                String attendeeId = documentSnapshot.getString("attendeeId");
                GeoPoint checkInLocation = documentSnapshot.getGeoPoint("checkInLocation");
                Timestamp checkInTime = documentSnapshot.getTimestamp("checkInTime");

                CheckIn checkIn = new CheckIn(attendeeId, checkInLocation, checkInTime);
                checkIns.add(checkIn);
            }
            adapter.setFilter(checkIns);
            adapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> Log.e("TAG", "Error getting check-ins: " + e));
    }

    // GEOLOCATION
    public interface OnLocationsFetched {
        void onFetched(Map<String, GeoPoint> locations);
    }

    public void getCheckInLocations(String eventId, OnLocationsFetched callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference checkInRef = eventRef.document(eventId).collection("CheckIns");

        checkInRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            Map<String, GeoPoint> locationsMap = new HashMap<>();
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                String attendeeId = documentSnapshot.getString("attendeeId");
                GeoPoint location = documentSnapshot.getGeoPoint("checkInLocation");
                if (attendeeId != null && location != null) {
                    locationsMap.put(attendeeId, location);
                }
            }
            callback.onFetched(locationsMap);
            Log.e("EventMap", locationsMap.toString());

        }).addOnFailureListener(e -> Log.e("GeolocationController", "Error getting check-in locations: " + e));
    }


}
