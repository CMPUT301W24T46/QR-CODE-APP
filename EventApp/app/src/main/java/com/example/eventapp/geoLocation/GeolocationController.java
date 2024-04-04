package com.example.eventapp.geoLocation;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.example.eventapp.checkIn.CheckIn;
import com.example.eventapp.checkIn.CheckInAdapter;
import com.example.eventapp.event.Event;
import com.example.eventapp.event.EventAdapter;
import com.example.eventapp.users.User;
import com.example.eventapp.users.UserAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.tasks.Task;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GeolocationController {

    private final CollectionReference eventRef;
    private final Map<String, GeoPoint> attendeeLocationRefMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Context context;


    public GeolocationController(Context context) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        this.context = context;
        this.eventRef = db.collection("Events");
        this.attendeeLocationRefMap = new HashMap<>();

        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        // Override methods to handle location updates
        this.locationCallback = new LocationCallback() {
        };
    }




    // Geolocation toggle for attendee
    // Method to enable geolocation features
    public void enableGeolocationFeatures(String userId) {
        if (ContextCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setInterval(10000);  // 10 seconds
            locationRequest.setFastestInterval(5000);  // 5 seconds
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
                    .addOnSuccessListener(aVoid -> {
                        // Location updates started successfully
                        Log.d("Geolocation", "Location updates started for: " + userId);
                        // Update attendee's status in Firestore to indicate they are sharing their location
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure to start location updates
                        Log.e("Geolocation", "Failed to start location updates for: " + userId, e);
                    });
        } else {
            // Log or handle lack of permissions as appropriate
            Log.e("Geolocation", "Location permission not granted for: " + userId);
            // Depending on your app flow, you might notify the user here
        }
    }

    // Method to disable geolocation features
    public void disableGeolocationFeatures(String userId) {
        fusedLocationClient.removeLocationUpdates(locationCallback)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Location updates stopped successfully
                        Log.d("Geolocation", "Location updates stopped for: " + userId);
                        // Update attendee's status in Firestore to indicate they are no longer sharing their location
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        // Handle failure to stop location updates
                        Log.e("Geolocation", "Failed to stop location updates for: " + userId, e);
                    }
                });
    }

}
