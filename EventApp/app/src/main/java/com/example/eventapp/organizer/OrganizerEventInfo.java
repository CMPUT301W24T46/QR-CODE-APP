package com.example.eventapp.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.example.eventapp.R;

import java.util.Objects;

/**
 * OrganizerEventInfo is a Fragment that displays detailed information about a specific event.
 * It shows the event's name, description, date, and image, and provides options to edit the event
 * or reuse the event's QR code.
 */

public class OrganizerEventInfo extends Fragment {

    private NavController navController;

    /**
     * Called when the fragment is first created. This method initializes the fragment.
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return Return the View for the fragment's UI, or null.
     */

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_organizer_event_info, container, false);
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} has returned, but before any
     * saved state has been restored into the view. This method initializes the fragment's content and event handlers.
     *
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     */

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Event Information");
            actionBar.setDisplayHomeAsUpEnabled(true); // Enable the back button
        }

        // Retrieve the event details from the Bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            String eventName = bundle.getString("eventName");
            String eventDate = bundle.getString("eventDate");
            String imageUrl = bundle.getString("imageURL");
            String eventDescription = bundle.getString("eventDescription");
            String eventId = bundle.getString("eventId");
//            Log.d("OrganizerEventInfo", "Event ID: " + eventId);

            TextView eventNameView = view.findViewById(R.id.eventName_info);
            TextView eventDescriptionView = view.findViewById(R.id.eventDescription_info);
            TextView eventDateView = view.findViewById(R.id.event_date_time);
            ImageView eventImageView = view.findViewById(R.id.organizer_biggerEventImage);

            eventNameView.setText(eventName);
            eventDescriptionView.setText(eventDescription);
            eventDateView.setText(eventDate);

            // Log.d("eventDescription", "Event Description: " + eventDescription);

            // Load the event image
            Glide.with(this).load(imageUrl).into(eventImageView);

            navController = Navigation.findNavController(view);
            View editEventButton = view.findViewById(R.id.button_editEvent_info);
            editEventButton.setOnClickListener(v -> {
                Bundle newBundle = new Bundle();
                newBundle.putString("eventId", eventId);
//                Log.d("OrganizerEventInfo", "Navigating with Event ID: " + eventId);
                navController.navigate(R.id.action_organizerEventInfo_to_organizer_edit_event_selection,newBundle);
            });
            View reuseQRCodeButton = view.findViewById(R.id.btn_reuse_qrcode);
            reuseQRCodeButton.setOnClickListener(v -> {
                // Check if eventId is not null or empty
                if (eventId != null && !eventId.isEmpty()) {
                    Intent intent = new Intent(getContext(), QRCodeReuseActivity.class);
                    intent.putExtra("eventId", eventId);
                    startActivity(intent);
                } else {
                    // Log error or show a message if eventId is null or empty
                    Log.e("OrganizerEventInfo", "Event ID is null or empty. Cannot navigate to QRCodeReuseActivity.");
                    Toast.makeText(getContext(), "Error: No event ID found.", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

}