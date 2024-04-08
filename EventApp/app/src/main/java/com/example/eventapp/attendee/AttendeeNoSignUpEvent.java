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

/**
 * This displays detailed information about an event for attendees who have not signed up.
 * It shows event details including the name, description, date, and an image associated with the event.
 * This fragment is intended to be used within an application that manages events, providing a way for users
 * to explore event details even if they haven't registered or signed up for the event.
 * Layout File: R.layout.fragment_attendee_nosignup_event
 */

public class AttendeeNoSignUpEvent extends Fragment {

    private ImageView bigEventImageView;
    private TextView eventNameView, eventDescriptionView, eventDateView;

    /**
     * Called to have the fragment instantiate its user interface view. This is optional, and non-graphical fragments can return null.
     * This will be called between onCreate(Bundle) and onActivityCreated(Bundle).
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to. The fragment should not add the view itself,
     *                  but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_attendee_nosignup_event, container, false);
    }

    /**
     * Called immediately after onCreateView(LayoutInflater, ViewGroup, Bundle) has returned, but before any saved state has been restored in to the view.
     * This gives subclasses a chance to initialize themselves once they know their view hierarchy has been completely created.
     *
     * @param view The View returned by onCreateView(LayoutInflater, ViewGroup, Bundle).
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */

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

    /**
     * Fetches event details from the Firestore database using the provided event ID and updates the UI with these details.
     *
     * @param eventId The ID of the event for which details are to be fetched.
     */

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