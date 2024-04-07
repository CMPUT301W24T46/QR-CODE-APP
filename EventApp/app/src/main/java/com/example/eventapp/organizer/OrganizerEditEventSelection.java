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

/**
 * OrganizerEditEventSelection is a Fragment that provides various options for organizers to manage their events.
 * This includes viewing the list of attendees, generating and viewing QR codes, updating event details, and viewing
 * check-in locations. It receives an 'eventId' from its arguments to identify the event being edited.
 */

public class OrganizerEditEventSelection extends Fragment {

    // TODO: event data is lost on back navigations to edit page


    private NavController navController;

    /**
     * Called when the fragment is first created. Sets up the fragment to have an options menu.
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    /**
     * Called to have the fragment instantiate its user interface view. Inflates the layout for this fragment.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return Return the View for the fragment's UI.
     */

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_organizer_edit_event_selection, container, false);
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} has returned, but before any
     * saved state has been restored in to the view. Sets up the navigation controller and button click listeners
     * to navigate to different editing options for the selected event.
     *
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     */
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
            Bundle attendeeBundle = new Bundle();
            attendeeBundle.putString("eventId", eventId1);
            navController.navigate(R.id.action_organizer_edit_event_selection_to_organizer_attendees_list, attendeeBundle);
//            Log.d("OrganizerEditEventSelection", "EventId passed: " + eventId1);

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

        Button btnQRCodeEventInfo = view.findViewById(R.id.btn_qrcode_eventinfo);
        btnQRCodeEventInfo.setOnClickListener(v -> {
            Bundle qrBundle = new Bundle();
            qrBundle.putString("eventId", eventId1);
            navController.navigate(R.id.action_organizer_edit_event_selection_to_organizerQRCodeEventInfo, qrBundle);
//            Log.d("OrganizerEditEventSelection", "EventId passed: " + eventId1);

        });

        Button btnUpdateEvent = view.findViewById(R.id.btn_updateEvent);
        btnUpdateEvent.setOnClickListener(v -> {
            Bundle updateBundle = new Bundle();
            updateBundle.putString("eventId", eventId1);
            // Log.d("OrganizerEditEventSelection", "eventId" + eventId1);
            navController.navigate(R.id.action_organizer_edit_event_selection_to_organizer_update_event, updateBundle);
        });

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
