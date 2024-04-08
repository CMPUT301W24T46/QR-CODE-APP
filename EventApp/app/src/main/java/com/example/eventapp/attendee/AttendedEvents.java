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

public class AttendedEvents extends AppCompatActivity {
    private FirebaseFirestore db;
    private ArrayList<Event> attendedEventsList;
    private ListView eventList;
    private EventAdapter eventListAdapter;

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
