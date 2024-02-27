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
    private EditText emailEditText, userNameEditText, passwordEditText;
    private Button signUpButton;
    private ProgressDialog progressDialog;

    private UserDB userDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_signup);


        // UserDB Instance
        userDB = new UserDB(this, UserSignUp.this, FirebaseFirestore.getInstance());

        // Initialize views
        initializeViews();

        // Set up click listeners
        backButton.setOnClickListener(view -> finish());

        signUpButton.setOnClickListener(view -> organizerSignUp());
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        emailEditText = findViewById(R.id.email);
        userNameEditText = findViewById(R.id.userName);
        passwordEditText = findViewById(R.id.password);
        progressDialog = new ProgressDialog(this);
        signUpButton = findViewById(R.id.signUpButton);
    }

    private void organizerSignUp() {

        // Retrieve user input
        String email = emailEditText.getText().toString().trim();
        String username = userNameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Check if email, username, or password is empty
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(UserSignUp.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        // TODO: Validate username and password with constraints

        // progress bar
        progressDialog.setMessage("Signing up...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        userDB.signUpUser(email, password, username, new UserDB.AuthCallback() {
            @Override
            public void onSuccess() {
                progressDialog.dismiss();
                Toast.makeText(UserSignUp.this, "Account Registration Successful. You can now login to the app.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(UserSignUp.this, UserLogin.class));
                finish();
            }

            @Override
            public void onFailure(String errorMessage) {
                // If sign in fails, display a message to the user.
                Toast.makeText(UserSignUp.this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }
}
