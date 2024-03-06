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
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminDeleteEvent extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ImageView bigEventImageView;
    private TextView eventNameView;
    private Button deleteEventButton;
    private String eventName, imageURL;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_delete_event);

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

        deleteEventButton.setOnClickListener(v -> deleteEvent());
    }

    private void deleteEvent() {
        // TODO: Delete based on id instead of name
        if (eventName != null && !eventName.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("Delete event")
                    .setMessage("Are you sure you want to delete this event?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        // Query to find the event with the matching name
                        db.collection("Events").whereEqualTo("Name", eventName)
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    if (!queryDocumentSnapshots.isEmpty()) {
                                        String documentId = queryDocumentSnapshots.getDocuments().get(0).getId();
                                        db.collection("Events").document(documentId)
                                                .delete()
                                                .addOnSuccessListener(aVoid -> {
                                                    Toast.makeText(AdminDeleteEvent.this, "Event deleted successfully", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                })
                                                .addOnFailureListener(e -> Toast.makeText(AdminDeleteEvent.this, "Error deleting event", Toast.LENGTH_SHORT).show());
                                    } else {
                                        Toast.makeText(AdminDeleteEvent.this, "No such event found", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(e -> Toast.makeText(AdminDeleteEvent.this, "Error finding event", Toast.LENGTH_SHORT).show());
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            Toast.makeText(this, "Error: Event name not found.", Toast.LENGTH_SHORT).show();
        }
    }


    // Handles back button press
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
