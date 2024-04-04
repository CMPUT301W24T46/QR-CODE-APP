package com.example.eventapp.attendee;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.eventapp.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class AttendeeNoSignUpEvent extends Fragment {

    private ImageView bigEventImageView;
    private TextView eventNameView, eventDescriptionView, eventDateView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_attendee_nosignup_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventNameView = view.findViewById(R.id.eventTitleDescrip_1);
        bigEventImageView = view.findViewById(R.id.biggerEventImage_1);
        eventDescriptionView = view.findViewById(R.id.eventFullDescription_1);
        eventDateView = view.findViewById(R.id.attendee_event_date_time_1);

        // Get eventId from received bundle
        Bundle receivedBundle = getArguments();
        if (receivedBundle != null) {
            String eventId = receivedBundle.getString("eventId");
            if (eventId != null) {
                Log.d("AttendeeNoSignUpEvent", "Received EventId: " + eventId);
                fetchEventDetails(eventId);
            } else {
                Log.d("AttendeeNoSignUpEvent", "EventId is null");
                Toast.makeText(getContext(), "Error: Event ID is missing.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.d("AttendeeNoSignUpEvent", "Bundle is null or does not contain EventId");
            Toast.makeText(getContext(), "Error: No event information provided.", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchEventDetails(String eventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Events").document(eventId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String eventName = documentSnapshot.getString("eventName");
                String eventDescription = documentSnapshot.getString("eventDescription");
                String eventDate = documentSnapshot.getString("eventDate");
                String imageURL = documentSnapshot.getString("imageURL");

                eventNameView.setText(eventName);
                eventDescriptionView.setText(eventDescription);
                eventDateView.setText(eventDate);

                if (imageURL != null && !imageURL.isEmpty()) {
                    Glide.with(requireContext()).load(imageURL).into(bigEventImageView);
                }
            } else {
                Toast.makeText(getContext(), "Event not found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Error fetching event details", Toast.LENGTH_SHORT).show());
    }
}