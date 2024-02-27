package com.example.eventapp.users;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.eventapp.R;

public class UserLogin extends AppCompatActivity {

    private EditText userName;
    private EditText password;
    private Button signUpButton;
    private TextView forgotPassword;
    private TextView redirectToSignUp;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login); // Make sure the layout name matches your XML file name

        // Initialize views
        userName = findViewById(R.id.userName);
        password = findViewById(R.id.password);
        signUpButton = findViewById(R.id.signUpButton);
        forgotPassword = findViewById(R.id.forgotPassword);
        redirectToSignUp = findViewById(R.id.redirectToSignUp);
        backButton = findViewById(R.id.backButton);

        // Set click listeners
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement your login logic here
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
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                // Redirect to UserSignUp Activity
                Intent intent = new Intent(UserLogin.this, UserSignUp.class);
                startActivity(intent);
            }

            // style the clickable span
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


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
