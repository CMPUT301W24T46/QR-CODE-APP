package com.example.eventapp.organizer;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.eventapp.R;
import com.example.eventapp.event.Event;
import com.example.eventapp.event.EventAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrganizerEvent#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrganizerEvent extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OrganizerEvent() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrganizerEvent.
     */
    // TODO: Rename and change types and number of parameters
    public static OrganizerEvent newInstance(String param1, String param2) {
        OrganizerEvent fragment = new OrganizerEvent();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_organizer_event, container, false);
    }
    public void fetchEvents() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Events")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Event> eventList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String eventName = document.getString("eventName");
                            String eventDate = document.getString("eventDate");
                            String imageURL = document.getString("imageURL");
                            String creatorId = document.getString("creatorId");
                            Event event = new Event(eventName, eventDate, imageURL, creatorId);
                            eventList.add(event);
                        }
                        updateUI(eventList);
                    } else {
                        Log.d("EventFetch", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void updateUI(ArrayList<Event> events) {
        EventAdapter adapter = new EventAdapter(getContext(), events, event -> {
            // Handle event click
            navigateToEventInfo(event);
        });
        ListView listView = getView().findViewById(R.id.organizer_eventListView); // Replace with your actual ListView ID
        listView.setAdapter(adapter);
    }
    private void navigateToEventInfo(Event event) {
        Bundle bundle = new Bundle();
        bundle.putString("eventName", event.getEventName());
        bundle.putString("eventDate", event.getEventDate());
        bundle.putString("imageURL", event.getImageURL());
        bundle.putString("creatorId", event.getCreatorId());
        Navigation.findNavController(getView()).navigate(R.id.action_organizerEvent_to_organizerEventInfo, bundle);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchEvents();
        if (getArguments() != null) {
            String eventName = getArguments().getString("eventName");
            String eventDate = getArguments().getString("eventDate");
            String imageURL = getArguments().getString("imageURL");
            String creatorId = getArguments().getString("creatorId");
            // Now use these details to update your UI accordingly
        }

    }

}