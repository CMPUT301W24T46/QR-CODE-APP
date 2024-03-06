package com.example.eventapp.admin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.eventapp.Image.Image;
import com.example.eventapp.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class AdminDeleteImage extends AppCompatActivity {
    private FirebaseFirestore db;
    private CollectionReference imageRef;

    private TextView imageTextView;
    private ImageView imageView;
    private Button deleteImageButton;
    private String imageId, imageURL;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_delete_image);

        db = FirebaseFirestore.getInstance();
        imageRef = db.collection("Image");


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("View/Delete Image");
            actionBar.setDisplayHomeAsUpEnabled(true); // Enable the back button
        }

        // Extract information from the intent
        imageURL = getIntent().getStringExtra("ImageURL");
        imageId = getIntent().getStringExtra("ImageID");

        imageView = findViewById(R.id.imageItem);
        imageTextView = findViewById(R.id.imageID);
        deleteImageButton = findViewById(R.id.btnDeleteImage);

        if (imageURL != null) {
            Glide.with(this).load(imageURL).centerCrop().into(imageView);
        } else {
            Toast.makeText(this, "Event data not available", Toast.LENGTH_LONG).show();
        }

        imageTextView.setText(imageId);
        deleteImageButton.setOnClickListener(v -> deleteImage(imageURL));
    }


    private void deleteImage(String imageURL) {
        if (imageURL != null && !imageURL.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Image")
                    .setMessage("Are you sure you want to delete this image?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        // Query for the image by URL and delete it
                        imageRef
                                .whereEqualTo("URL", imageURL)
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                        imageRef.document(document.getId())
                                                .delete()
                                                .addOnSuccessListener(aVoid -> {
                                                    Toast.makeText(AdminDeleteImage.this, "Image deleted successfully", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                })
                                                .addOnFailureListener(e -> Toast.makeText(AdminDeleteImage.this, "Error deleting image", Toast.LENGTH_SHORT).show());
                                    }
                                })
                                .addOnFailureListener(e -> Toast.makeText(AdminDeleteImage.this, "Error finding image", Toast.LENGTH_SHORT).show());
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            Toast.makeText(this, "Error: Image URL not found.", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}

