package com.example.eventapp.upload_image;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.eventapp.attendee.CustomizeProfile;
import com.example.eventapp.document_reference.DocumentReferenceChecker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class UploadImage {

    private Uri imageUri ;
    private String userId ;
    public  UploadImage(Uri imageURI ){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        this.imageUri = imageURI ;
        this.userId = user.getUid() ;
    }

    public void uploadToFireStore(){
        if(userId != null){
            StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                    .child("profileImages")
                    .child(userId + "_profile_image.jpg");

            // Upload the image to Firebase Cloud Storage
            storageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Image uploaded successfully, get the download URL
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Save the download URL to the Image collection in Firestore
                            Log.e("Image Upload", "Successful");
                            saveImageUrlToFirestore(uri);
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Handle unsuccessful upload
                        Log.e("CustomizeProfile", "Error uploading image", e);
                    });
        }
    }

    private void saveImageUrlToFirestore(Uri imageUri){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

//      Create a new document in a collection and set the URI field
        Map<String, Object> data = new HashMap<>();
        data.put("URL", imageUri.toString()); // Convert URI to string and store it

//      Add the document to a collection named "Images"
        db.collection("profileImages")
                .document(userId)
                .set(data)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore Image saved to image collection", "Ini");
                    addReferenceToUserDatabase();
                    // Document added successfully
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error adding document", e);
                    // Handle failure (e.g., show an error message)
                });
    }

    private void addReferenceToUserDatabase(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //      Create a new document in a collection and set the URI field
        Map<String, Object> data = new HashMap<>();
        data.put("URL", imageUri.toString()); // Convert URI to string and store it

//      Add the document to a collection named "Images"
        db.collection("profileImages")
                .document(userId)
                .set(data)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore Image saved to image collection", "Ini");
                    // Document added successfully
                    DocumentReferenceChecker.documentReferenceUserWrite(userId);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error adding document", e);
                    // Handle failure (e.g., show an error message)
                });
    }
}
