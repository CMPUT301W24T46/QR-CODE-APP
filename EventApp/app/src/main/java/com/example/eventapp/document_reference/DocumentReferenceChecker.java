package com.example.eventapp.document_reference;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

/**
 * DocumentReferenceChecker provides utility methods to interact with Firestore document references,
 * particularly for handling default images and user profile images within an application.
 *
 * <p>This class includes methods to retrieve a default image document reference, write a user document
 * reference to Firestore, and update user documents with a default image reference. These operations are
 * common in scenarios where user profiles and associated images are managed within a Firestore database.</p>
 */

public class DocumentReferenceChecker {

    /**
     * Checks if a specific condition or document existence criteria is met. Currently, this method
     * always returns true, serving as a placeholder for more complex checks.
     *
     * @return Always returns true.
     */

    public static boolean documentChecker(){
        return true ;
    }

    /**
     * Retrieves a Firestore document reference pointing to a "NoImage" document within the "defaultImage" collection.
     * This document can be used as a fallback or default image reference for user profiles or other entities.
     *
     * @return A {@link DocumentReference} pointing to the "NoImage" document within the "defaultImage" collection.
     */

    public DocumentReference documentReferenceWrite(){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference referenceDocRef = firestore.collection("defaultImage").document("NoImage");
        return referenceDocRef ;
    }

    /**
     * Updates a user's document in Firestore with a new profile image reference. This method is useful when
     * the user updates their profile image, and the new image reference needs to be stored in Firestore.
     *
     * @param uid The unique identifier (UID) of the user whose document is to be updated.
     */

    public void documentReferenceUserWrite(String uid ){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference usersRef = firestore.collection("Users");
        DocumentReference docRef = firestore.collection("profileImages").document(uid) ;

        HashMap<String, Object> data = new HashMap<>();
        data.put("imageUrl", docRef);

        usersRef.document(uid)
                .update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("User Profile", "Successfuly written to with Updated Information");
                    }});
    }

    /**
     * Updates a user's document in Firestore to reference a default image, typically used when a user removes
     * their profile image or when a new user is created without a custom image.
     *
     * @param uid The unique identifier (UID) of the user whose document is to be updated with a default image reference.
     */

    public void emptyDocumentReferenceWrite(String uid){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference usersRef = firestore.collection("Users");
        DocumentReference docRef = firestore.collection("defaultImage").document("NoImage") ;
        HashMap<String, Object> data = new HashMap<>();
        data.put("imageUrl", docRef);

        usersRef.document(uid)
                .update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore 2", "DocumentSnapshot successfully written!");
                    }});
    }
}
