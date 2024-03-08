package com.example.eventapp.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.eventapp.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminDeleteEvent extends AppCompatActivity {
    private AdminController adminController;

    private ImageView bigEventImageView;
    private TextView eventNameView;
    private Button deleteEventButton;
    private String eventName, imageURL;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_delete_event);

        adminController = new AdminController(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("View/Delete Event");
            actionBar.setDisplayHomeAsUpEnabled(true); // Enable the back button
        }

        // Extract information from the intent
        eventName = getIntent().getStringExtra("EventName");
        imageURL = getIntent().getStringExtra("ImageURL");

        eventNameView = findViewById(R.id.eventTitleDescrip);
        bigEventImageView = findViewById(R.id.biggerEventImage);
        deleteEventButton = findViewById(R.id.btnDeleteEvent);

        if (eventName != null && imageURL != null) {
            eventNameView.setText(eventName);
            Glide.with(this).load(imageURL).centerCrop().into(bigEventImageView);
        } else {
            Toast.makeText(this, "Event data not available", Toast.LENGTH_LONG).show();
        }

        deleteEventButton.setOnClickListener(v -> adminController.deleteEvent(eventName));
    }




    // Handles back button press
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
