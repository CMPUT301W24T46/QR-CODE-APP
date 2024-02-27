package com.example.eventapp.users;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventapp.R;

public class UserSignUp extends AppCompatActivity {

    private ImageView backButton;
    private EditText fullNameEditText;
    private EditText userNameEditText;
    private EditText passwordEditText;
    private Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_signup);

        // Initialize views
        backButton = findViewById(R.id.backButton);
        fullNameEditText = findViewById(R.id.fullName);
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
                signUp();
            }
        });
    }

    private void signUp() {
        // Extract user inputs
        String fullName = fullNameEditText.getText().toString().trim();
        String userName = userNameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // TODO: Add your sign-up logic here (e.g., validation, Firebase authentication)
    }
}
