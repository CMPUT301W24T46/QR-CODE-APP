package com.example.eventapp.organizer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.eventapp.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class OrganizerSeeListOfAttendees extends Fragment implements AttendeeManager.AttendeeRetrievalListener {
    private NavController navController;
    private FirebaseFirestore db;
    private String eventId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventId = getArguments().getString("eventId");
            Log.d("OrganizerSeeListOfAttendees","EventId" + eventId);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_of_attendees, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            eventId = bundle.getString("eventId");
        }
        navController = Navigation.findNavController(view);
        Button buttonCreateAnnouncement = view.findViewById(R.id.button_notifyAttendees);
        db = FirebaseFirestore.getInstance();
        buttonCreateAnnouncement.setOnClickListener(v -> {
            // Show notify attendees dialog
            CreateNotificationFragment dialogFragment = new CreateNotificationFragment();
            dialogFragment.show(requireActivity().getSupportFragmentManager(), "CreateNotificationDialog");
        });
        fetchEventIdAndAttendees();
    }

    private void fetchEventIdAndAttendees() {
        if (eventId != null) {
            AttendeeManager attendeeManager = new AttendeeManager();
            attendeeManager.getAllAttendeesForEvent(eventId, this);
        } else {
            onError("Event ID is null");
        }
    }


    interface EventIdCallback {
        void onEventIdReceived(String eventId);
    }

    @Override
    public void onAttendeesRetrieved(List<String> attendees) {
        // Process the retrieved attendees here
        Toast.makeText(requireContext(), "Retrieved " + attendees.size() + " attendees", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(String errorMessage) {
        // Handle errors during attendee retrieval
        Toast.makeText(requireContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
    }
}