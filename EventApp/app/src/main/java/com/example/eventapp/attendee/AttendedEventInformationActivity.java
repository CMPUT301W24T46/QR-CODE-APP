package com.example.eventapp.attendee;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eventapp.R;
import com.bumptech.glide.Glide;

/**
 * An {@link AppCompatActivity} that presents detailed information about an event the user has attended.
 * It displays the event name and date, and offers a back navigation option in the action bar.
 * This activity is intended to be used within the context of an event application where users can view a list of events they have attended.
 * It provides a focused view for a single event, showing the event's key details that were passed to it through an intent.
 * Layout File: R.layout.attended_event_list_information
 */

public class AttendedEventInformationActivity extends AppCompatActivity {

    /**
     * TextView for displaying the name of the event.
     */

    private TextView eventNameView;

    /**
     * TextView for displaying the date and time of the event.
     */
    private TextView eventDateView;

    /**
     * Called when the activity is starting. This is where most initialization should go:
     * calling setContentView(int) to inflate the activity's UI, using findViewById(int) to programmatically interact with widgets in the UI,
     * extracting extras from the intent, and populating the UI with the event details.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle). Otherwise, it is null. This value may be null.
     */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attended_event_list_information);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Event Information");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Bundle args = getIntent().getExtras();
        if (args != null) {
            // Extract information from the bundle
            String eventName = args.getString("eventName");
            String eventDate = args.getString("eventDate");

            eventNameView = findViewById(R.id.eventTitleDescrip);
            eventDateView = findViewById(R.id.attendee_event_date_time);

            eventNameView.setText(eventName);
            eventDateView.setText(eventDate);

        }

    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * The default implementation simply returns false to have the normal processing happen
     * (calling the item's Runnable or sending a message to its Handler as appropriate).
     * You can use this method for any items for which you would like to do processing without
     * those other facilities.
     *
     * Derived classes should call through to the base class for it to perform the default menu handling.
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to proceed, true to consume it here.
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
