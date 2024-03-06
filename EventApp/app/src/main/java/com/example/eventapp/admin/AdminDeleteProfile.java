package com.example.eventapp.admin;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.example.eventapp.R;
import java.util.HashMap;

public class AdminViewProfileInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_edit_profile);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("View/Delete Profile");
        }

        TextView tvUserId = findViewById(R.id.tvUserId);
        TextView tvUserName = findViewById(R.id.tvUserName);

        // Try-catch block to handle a potential NullPointerException
        try {
            HashMap<String, String> userData = (HashMap<String, String>) getIntent().getSerializableExtra("userData");

            if (userData != null) {
                tvUserId.setText(userData.get("id"));
                tvUserName.setText(userData.get("name"));
                // ... set text for other TextViews
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
            // Handle the error appropriately
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
