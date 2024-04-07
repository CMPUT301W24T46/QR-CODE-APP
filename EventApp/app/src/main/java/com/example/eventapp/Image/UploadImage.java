package com.example.eventapp.Image;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.eventapp.attendee.CustomizeProfile;
import com.example.eventapp.document_reference.DocumentReferenceChecker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles the uploading of an image to Firebase Cloud Storage and updates the user's Firestore document
 * with a reference to the uploaded image.
 */

public class UploadImage {

    private Uri imageUri ;
    private String userId ;

    /**
     * Constructs a new {@link UploadImage} instance.
     *
     * @param imageURI The URI of the image to upload.
     */

    public  UploadImage(Uri imageURI ){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        this.imageUri = imageURI ;
        this.userId = user.getUid() ;
    }

    /**
     * Uploads the image to Firebase Cloud Storage and on successful upload, saves the image URL to Firestore.
     */

    public void uploadToFireStore() {
        if (userId == null) {
            Log.e("UploadImage", "User ID is null, cannot upload image.");
            return;
        }

        StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                .child("profileImages")
                .child(userId + "_profile_image.jpg");

        // Upload the image to Firebase Cloud Storage
        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot ->
                        // Get the download URL after successful upload
                        storageRef.getDownloadUrl().addOnSuccessListener(this::saveImageUrlToFirestore)
                )
                .addOnFailureListener(e ->
                        Log.e("UploadImage", "Error uploading image", e)
                );
    }

    /**
     * Saves the download URL of the uploaded image to the 'profileImages' collection in Firestore.
     *
     * @param downloadUrl The download URL of the uploaded image.
     */

    private void saveImageUrlToFirestore(Uri downloadUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> data = new HashMap<>();
        data.put("URL", downloadUrl.toString()); // Use the download URL from Firebase Storage

        // Save the download URL in the 'profileImages' collection
        db.collection("profileImages")
                .document(userId)
                .set(data)
                .addOnSuccessListener(aVoid -> {
                    Log.d("UploadImage", "Image URL saved to profileImages collection");
                    addReferenceToUserDatabase();
                })
                .addOnFailureListener(e ->
                        Log.e("UploadImage", "Error saving image URL", e)
                );
    }

    /**
     * Updates the user's Firestore document with a reference to the image document in the 'profileImages' collection.
     */

    private void addReferenceToUserDatabase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a reference to the image document in the 'profileImages' collection
        DocumentReference imageRef = db.collection("profileImages").document(userId);

        // Update the user's document with the reference to the image document
        Map<String, Object> userData = new HashMap<>();
        userData.put("imageUrl", imageRef);
        userData.put("hasCustomImage", true);

        db.collection("Users").document(userId)
                .update(userData)
                .addOnSuccessListener(aVoid ->
                        Log.d("UploadImage", "User document updated with image reference")
                )
                .addOnFailureListener(e ->
                        Log.e("UploadImage", "Error updating user document", e)
                );
    }

}
