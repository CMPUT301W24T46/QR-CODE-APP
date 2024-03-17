package com.example.eventapp.organizer;

import android.os.Bundle;
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
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Event Selection");
            actionBar.setDisplayHomeAsUpEnabled(true); // Enable the back button
        }
//        Button btnListAttendee = view.findViewById(R.id.btn_listAttendee);
//        btnListAttendee.setOnClickListener(v -> {
//            // Navigate to the list of attendees
//            Navigation.findNavController(v).navigate(R.id.);
//        });
//
//        Button btnQRCode = view.findViewById(R.id.btn_QRCode);
//        btnQRCode.setOnClickListener(v -> {
//            // Handle QR Code button click
//        });
//
//        Button btnUpdateEvent = view.findViewById(R.id.btn_updateEvent);
//        btnUpdateEvent.setOnClickListener(v -> {
//            // Navigate to update event fragment
//            Navigation.findNavController(v).navigate(R.id.);
//        });
//
//        Button btnLocationCheckIn = view.findViewById(R.id.btn_locationCheckIn);
//        btnLocationCheckIn.setOnClickListener(v -> {
//            // Handle location check-in button click
//        });
        navController = Navigation.findNavController(view);
    }
}
