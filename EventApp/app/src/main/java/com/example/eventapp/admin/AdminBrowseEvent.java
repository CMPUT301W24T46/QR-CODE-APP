package com.example.eventapp.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import com.example.eventapp.R;
import com.example.eventapp.attendee.AttendeeEventInformation;
import com.example.eventapp.event.Event;
import com.example.eventapp.event.EventAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AdminBrowseEvent extends AppCompatActivity {
    private FirebaseFirestore db;
    private SearchView searchView;
    private CollectionReference eventsRef;
    private ArrayList<Event> eventDataList;
    private ListView eventList;
    private EventAdapter eventListArrayAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_attendee_event);

        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("Events");

        searchView = findViewById(R.id.eventSearcher);
        eventList = findViewById(R.id.eventListView);

        eventDataList = new ArrayList<>();

        EventAdapter.OnEventClickListener eventClickListener = new EventAdapter.OnEventClickListener() {
            @Override
            public void onEventClick(Event event) {
                Intent intent = new Intent(AdminBrowseEvent.this, AdminDeleteEvent.class);
                intent.putExtra("EventName", event.getEventName());
                intent.putExtra("ImageURL", event.getImageURL());
                startActivity(intent);
            }
        };

        eventListArrayAdapter = new EventAdapter(this, eventDataList, eventClickListener) ;
        eventList.setAdapter(eventListArrayAdapter);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Browse Events");
            actionBar.setDisplayHomeAsUpEnabled(true); // Enable the back button
        }

        setUpSearchView();
        subscribeToFirestore();
    }

    private void setUpSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(TextUtils.isEmpty(newText)) {
                    getCurrentEventList("", false);
                } else {
                    getCurrentEventList(newText, true);
                }
                return false;
            }
        });
    }

    private void subscribeToFirestore() {
        eventsRef.addSnapshotListener((querySnapshots, error) -> {
            if (error != null) {
                Log.e("Firestore", error.toString());
                return;
            }
            if (querySnapshots != null) {
                eventDataList.clear();
                for (QueryDocumentSnapshot doc : querySnapshots) {
                    String eventId = doc.getId();
                    String eventName = doc.getString("Name");
                    String imageURL = doc.getString("URL");
                    Log.d("Firestore", String.format("Name(%s, %s) fetched", eventId, eventName));
                    eventDataList.add(new Event(eventName, imageURL));
                }
                eventListArrayAdapter.notifyDataSetChanged();
            }
        });
    }

    private void getCurrentEventList(String searchText, boolean queryOrDisplay) {
        eventsRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            ArrayList<Event> searchResults = new ArrayList<>();
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                String eventName = documentSnapshot.getString("Name");
                String URL = documentSnapshot.getString("URL");
                if (!queryOrDisplay) {
                    searchResults.add(new Event(eventName, URL));
                    continue;
                }
                if (eventName.toLowerCase().contains(searchText.toLowerCase())) {
                    searchResults.add(new Event(eventName, URL));
                }
            }
            eventListArrayAdapter.setFilter(searchResults);
            eventListArrayAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> Log.e("TAG", "Error getting documents: " + e));
    }

    // This method is called when the up button is pressed
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    // This method is used if you have an options menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Check if the correct item was clicked
        if (item.getItemId() == android.R.id.home) {
            // Handle the action when the up button is pressed
            return onSupportNavigateUp();
        }
        return super.onOptionsItemSelected(item);
    }
}
