package com.example.eventapp.firestoreservice;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class UserProfileService {

    private final CollectionReference userRef ;
    private String userId ;

    private String name ;

    private String contactInformation ;

    private String description ;

    public UserProfileService(CollectionReference userRef , String name ,String userId , String contactInformation , String description){
        this.userRef = userRef ;
        this.userId = userId ;
        this.contactInformation = contactInformation ;
        this.description = description ;
        this.name = name ;
    }

//  Get Username from FireStor
    public void getUserInfo(UserNameCallback callback){
        userRef.document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    // Assuming the user's name is stored under a field called "name"
                    String name = document.getString("name");
                    callback.onCallback(name);
                } else {
                    // Document does not exist
                    callback.onError(new Exception("Document does not exist"));
                }
            } else {
                // Task failed with an exception
                callback.onError(task.getException());
            }
        });
    }
    
    public void updateUserInfo(UserNameCallback callback){
        Map<String , Object > updateFields = new HashMap<>();
        updateFields.put("name" , name) ;
        updateFields.put("homepage" , description) ;
        updateFields.put("contactInformation" , contactInformation) ;
        userRef.document(userId).update(updateFields).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onSuccessfulUpdate(true);
            } else {
                // Task failed with an exception
                callback.onError(task.getException());
            }
        });
    }
}
