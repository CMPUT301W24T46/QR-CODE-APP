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


/**
 * AttendedEventInformationActivity is an {@link AppCompatActivity} that presents detailed information about an event
 * that an attendee is interested in or has already signed up for. It displays the event's name, description, date, and
 * an image associated with the event. This activity aims to provide attendees with all the necessary details about an
 * event in a concise and user-friendly format.
 *
 * <p>This activity is invoked with an intent containing the event's details passed as extras. It extracts this
 * information from the intent, sets up the UI components, and populates them with the provided event data.</p>
 *
 * <p>Key Features:</p>
 * <ul>
 *     <li>Displays a large, center-cropped image of the event at the top of the screen.</li>
 *     <li>Shows the event's name, description, and date below the image.</li>
 *     <li>Utilizes Glide for efficient and smooth loading of the event's image.</li>
 *     <li>Includes a back button in the ActionBar for easy navigation back to the previous screen.</li>
 * </ul>
 */

import com.example.eventapp.R;
import com.bumptech.glide.Glide;


public class AttendedEventInformationActivity extends AppCompatActivity {
    private TextView eventNameView;
    private TextView eventDateView;

    /**
     * Sets up the activity's UI by inflating the layout, initializing UI components, and populating them with event
     * details passed through the intent. It also configures the ActionBar with a title and back button for
     * navigation. If the intent contains the necessary event details, it displays them in the respective views.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this
     *                           Bundle contains the data most recently supplied in onSaveInstanceState(Bundle).
     *                           Otherwise, it is null. This bundle can be used to recreate the activity's state.
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
