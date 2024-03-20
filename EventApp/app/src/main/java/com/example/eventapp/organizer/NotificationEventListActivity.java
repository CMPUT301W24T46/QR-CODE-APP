package com.example.eventapp.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import com.example.eventapp.R;
import com.example.eventapp.event.Event;
import com.example.eventapp.event.EventAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class NotificationEventListActivity extends AppCompatActivity {

    private EventAdapter adapter;
    private ArrayList<Event> allEvents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_notification_event_list);
        fetchEvents();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        View selectButtonLayout = getLayoutInflater().inflate(R.layout.content_of_event_display_list, null);
        Button selectButton = selectButtonLayout.findViewById(R.id.btnViewEvent);
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the previous fragment
                Intent intent = new Intent(NotificationEventListActivity.this, CreateNotificationFragment.class);
                startActivity(intent);
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
        adapter = new EventAdapter(this, events, event -> {
            navigateToEventInfo(event);
        });
        ListView listView = findViewById(R.id.organizer_eventListView);
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
        Navigation.findNavController(this, R.id.organizer_eventListView).navigate(R.id.action_organizerEvent_to_organizerEventInfo, bundle);
    }

    /**
     * Fetches events from Firestore.
     * Filters events by the current user's ID.
     * Updates the UI with the fetched events.
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
