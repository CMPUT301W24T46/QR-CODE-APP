package com.example.eventapp.geoLocation;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventapp.R;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.GeoPoint;

import java.util.Map;

public class EventMap extends Fragment implements OnMapReadyCallback {

    SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private String eventId;
    private GeolocationController controller;


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
        addLocationMarkers();

        // Enable zoom buttons
        mMap.getUiSettings().setZoomControlsEnabled(true);

    }

    private void addLocationMarkers() {
        if (eventId != null) {
            GeolocationController controller = new GeolocationController(); // Ensure this is initialized appropriately
            controller.getCheckInLocations(eventId, locationsMap -> {
                double totalLat = 0;
                double totalLng = 0;
                int count = 0;

                for (Map.Entry<String, GeoPoint> entry : locationsMap.entrySet()) {
                    String attendeeId = entry.getKey();
                    GeoPoint location = entry.getValue();
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title(attendeeId));

                    totalLat += location.getLatitude();
                    totalLng += location.getLongitude();
                    count++;
                }

                // move camera to a centroid location
                if (count > 0) {
                    LatLng centroid = new LatLng(totalLat / count, totalLng / count);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centroid, 10));
                }

            });
        } else {
            Log.e("EventMap", "EventId is null");
        }
    }
}
