package com.example.eventapp.users;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;

import android.content.Context;
import android.app.Activity;

import com.example.eventapp.R;
import com.example.eventapp.attendee.AttendeeActivity;
import com.example.eventapp.document_reference.DocumentReferenceChecker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is responsible for managing user data in Firestore and authentication
 * via FirebaseAuth. It includes methods for anonymous user authentication,
 * updating user profiles, and adding new user information to the database.
 */
public class UserDB {

    private String uid ;
    private String typeOfUser ;
    private final FirebaseAuth mAuth ;
    private final FirebaseFirestore dbQRApp ;
    private final CollectionReference userRef;
    private FirebaseUser currentUser ;
    private final Context context ;
    private final Activity activity ;

    /**
     * Defines the callback interface for user authentication.
     * This interface should be implemented by class that handles navigation based
     * on authentication result
     */
    public interface AuthCallbackAttendee {
        void onSuccess();
    }

    /**
     * Constructor for the UserDB class. Initializes the FirebaseAuth, FirebaseFirestore,
     * and Firestore settings for offline support.
     *
     * @param context The context from which this class is invoked.
     * @param activity The activity from which this class is invoked.
     * @param dbQRApp The FirebaseFirestore instance for database operations.
     */
    public UserDB(Context context , Activity activity , FirebaseFirestore dbQRApp) {
        mAuth = FirebaseAuth.getInstance();
        this.dbQRApp = dbQRApp ;
        this.userRef = dbQRApp.collection("Users") ;
        this.context = context ;
        this.activity = activity ;

        // Enable offline support
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        dbQRApp.setFirestoreSettings(settings);
    }

    /**
     * Retrieves user information for attendees. If the current user is signed in anonymously,
     * it updates the profile in the database. Otherwise, it initiates anonymous login.
     *
     * @param authCallback The callback interface to handle authentication events.
     */
    public void  getUserInfoAttendee(AuthCallbackAttendee authCallback){
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.isAnonymous()) {
            // User is signed in anonymously
            uid = currentUser.getUid();
            updateProfileInDatabase();
            authCallback.onSuccess();
        } else {
            // No user is signed in or the signed-in user is not anonymous
            anonymousLogin(context , activity, authCallback);
        }
    }


    /**
     * Handles anonymous user login using FirebaseAuth. Upon success, it adds user information
     * to the database.
     *
     * @param context The context from which this method is invoked.
     * @param activity The activity from which this method is invoked.
     * @param authCallback The callback interface to handle authentication events.
     */
    private void anonymousLogin(Context context , Activity activity ,AuthCallbackAttendee authCallback){
        mAuth.signInAnonymously()
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInAnonymously:success");
                            FirebaseUser user = task.getResult().getUser();
                            // You can update UI or perform other actions here
                            String uid = user.getUid() ;
                            addUserInformation(uid , authCallback);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInAnonymously:failure", task.getException());
                            Toast.makeText(context, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Adds user information to the Firestore database. This method is called after
     * successful anonymous authentication.
     *
     * @param uid The unique identifier for the user.
     * @param authCallback The callback interface to handle post-addition events.
     */
    private void addUserInformation(String uid , AuthCallbackAttendee authCallback){
        HashMap<String, Object> data = new HashMap<>();
        DocumentReferenceChecker documentReferenceChecker = new DocumentReferenceChecker() ;

        data.put("id", uid);
        data.put("name", "");
        data.put("homepage", "");
        data.put("typeOfUser" , typeOfUser) ;
        data.put("contactInformation", "");
        data.put("imageUrl", documentReferenceChecker.documentReferenceWrite());

        userRef.document(uid)
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        authCallback.onSuccess();
                        Log.d("Firestore", "DocumentSnapshot successfully written!");
                    }});

    }

    /**
     * Updates the type of user in the Firestore database for the current user.
     * This method is typically called after a successful login or user type change.
     */
    private void updateProfileInDatabase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String userId = user.getUid();

            DocumentReference userRef = db.collection("Users").document(userId);

            Map<String, Object> updates = new HashMap<>();
            updates.put("typeOfUser", typeOfUser);
            userRef.update(updates);
        }
    }

    /**
     * Sets the type of user.
     * @param typeOfUser The type of user (e.g., "Attendee", "Organizer", "Administrator").
     */
    public void setTypeOfUser(String typeOfUser) {
        this.typeOfUser = typeOfUser;
    }

    public String getCurrentUserId() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();
        } else {
            return null; // or handle the case when user is not signed in
        }
    }

}

