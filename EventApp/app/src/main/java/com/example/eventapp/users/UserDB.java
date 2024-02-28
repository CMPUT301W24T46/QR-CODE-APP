package com.example.eventapp.users;

import android.app.ProgressDialog;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;

import android.content.Context;
import android.app.Activity;

import com.example.eventapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.HashMap;

public class UserDB {

    private String uid ;
    private User user ;
    private String typeOfUser ;
    private int navigationPageId;
    private NavController selectAccountController ;
    private final FirebaseAuth mAuth ;
    private final FirebaseFirestore dbQRApp ;
    private final CollectionReference userRef;
    private FirebaseUser currentUser ;
    private final Context context ;
    private final Activity activity ;

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

    public void  getUserInfoAttendee(){
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.isAnonymous()) {
            // User is signed in anonymously
            uid = currentUser.getUid();
            selectAccountController.navigate(navigationPageId);
            Log.d("AnonymousUser", "User already signed in anonymously with UID: " + uid);
        } else {
            // No user is signed in or the signed-in user is not anonymous
            anonymousLogin(context , activity);
        }
    }

    public void getUserInfoOrganizer(){

    }

    public void getUserInfoAdministrator(){

    }

    public void setNavController(NavController navController) {
        this.selectAccountController = navController;
    }

    public int getNavigationPageId() {
        return navigationPageId;
    }

    public void setNavigationPageId(int navigationPageId) {
        this.navigationPageId = navigationPageId;
    }

    private void anonymousLogin(Context context , Activity activity){
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
                            addUserInformation(uid);
                            selectAccountController.navigate(navigationPageId);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInAnonymously:failure", task.getException());
                            Toast.makeText(context, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void addUserInformation(String uid){
        Log.d("Write" , uid) ;
        HashMap<String, String> data = new HashMap<>();
        data.put("id", uid);
        data.put("name", "");
        data.put("homepage", "");
        data.put("typeOfUser" , typeOfUser) ;
        data.put("contactInformation", "");
        user = new User(uid , "" , "" , "" , null , typeOfUser) ;

        userRef.document(uid)
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "DocumentSnapshot successfully written!");
                    }});

    }


    public void updateUserInformation(String Username, String Contact, String Homepage) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            HashMap<String, Object> data = new HashMap<>();
            data.put("Username", Username);
            data.put("Contact", Contact);
            data.put("Homepage", Homepage);
            userRef.document(uid)
                    .update(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("Firestore", "User info updated");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception error) {
                            Log.e("Firestore", "Error", error);
                        }
                    });
        }
    }

    public interface AuthCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    // ORGANIZER SIGN UP
    public void signUpUser(String email, String password, String userName, AuthCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String uid = firebaseUser.getUid();
                            HashMap<String, Object> userData = new HashMap<>();
                            userData.put("email", email);
                            userData.put("username", userName);
                            userData.put("id", uid);
                            userData.put("name", "");
                            userData.put("homepage", "");
                            userData.put("typeOfUser" , "organizer") ;
                            userData.put("contactInformation", "");

                            // set user data in Users collection
                            addOrganizerInfo(uid, userData, callback);
                        }
                    } else {
                        Log.w("UserSignUp", "Failed to register user", task.getException());
                        callback.onFailure("Authentication Failed");
                    }
                });
    }

    private void addOrganizerInfo(String uid, HashMap<String, Object> userData, AuthCallback callback) {
        userRef.document(uid).set(userData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("UserSignUp", "User details successfully added!");
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.w("UserSignUp", "Error writing document", e);
                    callback.onFailure("Failed to add user details to database");
                });
    }

    // ORGANIZER LOGIN
    public void organizerLogin(String username, String password, AuthCallback callback) {
        // find email based on username
        userRef
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        String email = document.getString("email");

                        // Authenticate email and password credentials
                        signInWithEmail(email, password, callback);
                    } else {
                        // Failed to find a user with the given username
                        Log.w("TAG", "Failed to find user by username", task.getException());
                        callback.onFailure("Failed to find user by username");
                    }
                });
    }

    private void signInWithEmail(String email, String password, AuthCallback callback) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // inform user and redirect after a successful attempt
                        Log.d("TAG", "signInWithEmail:success");
                        callback.onSuccess();
                    } else {
                        // Invalid credentials
                        Log.w("TAG", "Sign In Filaed", task.getException());
                        callback.onFailure("Authentication failed.");
                    }
                });
    }

}

