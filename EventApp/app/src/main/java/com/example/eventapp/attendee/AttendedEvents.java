package com.example.eventapp.attendee;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.eventapp.R;
import com.example.eventapp.event.Event;
import com.example.eventapp.event.EventAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * AttendedEvents is an {@link AppCompatActivity} that displays a list of events an attendee has attended.
 * This activity fetches data from a Firestore collection named "AttendedEvents", where each document corresponds
 * to a user and contains a list of events they have attended. The events are displayed in a {@link ListView}
 * using an {@link EventAdapter}.
 *
 * <p>Key Features:</p>
 * <ul>
 *     <li>Displays a list of events the current user has attended.</li>
 *     <li>Each list item includes event details such as name, date, description, and an image URL.</li>
 *     <li>Clicking on an event in the list opens the {@link AttendedEventInformationActivity}, showing more
 *     detailed information about the event.</li>
 *     <li>Utilizes Firebase Firestore to fetch attended events data.</li>
 *     <li>Provides a back button in the ActionBar for easy navigation back to the previous screen.</li>
 * </ul>
 */

public class AttendedEvents extends AppCompatActivity {
    private FirebaseFirestore db;
    private ArrayList<Event> attendedEventsList;
    private ListView eventList;
    private EventAdapter eventListAdapter;

    /**
     * Sets up the activity's UI by inflating the layout, initializing UI components, and configuring the ActionBar.
     * It also initializes Firestore and the ListView adapter, and it starts the process of loading attended events
     * from Firestore.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this
     *                           Bundle contains the data most recently supplied in onSaveInstanceState(Bundle).
     *                           Otherwise, it is null. This bundle can be used to recreate the activity's state.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attended_event_list);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Attended Events");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        db = FirebaseFirestore.getInstance();
        eventList = findViewById(R.id.listViewAttendedEvents);
        attendedEventsList = new ArrayList<>();

        EventAdapter.OnEventClickListener eventClickListener = new EventAdapter.OnEventClickListener() {
            @Override
            public void onEventClick(Event event) {
                Intent intent = new Intent(AttendedEvents.this, AttendedEventInformationActivity.class);
                intent.putExtra("eventName", event.getEventName());
                intent.putExtra("eventDate", event.getEventDate());
                intent.putExtra("eventDescription", event.getEventDescription());
                intent.putExtra("imageURL", event.getImageURL());
                startActivity(intent);
            }
        };

        eventListAdapter = new EventAdapter(this, attendedEventsList, eventClickListener);
        eventList.setAdapter(eventListAdapter);

        loadAttendedEvents();
    }

    /**
     * Fetches the list of attended events for the current user from Firestore. It updates the ListView adapter
     * with the fetched events to display them. If no events are found, or in case of an error, it logs the
     * appropriate message.
     */

    private void loadAttendedEvents() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference userDocRef = db.collection("AttendedEvents").document(currentUserId);
        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<Map<String, Object>> attendedEvents = (List<Map<String, Object>>) documentSnapshot.get("allAttendedEvents");
                if (attendedEvents != null) {
                    attendedEventsList.clear();
                    for (Map<String, Object> eventData : attendedEvents) {
                        String eventName = (String) eventData.get("eventName");
                        String eventDate = (String) eventData.get("eventDate");
                        String eventDescription = (String) eventData.get("eventDescription");
                        String imageURL = (String) eventData.get("imageURL");
                        attendedEventsList.add(new Event(eventName, eventDate, eventDescription, imageURL));
                    }
                    eventListAdapter.notifyDataSetChanged();
                }
            } else {
                Log.d("AttendedEvents", "No attended events found for user: " + currentUserId);
            }
        }).addOnFailureListener(e -> {
            Log.e("AttendedEvents", "Error fetching attended events for user: " + currentUserId, e);
        });
    }

    /**
     * Handles the selection of items in the options menu. Specifically, it listens for the press of the home
     * button in the ActionBar, triggering a back navigation when pressed.
     *
     * @param item The menu item that was selected.
     * @return true to consume the menu selection here, false to allow normal menu processing to continue.
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
