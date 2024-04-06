package com.example.eventapp.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.eventapp.R;

/**
 * {@link androidx.appcompat.app.AppCompatActivity} .
 * AdminDeleteImage is an AppCompatActivity that provides the functionality for an admin
 * to view and delete specific images.
 */
public class AdminDeleteImage extends AppCompatActivity {

    private AdminController adminController;
    private TextView imageTextView;
    private ImageView imageView;
    private Button deleteImageButton;
    private String imageId, imageURL, selectedFilter;


    /**
     * Called when the activity is starting.
     * Sets up the UI components and image deletion functionalities.
     *
     * @param savedInstanceState a previously saved state
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_delete_image);

        adminController = new AdminController(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("View/Delete Image");
            actionBar.setDisplayHomeAsUpEnabled(true); // Enable the back button
        }

        // Extract information from the intent
        imageURL = getIntent().getStringExtra("ImageURL");
        imageId = getIntent().getStringExtra("ImageID");
        selectedFilter = getIntent().getStringExtra("selectedFilter");

        imageView = findViewById(R.id.imageItem);
        imageTextView = findViewById(R.id.imageID);
        deleteImageButton = findViewById(R.id.btnDeleteImage);

        if (imageURL != null) {
            Glide.with(this).load(imageURL).centerCrop().into(imageView);
        } else {
            Toast.makeText(this, "Event data not available", Toast.LENGTH_LONG).show();
        }

        imageTextView.setText(imageId);
        deleteImageButton.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Delete image")
                .setMessage("Are you sure you want to delete this image?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    adminController.deleteImage(imageId, selectedFilter)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Image deleted successfully", Toast.LENGTH_SHORT).show();
                                this.finish();
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "Error deleting image", Toast.LENGTH_SHORT).show());
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
        onBackPressed();
        return true;
    }

}

