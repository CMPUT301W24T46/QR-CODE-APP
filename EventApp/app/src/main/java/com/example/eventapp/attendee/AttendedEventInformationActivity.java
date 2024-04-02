package com.example.eventapp.attendee;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.example.eventapp.R;


public class AttendedEventInformationActivity extends AppCompatActivity {

    private ImageView bigEventImageView;
    private TextView eventNameView;
    private TextView eventDescriptionView;
    private TextView eventDateView;
    private TextView alreadySignedUpTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_attendee_event_information);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Event Information");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Bundle args = getIntent().getExtras();
        if (args != null) {
            // Extract information from the bundle
            String eventName = args.getString("eventName");
            String URL = args.getString("imageURL");
            String eventDate = args.getString("eventDate");
            String eventDescription = args.getString("eventDescription");

            eventNameView = findViewById(R.id.eventTitleDescrip);
            bigEventImageView = findViewById(R.id.biggerEventImage);
            eventDescriptionView = findViewById(R.id.eventFullDescription);
            eventDateView = findViewById(R.id.attendee_event_date_time);

            eventNameView.setText(eventName);
            eventDateView.setText(eventDate);
            eventDescriptionView.setText(eventDescription);

            Glide.with(this).load(URL).centerCrop().into(bigEventImageView);
        }

        alreadySignedUpTextView = findViewById(R.id.alreadySigneUpTextView);
        alreadySignedUpTextView.setVisibility(View.GONE);
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
