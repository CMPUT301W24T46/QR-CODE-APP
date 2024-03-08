package com.example.eventapp.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eventapp.R;
import com.example.eventapp.event.Event;
import com.example.eventapp.event.EventAdapter;

import java.util.ArrayList;

/**
 *  {@link androidx.appcompat.app.AppCompatActivity} .
 * AdminBrowseEvent is an AppCompatActivity that allows admins to browse through events.
 * It includes search functionality and options to perform administrative actions on the events.
 */
public class AdminBrowseEvent extends AppCompatActivity {
    private AdminController adminController;
    private SearchView searchView;
    private ArrayList<Event> eventDataList;
    private ListView eventList;
    private EventAdapter eventAdapter;

    /**
     * Called when the activity is starting
     *
     * @param savedInstanceState A previously saved state if the application has been run before
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_attendee_event);

        adminController = new AdminController(this);

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

        eventAdapter = new EventAdapter(this, eventDataList, eventClickListener) ;
        eventList.setAdapter(eventAdapter);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Browse Events");
            actionBar.setDisplayHomeAsUpEnabled(true); // Enable the back button
        }

        setUpSearchView();
        adminController.subscribeToEventDB(eventAdapter);
    }

    /**
     * Sets up the search view with query listeners.
     */
    private void setUpSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(TextUtils.isEmpty(newText)) {
                    adminController.getCurrentEventList("", false, eventAdapter);
                } else {
                    adminController.getCurrentEventList(newText, true, eventAdapter);
                }
                return false;
            }
        });
    }


    /**
     * This method is called when the up button is pressed.
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    /**
     * Called whenever an item in your options menu is selected.
     *
     * @param item The menu item that was selected.
     * @return Return false to allow normal menu processing, true to consume it here.
     */
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
