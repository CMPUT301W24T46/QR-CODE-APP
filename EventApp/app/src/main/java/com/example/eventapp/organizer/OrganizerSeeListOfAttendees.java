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
import android.widget.ListView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.eventapp.R;
import com.example.eventapp.checkIn.AttendeeCheckInAdapter;
import com.example.eventapp.checkIn.AttendeeCheckInView;
import com.example.eventapp.checkIn.CheckInController;

import java.util.ArrayList;

public class OrganizerSeeListOfAttendees extends Fragment{

    private AttendeeCheckInAdapter adapter;
    private ListView listViewAttendees;
    private CheckInController controller;
    String eventId;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_of_attendees, container, false);

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
        ArrayList<AttendeeCheckInView> attendeesList = new ArrayList<>();
        adapter = new AttendeeCheckInAdapter(getContext(), attendeesList);
        listViewAttendees.setAdapter(adapter);

        controller = new CheckInController();

        controller.subscribeToEventCheckIns(eventId, adapter);
    }
}