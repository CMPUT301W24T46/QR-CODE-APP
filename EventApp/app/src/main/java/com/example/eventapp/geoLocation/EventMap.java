package com.example.eventapp.geoLocation;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

//import com.example.eventapp.BuildConfig;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import com.example.eventapp.BuildConfig;

/**
 * EventMap is a Fragment that displays a map showing locations related to an event. This includes locations where attendees
 * have checked in. The map provides a visual representation of where the event's participants are distributed geographically.
 * This class uses Google Maps to display the map and markers for each check-in location.
 */


public class EventMap extends Fragment implements OnMapReadyCallback {

    SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private String eventId;
    private CheckInController controller;

//    private String apiKey = BuildConfig.GOOGLE_MAPS_API_KEY;

    /**
     * Inflates the layout for this fragment and initializes the map fragment.
     * Retrieves the event ID from the fragment's arguments, if provided.
     *
     * @param inflater           LayoutInflater object to inflate any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, the fragment is being re-constructed from a previous saved state.
     * @return The View for the inflated layout of the fragment.
     */

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

    /**
     * Called when the Google Map is ready to be used. Sets up map configurations and adds markers for each check-in location.
     *
     * @param googleMap The GoogleMap object representing the map to be displayed.
     */

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // customize map
        addAndFocusOnDenseAreaMarkers();

        // Enable zoom buttons
        mMap.getUiSettings().setZoomControlsEnabled(true);

    }

    /**
     * Adds markers to the map for each dense area where attendees have checked in. Aggregates check-in locations to identify
     * areas with multiple check-ins and focuses the map on these dense areas.
     */

    private void addAndFocusOnDenseAreaMarkers() {
        if (eventId != null) {
            CheckInController controller = new CheckInController();
            controller.getCheckInLocations(eventId, locationsMap -> {
                if (locationsMap.isEmpty()) {
                    Log.e("EventMap", "No check-in locations available.");
                    return;
                }

                // Check if any location has multiple check ins
                Map<LatLng, List<String>> aggregatedLocations = new HashMap<>();
                for (Map.Entry<String, GeoPoint> entry : locationsMap.entrySet()) {
                    LatLng latLng = new LatLng(entry.getValue().getLatitude(), entry.getValue().getLongitude());
                    aggregatedLocations.putIfAbsent(latLng, new ArrayList<>());
                    aggregatedLocations.get(latLng).add(entry.getKey());
                }

                // Add markers to the map
                for (Map.Entry<LatLng, List<String>> entry : aggregatedLocations.entrySet()) {
                    LatLng position = entry.getKey();
                    List<String> ids = entry.getValue();
                    String markerTitle;
                    if (ids.size() == 1) {
                        // If there's only one check-in, use the attendee ID as the title
                        markerTitle = ids.get(0);
                    } else {
                        // For multiple check-ins, indicate the number of check-ins at this location
                        markerTitle = ids.size() + " check-ins in this location";
                    }
                    mMap.addMarker(new MarkerOptions().position(position).title(markerTitle));
                }

                // Focus the camera on the densest area on map
                if (!aggregatedLocations.isEmpty()) {
                    LatLng denseCenter = calculateDenseCenter(new ArrayList<>(aggregatedLocations.keySet()));
                    if (denseCenter != null) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(denseCenter, 10));
                    }
                }
            });
        } else {
            Log.e("EventMap", "EventId is null");
        }
    }

    /**
     * Calculates the geographical center of the densest area based on the check-in locations. This is used to focus the map's
     * camera on the area with the highest concentration of attendees.
     *
     * @param positions A list of LatLng objects representing the check-in locations.
     * @return The LatLng object representing the center of the densest area.
     */

    private LatLng calculateDenseCenter(List<LatLng> positions) {
        if (positions.isEmpty()) return null;

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

        return densestPoint;
    }

    /**
     * Calculates the distance between two geographical points.
     *
     * @param pos1 The first geographical point.
     * @param pos2 The second geographical point.
     * @return The distance in meters between the two points.
     */

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
