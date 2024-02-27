package com.example.eventapp.users;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.eventapp.R;
import com.example.eventapp.organizer.OrganizerAccount;
import com.example.eventapp.organizer.OrganizerActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nonnull;

public class UserLogin extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView forgotPassword;
    private TextView redirectToSignUp;
    private ImageView backButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        // Initialize views
        usernameEditText = findViewById(R.id.loginUsername);
        passwordEditText = findViewById(R.id.loginPassword);
        loginButton = findViewById(R.id.loginButton);
        forgotPassword = findViewById(R.id.forgotPassword);
        redirectToSignUp = findViewById(R.id.redirectToSignUp);
        backButton = findViewById(R.id.loginBackButton);

        // Set click listeners
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                organizerLogin();
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Display a dialog to reset password
            }
        });

        String redirectText = getString(R.string.redirect_to_signup);
        SpannableString ss = new SpannableString(redirectText);
        // Clickable span for SignIn
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                // Redirect to UserSignUp Activity
                Intent intent = new Intent(UserLogin.this, UserSignUp.class);
                startActivity(intent);
            }

            // style the clickable text
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(ContextCompat.getColor(UserLogin.this, R.color.white));
                ds.setUnderlineText(true);
            }
        };

        // specify clickable portion of text
        int start = redirectText.indexOf("Sign Up");
        int end = start + "Sign Up".length();
        ss.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        redirectToSignUp.setText(ss);
        redirectToSignUp.setMovementMethod(LinkMovementMethod.getInstance());


        // handles back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void organizerLogin() {
        // progress bar
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging In...");
        progressDialog.setCancelable(false);

        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // check if username or password is empty
        if ( TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(UserLogin.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        // find email based on username
        db.collection("Users")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        String email = document.getString("email");

                        // Authenticate email and password credentials
                        signInWithEmail(email, password);
                    } else {
                        // Failed to find a user with the given username
                        progressDialog.dismiss();
                        Log.w("TAG", "Failed to find user by username", task.getException());
                        Toast.makeText(UserLogin.this, "Failed to find user by username", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void signInWithEmail(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        // inform and redirect user after a successful attempt
                        Log.d("TAG", "signInWithEmail:success");
                        Toast.makeText(UserLogin.this, "Login Successful. Redirecting...", Toast.LENGTH_SHORT).show();
                        navigateToOrganizer();
                    } else {
                        // Invalid credentials
                        Log.w("TAG", "Sign In Filaed", task.getException());
                        Toast.makeText(UserLogin.this, "Wrong username or password", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void navigateToOrganizer() {
        // Redirect to OrganizerActivity
        Intent intent = new Intent(UserLogin.this, OrganizerActivity.class);
        startActivity(intent);
        finish();
    }

}
