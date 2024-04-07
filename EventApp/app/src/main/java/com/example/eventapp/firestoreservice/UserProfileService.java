package com.example.eventapp.firestoreservice;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;
/**
 * UserProfileService handles user profile operations within the application, such as retrieving and updating user information.
 * It interacts with Firestore to fetch user details and to update user profile data. The service is initialized with a reference
 * to the users collection in Firestore and user-specific details like name, contact information, and description.
 */

public class UserProfileService {

    private final CollectionReference userRef ;
    private String userId ;

    private String name ;

    private String contactInformation ;

    private String description ;

    /**
     * Constructs a new UserProfileService with the specified Firestore collection reference and user details.
     *
     * @param userRef             The Firestore collection reference for users.
     * @param name                The name of the user.
     * @param userId              The unique identifier of the user.
     * @param contactInformation  The contact information of the user.
     * @param description         A description of the user.
     */

    public UserProfileService(CollectionReference userRef , String name ,String userId , String contactInformation , String description){
        this.userRef = userRef ;
        this.userId = userId ;
        this.contactInformation = contactInformation ;
        this.description = description ;
        this.name = name ;
    }

    /**
     * Retrieves the user's profile information from Firestore and communicates the result through a callback interface.
     * The method fetches the user's name as an example of the information being retrieved.
     *
     * @param callback The callback interface to handle the result of the Firestore operation.
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
     * Updates the user's profile information in Firestore with the current details held by this UserProfileService instance.
     * The method updates fields like the user's name, contact information, and description.
     *
     * @param callback The callback interface to handle the result of the Firestore operation.
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
