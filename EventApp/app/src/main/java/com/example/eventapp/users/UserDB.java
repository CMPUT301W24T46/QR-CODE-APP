package com.example.eventapp.users;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;

import android.content.Context;
import android.app.Activity;

import com.example.eventapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

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


}