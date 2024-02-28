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

    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private TextView forgotPassword, redirectToSignUp;
    private ImageView backButton;
    private UserDB userDB;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        // Firebase
        userDB = new UserDB(this, UserLogin.this, FirebaseFirestore.getInstance());


        // Initialize views
        InitializeViews();

        // Set click listeners
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
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

    private void InitializeViews() {
        usernameEditText = findViewById(R.id.loginUsername);
        passwordEditText = findViewById(R.id.loginPassword);
        loginButton = findViewById(R.id.loginButton);
        forgotPassword = findViewById(R.id.forgotPassword);
        redirectToSignUp = findViewById(R.id.redirectToSignUp);
        backButton = findViewById(R.id.loginBackButton);
        progressDialog = new ProgressDialog(this);
    }

    private void userLogin() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // check if username or password is empty
        if ( TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(UserLogin.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // progress bar
        progressDialog.setMessage("Logging In...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        userDB.organizerLogin(username, password, new UserDB.AuthCallback() {
            @Override
            public void onSuccess() {
                progressDialog.dismiss();
                Toast.makeText(UserLogin.this, "Login Successful. Redirecting...", Toast.LENGTH_SHORT).show();
                navigateToOrganizer();
            }

            @Override
            public void onFailure(String errorMessage) {
                progressDialog.dismiss();
                Toast.makeText(UserLogin.this, errorMessage, Toast.LENGTH_SHORT).show();
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
