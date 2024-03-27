package com.example.eventapp.checkIn;

import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class CheckInController {

    private final CollectionReference eventRef;
    private final CollectionReference userRef;
    private final FirebaseFirestore db;



    public CheckInController() {

        db = FirebaseFirestore.getInstance();

        this.eventRef = db.collection("Events");
        this.userRef = db.collection("Users");

    }

    public void subscribeToEventCheckIns(String eventId, AttendeeCheckInAdapter adapter) {
        CollectionReference checkInRef = eventRef.document(eventId).collection("CheckIns");

        checkInRef.addSnapshotListener((querySnapshots, error) -> {
            if (error != null) {
                Log.e("Firestore", error.toString());
                return;
            }

            if (querySnapshots != null) {
                HashMap<String, AttendeeCheckInView> attendeeMap = new HashMap<>();
                for (QueryDocumentSnapshot doc : querySnapshots) {
                    String attendeeId = doc.getString("attendeeId");
                    Timestamp checkInTime = doc.getTimestamp("checkInDate");


                    // Aggregate check-ins properly
                    AttendeeCheckInView checkInView = attendeeMap.getOrDefault(attendeeId,
                            new AttendeeCheckInView(attendeeId, "", 0, checkInTime));
                    checkInView.setCheckInFrequency(checkInView.getCheckInFrequency() + 1);
                    if (checkInView.getLatestCheckIn() == null || checkInTime.compareTo(checkInView.getLatestCheckIn()) > 0) {
                        checkInView.setLatestCheckIn(checkInTime);
                    }

                    attendeeMap.put(attendeeId, checkInView);


                }

                // Proceed to update details for each attendee
                fetchUserDetails(attendeeMap, adapter);

            }
        });
    }

    private void fetchUserDetails(HashMap<String, AttendeeCheckInView> attendeeMap, AttendeeCheckInAdapter adapter) {
        List<Task<?>> tasks = new ArrayList<>();

        for (String userId : attendeeMap.keySet()) {
            Task<DocumentSnapshot> fetchUserTask = userRef.document(userId).get();
            AttendeeCheckInView checkInView = attendeeMap.get(userId);

            Task<?> task = fetchUserTask.continueWithTask(userTask -> {
                DocumentSnapshot userDoc = userTask.getResult();
                if (userDoc != null && userDoc.exists()) {
                    String name = userDoc.getString("name");
                    DocumentReference imageDocRef = userDoc.getDocumentReference("imageUrl");
                    if (!TextUtils.isEmpty(name)) {
                        checkInView.setAttendeeName(name);
                    }

                    if (imageDocRef != null) {
                        // Fetch the document referenced for image URL
                        return imageDocRef.get();
                    }
                }
                // Return null task if user document is not found or imageDocRef is null
                return Tasks.forResult(null);
            }).continueWith(imageTask -> {
                DocumentSnapshot imageDoc = (DocumentSnapshot) imageTask.getResult();
                String imageUrl = null;
                if (imageDoc != null && imageDoc.exists()) {
                    imageUrl = imageDoc.getString("URL");
                }
                if (checkInView != null) {
                    checkInView.setProfileImageUrl(imageUrl);
                }
                return null; // You may adjust the return value based on your logic
            });

            tasks.add(task);
        }

        Tasks.whenAllComplete(tasks).addOnCompleteListener(allTasks -> {
            // This block executes when all tasks are completed (both user detail fetch and image URL fetch)
            adapter.setFilter(new ArrayList<>(attendeeMap.values()));
        });
    }

    // GEOLOCATION
    // TODO: Get real time updates for gelocation
    public interface OnLocationsFetched {
        void onFetched(Map<String, GeoPoint> locations);
    }

    public void getCheckInLocations (String eventId, OnLocationsFetched callback){
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