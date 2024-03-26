package com.example.eventapp.organizer;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

public class OrganizerEventInfo extends Fragment {

    private NavController navController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_organizer_event_info, container, false);
    }

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
        }
    }

}