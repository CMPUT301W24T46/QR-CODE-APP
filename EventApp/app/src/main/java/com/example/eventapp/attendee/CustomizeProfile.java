package com.example.eventapp.attendee;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eventapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * The Activity after customize profile button is pressed
 */
public class CustomizeProfile extends AppCompatActivity {

    private EditText username;
    private EditText contact;
    private EditText description;
    private Button btnAttendeeSave;

    /**
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
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
        fetchDataFromFirestore();

        btnAttendeeSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });
    }

    /**
     * Fetches user data from Firestore and populates the EditText views with data.
     *
     */
    private void fetchDataFromFirestore() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String userId = user.getUid();

            DocumentReference userRef = FirebaseFirestore.getInstance().collection("Users").document(userId);
            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        // Retrieve data from Firestore document
                        String usernameText = documentSnapshot.getString("name");
                        String contactText = documentSnapshot.getString("contactInformation");
                        String descriptionText = documentSnapshot.getString("homepage");

                        // Set data to EditText views
                        username.setText(usernameText);
                        contact.setText(contactText);
                        description.setText(descriptionText);
                    } else {
                        Log.d("CustomizeProfile", "document doesn't exist");
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("CustomizeProfile", "Error getting document", e);
                }
            });
        }
    }

    /**
     *Handles the selection of menu items in the activity's options menu.
     * @param item The menu item that was selected.
     *
     * @return super.onOptionsItemSelected(item);
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Navigate back to AttendeeAccount
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Saves changes made to the user profile by updating the database with the new information.
     * Displays a toast message indicating whether the changes were saved successfully.
     */
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

    /**
     * Updates the user profile information in the Firestore database.
     *
     * @param username    The new username to be updated.
     * @param contact     The new contact information to be updated.
     * @param description The new description or homepage to be updated.
     *
     */
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
