package com.example.eventapp.geoLocation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.eventapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class EventUserLocation extends Fragment {

    public static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private TextView eventMapTitle, address, city, country, latitude, longitude, ipAddress;
    private Button buttonGetLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_attendee_location, container, false);


        // Initialize UI components
        eventMapTitle = view.findViewById(R.id.eventMapTitle);
        address = view.findViewById(R.id.address);
        city = view.findViewById(R.id.city);
        country = view.findViewById(R.id.country);
        latitude = view.findViewById(R.id.latitude);
        longitude = view.findViewById(R.id.longitude);
        ipAddress = view.findViewById(R.id.ip_address);
        buttonGetLocation = view.findViewById(R.id.button_get_location);

        if (getArguments() != null && getArguments().containsKey("eventId")) {
            String eventId = getArguments().getString("eventId");
            eventMapTitle.setText(eventId);
            Log.d("EventMap", "Received event: " + eventId);

        } else {
            Log.d("EventMap", "No eventId found in fragment arguments");
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Setup button click listener
        buttonGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAndDisplayLocation();
//                generateAndDisplayRandomLocation();
            }

        });

        return view;
    }

    private void getAndDisplayLocation() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    updateLocationUI(location);
                }
            });
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void updateLocationUI(Location location) {
        if (location != null) {
            Geocoder geocoder = new Geocoder(requireActivity(), Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (!addresses.isEmpty()) {
                    Address addressObj = addresses.get(0);

                    latitude.setText(String.format("Latitude: %s", addressObj.getLatitude()));
                    longitude.setText(String.format("Longitude: %s", addressObj.getLongitude()));
                    address.setText(String.format("Address: %s", addressObj.getAddressLine(0)));
                    city.setText(String.format("City: %s", addressObj.getLocality()));
                    country.setText(String.format("Country: %s", addressObj.getCountryName()));
                }
            } catch (IOException e) {
                Log.e("EventMapFragment", "Error fetching location details", e);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getAndDisplayLocation();
            } else {
                // Permission denied
                Log.d("EventMapFragment", "Location permission was denied by the user.");
            }
        }
    }

    private void generateAndDisplayRandomLocation() {
        Random random = new Random();
        double lat = -90 + 180 * random.nextDouble();
        double lon = -180 + 360 * random.nextDouble();

        // Displaying the randomly generated location
        latitude.setText(String.format(Locale.US, "Latitude: %s", lat));
        longitude.setText(String.format(Locale.US, "Longitude: %s", lon));
        address.setText("Address: Randomly Generated");
        city.setText("City: Random City");
        country.setText("Country: Random Country");
    }


}
