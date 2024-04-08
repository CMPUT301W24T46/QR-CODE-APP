package com.example.eventapp.organizer;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.eventapp.R;
import com.example.eventapp.event.Event;
import com.example.eventapp.event.EventAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

/**
 * OrganizerEvent is a Fragment that displays a list of events created by the current organizer. It provides
 * functionality to search through the events and navigate to detailed information for a selected event.
 */

public class OrganizerEvent extends Fragment {

    private EventAdapter adapter;
    private ArrayList<Event> allEvents = new ArrayList<>();
    private SearchView searchView;

    /**
     * Required empty public constructor.
     */

    public OrganizerEvent() {
        // Required empty public constructor
    }

    /**
     * Called to have the fragment instantiate its user interface view. Inflates the layout for this fragment.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return Return the View for the fragment's UI.
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organizer_event, container, false);
        return view;
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} has returned, but before any
     * saved state has been restored into the view. Initializes the SearchView and fetches events for the organizer.
     *
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     */

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchView = view.findViewById(R.id.organizer_eventSearcher);
        String uid = FirebaseAuth.getInstance().getUid();

        if(uid != null){
            fetchEvents();
        }else{
            loadStaticEvents();
        }
        setupSearchView();
    }

    private void loadStaticEvents() {
        allEvents.add(new Event("Event 1" , "EventUrl" , "123456" , "sjfbdfjbdfj", "ksdnfksd" , "sdcdscsdcds")) ;
        allEvents.add(new Event("Event 2" , "EventUrl" , "123456" , "sjfbdfjbdfj", "ksdnfksd" , "sdcdscsdcds")) ;
        updateUI(new ArrayList<>(allEvents));
    }

    /**
     * Fetches events created by the current organizer from the database and updates the UI.
     * Filters the events to only show those created by the current user.
     */

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
                            event.setEventId(document.getId());
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
        bundle.putString("eventId", event.getEventId());
        OrganizerEventInfo fragment = new OrganizerEventInfo();
        fragment.setArguments(bundle);
        Navigation.findNavController(getView()).navigate(R.id.action_organizerEvent_to_organizerEventInfo, bundle);
    }

    /**
     * Sets up the SearchView with a listener for text input to filter events based on the search query.
     */

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String uid = FirebaseAuth.getInstance().getUid();
                if(uid == null){
                    filterEvents(query);
                }
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

    /**
     * Filters the events list based on the search query and updates the UI with the filtered list.
     *
     * @param text The search query to filter events by.
     */

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