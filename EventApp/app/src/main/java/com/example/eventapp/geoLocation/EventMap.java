package com.example.eventapp.geoLocation;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventapp.BuildConfig;
import com.example.eventapp.R;
import com.example.eventapp.checkIn.CheckInController;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.GeoPoint;
import com.google.protobuf.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.example.eventapp.BuildConfig;

public class EventMap extends Fragment implements OnMapReadyCallback {

    SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private String eventId;
    private CheckInController controller;

    private String apiKey = BuildConfig.GOOGLE_MAPS_API_KEY;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_organizer_event_map, container, false);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.event_map);

        // Retrieve the eventId from the arguments
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey("eventId")) {
            eventId = arguments.getString("eventId");
            Log.d("EventMap", "EventId received: " + eventId);
        } else {
            Log.e("EventMap", "No eventId passed to EventMap fragment");
        }


        if (mapFragment != null) {
            mapFragment.getMapAsync((OnMapReadyCallback) this);
        } else {
            Log.e("EventMap", "SupportMapFragment is null");
        }

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // customize map
        addAndFocusOnDenseAreaMarkers();

        // Enable zoom buttons
        mMap.getUiSettings().setZoomControlsEnabled(true);

    }

    private void addAndFocusOnDenseAreaMarkers() {
        if (eventId != null) {
            CheckInController controller = new CheckInController();
            controller.getCheckInLocations(eventId, locationsMap -> {
                if (locationsMap.isEmpty()) {
                    Log.e("EventMap", "No check-in locations available.");
                    return;
                }

                List<LatLng> markerPositions = new ArrayList<>();
                for (Map.Entry<String, GeoPoint> entry : locationsMap.entrySet()) {
                    GeoPoint point = entry.getValue();
                    LatLng latLng = new LatLng(point.getLatitude(), point.getLongitude());
                    markerPositions.add(latLng);

                    // Add markers to the map for each check-in location
                    mMap.addMarker(new MarkerOptions().position(latLng).title(entry.getKey()));
                }

                // After adding all markers, calculate the densest area and focus the camera there
                LatLng denseCenter = calculateDenseCenter(markerPositions);
                if (denseCenter != null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(denseCenter, 10));
                }
            });
        } else {
            Log.e("EventMap", "EventId is null");
        }
    }

    private LatLng calculateDenseCenter(List<LatLng> positions) {
        if (positions.isEmpty()) return null;

        // Simple approach: use the first marker as a reference for finding the densest area
        LatLng reference = positions.get(0);
        double minDistanceSum = Double.MAX_VALUE;
        LatLng densestPoint = null;

        // Find the marker with the smallest sum of distances to all others
        for (LatLng position : positions) {
            double distanceSum = 0;
            for (LatLng other : positions) {
                distanceSum += distanceBetween(position, other);
            }
            if (distanceSum < minDistanceSum) {
                minDistanceSum = distanceSum;
                densestPoint = position;
            }
        }

        // Optionally, refine the center by averaging the positions of the closest markers to the densestPoint
        // For simplicity, this step is skipped here, but could be added for a more accurate center

        return densestPoint;
    }

    private double distanceBetween(LatLng pos1, LatLng pos2) {
        double earthRadius = 6371000; // meters
        double dLat = Math.toRadians(pos2.latitude - pos1.latitude);
        double dLng = Math.toRadians(pos2.longitude - pos1.longitude);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(pos1.latitude)) * Math.cos(Math.toRadians(pos2.latitude)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;

        return distance;
    }


}
