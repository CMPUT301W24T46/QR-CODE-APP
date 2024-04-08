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

public class AttendedEventInformationActivity extends AppCompatActivity {
    private TextView eventNameView;
    private TextView eventDateView;

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
