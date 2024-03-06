package com.example.eventapp.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.example.eventapp.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class AdminDeleteProfile extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_delete_profile);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("View/Delete Profile");
        }

        TextView tvUserId = findViewById(R.id.tvUserId);
        TextView tvUserName = findViewById(R.id.tvUserName);
        Button btnDeleteUser = findViewById(R.id.btnDeleteUser);

        // Try-catch block to handle a potential NullPointerException
        try {
            HashMap<String, String> userData = (HashMap<String, String>) getIntent().getSerializableExtra("userData");

            if (userData != null) {
                userId = userData.get("id");
                tvUserId.setText(userId);
                tvUserName.setText(userData.get("name"));
                // ... set text for other TextViews
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
            // Handle the error appropriately
        }

        btnDeleteUser.setOnClickListener(v -> deleteUser());

    }

    private void deleteUser() {
        if (userId != null && !userId.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("Delete User")
                    .setMessage("Are you sure you want to delete this user?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        // Continue with delete operation
                        db.collection("Users").document(userId)
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "User deleted successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Error deleting user", Toast.LENGTH_SHORT).show());
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            Toast.makeText(this, "Error: User ID not found.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
