package com.example.eventapp.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eventapp.Image.Image;
import com.example.eventapp.Image.ImageGridAdapter;
import com.example.eventapp.R;
import com.example.eventapp.event.Event;
import com.example.eventapp.users.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminBrowseImage extends AppCompatActivity {
    private FirebaseFirestore db;
    private CollectionReference imageRef;

    private GridView imageGridView;
    private List<Image> imageItems;
    private ImageGridAdapter adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_browse_image);

        // TODO: Add search functionality (optional)

        db = FirebaseFirestore.getInstance();
        imageRef = db.collection("Image");

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int columnWidth = screenWidth / 2;

        imageGridView = findViewById(R.id.imageGridView);
        imageGridView.setColumnWidth(columnWidth);

        imageItems = new ArrayList<>();

        adapter = new ImageGridAdapter(this, imageItems, columnWidth);
        imageGridView.setAdapter(adapter);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Browse Images");
            actionBar.setDisplayHomeAsUpEnabled(true); // Enable the back button
        }

        subscribeToFirestore();

        // Implement item click listener if needed
        imageGridView.setOnItemClickListener((parent, view, position, id) -> {
            Image selectedItem = imageItems.get(position);

            Intent intent = new Intent(AdminBrowseImage.this, AdminDeleteImage.class);
            intent.putExtra("ImageID", selectedItem.getId());
            intent.putExtra("ImageURL", selectedItem.getURL());
            startActivity(intent);

        });


    }

    private void subscribeToFirestore() {
        imageRef.addSnapshotListener((querySnapshots, error) -> {
            if (error != null) {
                Log.e("Firestore", error.toString());
                return;
            }
            if (querySnapshots != null) {
                imageItems.clear();
                for (QueryDocumentSnapshot doc : querySnapshots) {
                    String imageId = doc.getId();
                    String imageURL = doc.getString("URL");
                    Log.d("Firestore", String.format("Name(%s, %s) fetched", imageId, imageURL));
                    imageItems.add(new Image(imageURL, imageId));
                }
                adapter.notifyDataSetChanged();
            }
        });
    }


    // This method is called when the up button is pressed
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    // This method is used if you have an options menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Check if the correct item was clicked
        if (item.getItemId() == android.R.id.home) {
            // Handle the action when the up button is pressed
            return onSupportNavigateUp();
        }
        return super.onOptionsItemSelected(item);
    }
}