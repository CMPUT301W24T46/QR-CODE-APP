package com.example.eventapp.organizer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.eventapp.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
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
            // Log the event ID and collection path for debugging
            Log.d("Firestore", "Event ID: " + eventId);
            String collectionPath = "Events/" + eventId + "/CheckIns";
            Log.d("Firestore", "Collection Path: " + collectionPath);

            // Fetch attendees from Firestore
            db.collection(collectionPath)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        Log.d("Firestore", "Number of documents retrieved: " + queryDocumentSnapshots.size());
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<String> attendees = new ArrayList<>();
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                String attendeeId = documentSnapshot.getId();
                                attendees.add(attendeeId);
                            }
                            onAttendeesRetrieved(attendees);
                        } else {
                            onError("No attendees found for this event");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Error fetching attendees: " + e.getMessage());
                        onError("Failed to fetch attendees: " + e.getMessage());
                    });
        } else {
            onError("Event ID is null");
        }
    }


    @Override
    public void onAttendeesRetrieved(List<String> attendees) {
        // Process the retrieved attendees here
        Toast.makeText(requireContext(), "Retrieved " + attendees.size() + " attendees", Toast.LENGTH_SHORT).show();
        // Update the UI by populating a ListView
        ListView listView = requireView().findViewById(R.id.listview_attendees);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, attendees);
        listView.setAdapter(adapter);
    }

    @Override
    public void onError(String errorMessage) {
        Toast.makeText(requireContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
    }
}