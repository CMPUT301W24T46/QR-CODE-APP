package com.example.eventapp.organizer;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.eventapp.R;
import com.example.eventapp.checkIn.AttendeeCheckInAdapter;
import com.example.eventapp.checkIn.AttendeeCheckInView;
import com.example.eventapp.checkIn.CheckInController;
import com.example.eventapp.registrations.Registration;
import com.example.eventapp.registrations.RegistrationAdapter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class OrganizerSeeListOfAttendees extends Fragment{

    private AttendeeCheckInAdapter checkInAdapter;
    private ListView listViewAttendees;
    private CheckInController controller;
    String eventId;
    private RegistrationAdapter registrationAdapter;

    private NavController navController;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_organizer_attendee_list, container, false);

        // Retrieve the eventId from the arguments
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey("eventId")) {
            eventId = arguments.getString("eventId");
            Log.d("EventList", "EventId received: " + eventId);
        } else {
            Log.e("EventList", "No eventId passed to fragment");
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the ListView and Adapter
        listViewAttendees = view.findViewById(R.id.listview_attendees);
        // check in list
        ArrayList<AttendeeCheckInView> attendeesList = new ArrayList<>();
        checkInAdapter = new AttendeeCheckInAdapter(getContext(), attendeesList);

        // registration list
        ArrayList<Registration> registrationsList = new ArrayList<>();
        registrationAdapter = new RegistrationAdapter(getContext(), registrationsList);

        controller = new CheckInController();

        Spinner spinner = view.findViewById(R.id.spinner_eventActions);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.event_action_options, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner_item);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: // Check-ins
                        listViewAttendees.setAdapter(checkInAdapter);
                        controller.subscribeToEventCheckIns(eventId, checkInAdapter);
                        break;
                    case 1: // Registrations
                        listViewAttendees.setAdapter(registrationAdapter);
                        controller.subscribeToEventRegistrations(eventId, registrationAdapter);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

//        controller.subscribeToEventCheckIns(eventId, adapter);

        // Norify Attendees Button
        navController = Navigation.findNavController(view);
        Button buttonCreateAnnouncement = view.findViewById(R.id.button_notifyAttendees);
        db = FirebaseFirestore.getInstance();
        buttonCreateAnnouncement.setOnClickListener(v -> {
            // Show notify attendees dialog
            CreateNotificationFragment dialogFragment = new CreateNotificationFragment();
            Bundle args = new Bundle();
            args.putString("eventId", eventId);
            dialogFragment.setArguments(args);
            dialogFragment.show(requireActivity().getSupportFragmentManager(), "CreateNotificationDialog");
        });
    }
}