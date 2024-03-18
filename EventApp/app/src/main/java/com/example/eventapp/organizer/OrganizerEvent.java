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
import android.widget.SearchView;

import com.example.eventapp.R;
import com.example.eventapp.event.Event;
import com.example.eventapp.event.EventAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;


public class OrganizerEvent extends Fragment {

    private EventAdapter adapter;
    private ArrayList<Event> allEvents = new ArrayList<>();
    private SearchView searchView;

    public OrganizerEvent() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organizer_event, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchView = view.findViewById(R.id.organizer_eventSearcher);
        fetchEvents();
        setupSearchView();
    }

    private void fetchEvents() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("Events")
                .orderBy("eventDate")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        allEvents.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Event event = document.toObject(Event.class);
                            String creatorId = document.getString("creatorId");
                            if (currentUserId.equals(creatorId)) { // Filter by creatorId
                                allEvents.add(event);
                            }
                        }
                        updateUI(new ArrayList<>(allEvents));
                    } else {
                        Log.d("EventFetch", "Error getting documents: ", task.getException());
                    }
                });
    }

    /**
     * Updates the user interface with the provided list of events.
     * Sets up the event adapter and attaches it to the ListView for event display.
     *
     * @param events The list of Event objects to display.
     */

    private void updateUI(ArrayList<Event> events) {
        adapter = new EventAdapter(getContext(), events, event -> {
            navigateToEventInfo(event);
        });
        ListView listView = getView().findViewById(R.id.organizer_eventListView);
        listView.setAdapter(adapter);
    }

    /**
     * Navigates to the event information page for a specific event.
     * Passes event details to the destination fragment via a Bundle.
     *
     * @param event The Event object whose details are to be displayed.
     */

    private void navigateToEventInfo(Event event) {
        Bundle bundle = new Bundle();
        bundle.putString("eventName", event.getEventName());
        bundle.putString("eventDate", event.getEventDate());
        bundle.putString("imageURL", event.getImageURL());
        bundle.putString("eventDescription", event.getEventDescription());
        //bundle.putString("creatorId", event.getCreatorId());
        OrganizerEventInfo fragment = new OrganizerEventInfo();
        fragment.setArguments(bundle);
        Navigation.findNavController(getView()).navigate(R.id.action_organizerEvent_to_organizerEventInfo, bundle);
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    updateUI(new ArrayList<>(allEvents));
                } else {
                    filterEvents(newText);
                }
                return true;
            }
        });
    }
    private void filterEvents(String text) {
        ArrayList<Event> filteredList = new ArrayList<>();
        for (Event event : allEvents) {
            if (event.getEventName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(event);
            }
        }
        updateUI(filteredList);
    }
}