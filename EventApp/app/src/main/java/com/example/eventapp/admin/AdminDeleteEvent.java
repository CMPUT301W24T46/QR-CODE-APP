package com.example.eventapp.admin;

import android.app.Activity;
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
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * {@link androidx.appcompat.app.AppCompatActivity} .
 * An activity that provides the functionality for an admin
 * to view and delete specific events.
 */
public class AdminDeleteEvent extends AppCompatActivity {
    // TODO: Delete based on a unique identifier instead of event name

    private AdminController adminController;

    private ImageView bigEventImageView;
    private TextView eventNameView;
    private Button deleteEventButton;
    private String eventName, imageURL;


    /**
     * Called when the activity is starting.
     * Sets up the UI components and event deletion functionalities.
     *
     * @param savedInstanceState a previously saved state
     */
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

        deleteEventButton.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Delete event")
                .setMessage("Are you sure you want to delete this event?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    adminController.deleteEvent(eventName)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Event deleted successfully", Toast.LENGTH_SHORT).show();
                                this.finish();
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "Error deleting event", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show());
    }




    /**
     * Handles the action when the up button is pressed to navigate back.
     *
     * @return boolean Return true to indicate that the action has been handled.
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
