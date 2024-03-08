package com.example.eventapp.admin;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.eventapp.R;
import com.example.eventapp.users.Admin;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.concurrent.CancellationException;

public class AdminDeleteProfile extends AppCompatActivity {

    private AdminController adminController;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_delete_profile);

        adminController = new AdminController(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("View/Delete Profile");
        }

        ImageView profileImageView = findViewById(R.id.profileImageView);
        TextView tvUserId = findViewById(R.id.tvUserId);
        TextView tvUserName = findViewById(R.id.tvUserName);
        TextView tvUserContact = findViewById(R.id.tvUserContact);
        TextView tvUserHomepage = findViewById(R.id.tvUserHomepage);
        TextView tvUserType = findViewById(R.id.tvUserType);
        Button btnDeleteUser = findViewById(R.id.btnDeleteUser);

        // Try-catch block to handle a potential NullPointerException
        try {
            HashMap<String, String> userData = (HashMap<String, String>) getIntent().getSerializableExtra("userData");
            if (userData != null) {
                userId = userData.get("id");
                String imageURL = userData.get("imageURL");

                if (imageURL != null && !imageURL.isEmpty()) {
                    Glide.with(this).load(imageURL).into(profileImageView);
                } else {
                    // Set a default image or placeholder
                    profileImageView.setImageResource(R.drawable.ic_home);
                }

                tvUserId.setText("User ID: " + userId);
                tvUserName.setText("Name: " + userData.get("name"));
                tvUserContact.setText("Contact: " + userData.get("contact"));
                tvUserHomepage.setText("Homepage: " + userData.get("homepage"));
                tvUserType.setText("User Role: " + userData.get("typeOfUser"));

            }
        } catch (ClassCastException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error retrieving user data.", Toast.LENGTH_SHORT).show();
        }

        btnDeleteUser.setOnClickListener(v -> {

            if (userId != null && !userId.isEmpty()) {
                // Use runOnUiThread to ensure AlertDialog is created on the main UI thread
                this.runOnUiThread(() -> {
                    new AlertDialog.Builder(this)
                            .setTitle("Delete User")
                            .setMessage("Are you sure you want to delete this user?")
                            .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                // Call deleteUserDirectly method for actual deletion
                                adminController.deleteUser(userId)
                                        .addOnSuccessListener(aVoid ->{
                                            this.finish();
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(this, "Error finding user", Toast.LENGTH_SHORT).show());
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                });
            } else {
                Toast.makeText(this, "User Id not found", Toast.LENGTH_SHORT).show();
            }

        });

    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
