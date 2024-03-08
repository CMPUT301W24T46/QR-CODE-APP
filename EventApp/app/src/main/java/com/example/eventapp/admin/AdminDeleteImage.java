package com.example.eventapp.admin;

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

public class AdminDeleteImage extends AppCompatActivity {

    private AdminController adminController;
    private TextView imageTextView;
    private ImageView imageView;
    private Button deleteImageButton;
    private String imageId, imageURL;


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

        imageView = findViewById(R.id.imageItem);
        imageTextView = findViewById(R.id.imageID);
        deleteImageButton = findViewById(R.id.btnDeleteImage);

        if (imageURL != null) {
            Glide.with(this).load(imageURL).centerCrop().into(imageView);
        } else {
            Toast.makeText(this, "Event data not available", Toast.LENGTH_LONG).show();
        }

        imageTextView.setText(imageId);
        deleteImageButton.setOnClickListener(v -> adminController.deleteImage(imageURL));
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}

