package com.example.eventapp.organizer;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.eventapp.R;

public class OrganizerEditEventSelection extends Fragment {


    private NavController navController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_event_selection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle receivedBundle = getArguments();
        String eventId = null;
        if (receivedBundle != null) {
            eventId = receivedBundle.getString("eventId");
//            Log.d("OrganizerEditEventSelection", "Received EventId: " + eventId);
        }else {
            Log.d("OrganizerEditEventSelection", "Bundle is null or does not contain EventId");
        }
        final String eventId1 = eventId;

        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Event Selection");
            actionBar.setDisplayHomeAsUpEnabled(true); // Enable the back button
        }
        Button btnListAttendee = view.findViewById(R.id.btn_listAttendee);
        btnListAttendee.setOnClickListener(v -> {
            navController.navigate(R.id.action_organizer_edit_event_selection_to_organizer_attendees_list);
            Log.e("OrganizerEditEventSelection", "Error: Event ID is null");

            // Navigate to the list of attendees
//            Navigation.findNavController(v).navigate(R.id.action_organizer_edit_event_selection_to_organizer_attendees_list);
        });
//
        Button btnQRCode = view.findViewById(R.id.btn_QRCode);
        btnQRCode.setOnClickListener(v -> {
            Bundle qrBundle = new Bundle();
            qrBundle.putString("eventId", eventId1);
            navController.navigate(R.id.action_organizer_edit_event_selection_to_organizer_qrcode, qrBundle);
//            Log.d("OrganizerEditEventSelection", "EventId passed: " + eventId1);

        });
//
//        Button btnUpdateEvent = view.findViewById(R.id.btn_updateEvent);
//        btnUpdateEvent.setOnClickListener(v -> {
//            // Navigate to update event fragment
//            Navigation.findNavController(v).navigate(R.id.);
//        });
//

        Button btnLocationCheckIn = view.findViewById(R.id.btn_locationCheckIn);
        btnLocationCheckIn.setOnClickListener(v -> {
            Bundle eventBundle = new Bundle();
            eventBundle.putString("eventId", eventId1);
            navController.navigate(R.id.action_organizer_edit_event_selection_to_organizer_event_map, eventBundle);
            Log.d("OrganizerEditEventSelection", "EventId passed: " + eventId1);
        });

        navController = Navigation.findNavController(view);


    }
}
