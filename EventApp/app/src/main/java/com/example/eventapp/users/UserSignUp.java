package com.example.eventapp.users;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.eventapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class UserSignUp extends AppCompatActivity {

    private ImageView backButton;
    private EditText emailEditText;
    private EditText userNameEditText;
    private EditText passwordEditText;
    private Button signUpButton;
    private ProgressDialog progressDialog;


    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_signup);


        // Firebase auth instance
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize views
        backButton = findViewById(R.id.backButton);
        emailEditText = findViewById(R.id.email);
        userNameEditText = findViewById(R.id.userName);
        passwordEditText = findViewById(R.id.password);
        signUpButton = findViewById(R.id.signUpButton);

        // Set up click listeners
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle back button click
                finish(); // Close this activity
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle sign up button click
                organizerSignUp();
            }
        });
    }

    private void organizerSignUp() {
        // progress bar
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing up...");
        progressDialog.setCancelable(false);


        // Retrieve user input
        String email = emailEditText.getText().toString().trim();
        String userName = userNameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();


        // Check if email, username, or password is empty
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
            Toast.makeText(UserSignUp.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Validate username and password with constraints

        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in is successful
                            Log.d("TAG", "createUserWithEmail:success");
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();

                            // create new organizer in UserDB
                            if (firebaseUser != null) {
                                String uid = firebaseUser.getUid();
                                Organizer organizer = new Organizer(uid, "", userName, email, "", "", null, "organizer");

                                HashMap<String, Object> userData = new HashMap<>();
                                userData.put("email", email);
                                userData.put("username", userName);
                                userData.put("id", organizer.getId());
                                userData.put("name", organizer.getName());
                                userData.put("homepage", "");
                                userData.put("typeOfUser" , "organizer") ;
                                userData.put("contactInformation", "");
                                addOrganizerInfo(uid, userData, UserSignUp.this);
                            }


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(UserSignUp.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });

    }

    private void addOrganizerInfo(String uid, HashMap<String, Object> userData, Context context) {
        // Add organizer information to database
        db.collection("Users").document(uid)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("UserSignUp", "User details successfully added!");
                    progressDialog.dismiss();
                    Toast.makeText(UserSignUp.this, "Account Registration Successful. You can now login to the app.",
                            Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(UserSignUp.this, UserLogin.class);
                    startActivity(intent);
                    finish();

                })
                .addOnFailureListener(e -> {
                    Log.w("UserSignUp", "Error writing document", e);
                    Toast.makeText(context, "Failed to add user details.", Toast.LENGTH_SHORT).show();
                });
    }
}
