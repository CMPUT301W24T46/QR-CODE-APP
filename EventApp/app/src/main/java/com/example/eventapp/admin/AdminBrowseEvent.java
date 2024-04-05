package com.example.eventapp.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eventapp.R;
import com.example.eventapp.event.Event;
import com.example.eventapp.event.EventAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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

    String uid ;
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
                intent.putExtra("eventName", event.getEventName());
                intent.putExtra("imageURL", event.getImageURL());
                intent.putExtra("eventDate", event.getEventDate());
                intent.putExtra("eventDescription", event.getEventDescription());
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

        uid = FirebaseAuth.getInstance().getUid();

        if(uid != null){
            adminController.subscribeToEventDB(eventAdapter);
        }else{
            testBrowseEvent();
        }
    }

    /**
     * Sets up the search view with query listeners.
     */
    private void setUpSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(uid == null){
                    filterStaticEventList(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(TextUtils.isEmpty(newText)) {
                    if(uid!=null){
                        adminController.getCurrentEventList("", false, eventAdapter);
                    }
                } else {
                    if(uid!=null){
                        adminController.getCurrentEventList(newText, true, eventAdapter);
                    }
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

    private void testBrowseEvent(){
        ArrayList<Event> staticEvents = new ArrayList<>() ;
        staticEvents.add(new Event("TestEvent1", "eventDate", "imageURL", "eventDescription"));
        staticEvents.add(new Event("TestEvent2", "eventDate", "imageURL", "eventDescription"));
        eventAdapter.setFilter(staticEvents);
        eventAdapter.notifyDataSetChanged();
    }

    public void filterStaticEventList(String searchText){
        String eventStaticName ;
        Event staticEvent ;
        ArrayList<Event> searchResults = new ArrayList<>();
        for(int i = 0 ; i < eventDataList.size() ; i++){
            staticEvent = eventDataList.get(i) ;
            eventStaticName = eventDataList.get(i).getEventName() ;
            if (eventStaticName.toLowerCase().contains(searchText.toLowerCase())) {
                searchResults.add(new Event(staticEvent.getEventName(),  staticEvent.getEventDate() , staticEvent.getImageURL() , staticEvent.getEventId() ,
                        staticEvent.getEventDescription()));
            }
        }
        eventAdapter.setFilter(searchResults);
        eventAdapter.notifyDataSetChanged();
    }
}
