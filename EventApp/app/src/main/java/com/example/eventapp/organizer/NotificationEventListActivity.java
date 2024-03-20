package com.example.eventapp.organizer;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eventapp.R;
import com.example.eventapp.event.Event;
import com.example.eventapp.event.EventAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class NotificationEventListActivity extends AppCompatActivity {

    private NotificationEventAdapter adapter;
    private ArrayList<Event> allEvents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_notification_event_list);
        fetchEvents();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void updateUI(ArrayList<Event> events) {
        adapter = new NotificationEventAdapter(this, events);
        ListView listView = findViewById(R.id.organizer_eventListView);
        listView.setAdapter(adapter);
        // Set item click listener if needed
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
                            if (currentUserId.equals(creatorId)) {
                                allEvents.add(event);
                            }
                        }
                        updateUI(new ArrayList<>(allEvents));
                    } else {
                        // Handle error
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
