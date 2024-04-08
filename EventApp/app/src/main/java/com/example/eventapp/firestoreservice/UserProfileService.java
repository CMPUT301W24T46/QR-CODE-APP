package com.example.eventapp.firestoreservice;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides services for managing user profiles, including retrieving and updating user information
 * within a Firestore collection.
 */

public class UserProfileService {

    /**
     * Reference to the Firestore collection containing user profiles.
     */

    private final CollectionReference userRef ;
    private String userId ;

    private String name ;

    private String contactInformation ;

    private String description ;

    /**
     * Initializes a new instance of UserProfileService with specific user details.
     *
     * @param userRef             The Firestore collection reference to user profiles.
     * @param name                The name of the user.
     * @param userId              The unique identifier of the user.
     * @param contactInformation  The contact information of the user.
     * @param description         A description or additional information about the user.
     */

    public UserProfileService(CollectionReference userRef , String name ,String userId , String contactInformation , String description){
        this.userRef = userRef ;
        this.userId = userId ;
        this.contactInformation = contactInformation ;
        this.description = description ;
        this.name = name ;
    }

    /**
     * Retrieves the name of the user from Firestore and invokes the provided callback with the result.
     *
     * @param callback The callback to be invoked with the name of the user or an error.
     */

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

    /**
     * Updates the user's profile information in Firestore and invokes the provided callback upon completion.
     *
     * @param callback The callback to be invoked upon successful update or an error.
     */
    
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
