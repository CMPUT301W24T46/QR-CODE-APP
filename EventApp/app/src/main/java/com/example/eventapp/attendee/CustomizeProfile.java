package com.example.eventapp.attendee;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventapp.R;

import java.util.Objects;

public class CustomizeProfile extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customize_attendee_profile);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Customize Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Navigate back to AttendeeAccount
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
