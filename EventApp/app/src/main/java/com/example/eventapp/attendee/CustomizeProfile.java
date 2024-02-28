package com.example.eventapp.attendee;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CustomizeProfile extends AppCompatActivity {

    private EditText username;
    private EditText contact;
    private EditText description;
    private Button btnAttendeeSave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customize_attendee_profile);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Customize Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        username = findViewById(R.id.editTextTextEmailAddress);
        contact = findViewById(R.id.editTextPhone);
        description = findViewById(R.id.editTextTextMultiLine);
        btnAttendeeSave = findViewById(R.id.AttendeeAccountSave);

        btnAttendeeSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });
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
    private void saveChanges() {
        String usernameText = username.getText().toString().trim();
        String contactText = contact.getText().toString().trim();
        String descriptionText = description.getText().toString().trim();

        if (usernameText.isEmpty() || contactText.isEmpty() || descriptionText.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        updateProfileInDatabase(usernameText, contactText, descriptionText);
        Toast.makeText(this, "Changes saved", Toast.LENGTH_SHORT).show();
    }
    private void updateProfileInDatabase(String username, String contact, String description) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String userId = user.getUid();

            DocumentReference userRef = db.collection("Users").document(userId);

            Map<String, Object> updates = new HashMap<>();
            updates.put("name", username);
            updates.put("contactInformation", contact);
            updates.put("homepage", description);

            userRef.update(updates);
        }
    }

    public void onCustomizeProfileSaveClicked(View view) {
        // Implementation for saving profile changes
    }

    public void onCustomizeProfileCustomizeImageClicked(View view) {
        // Implementation for customizing image
    }

    public void onCustomizeProfileDeleteImageClicked(View view) {
        // Implementation for deleting image
    }

}
