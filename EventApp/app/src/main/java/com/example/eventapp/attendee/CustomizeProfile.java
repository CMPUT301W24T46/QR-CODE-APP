package com.example.eventapp.attendee;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.eventapp.R;
import com.example.eventapp.document_reference.DocumentReferenceChecker;
import com.example.eventapp.upload_image.UploadImage;
import com.example.eventapp.users.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * The Activity after customize profile button is pressed
 */
public class CustomizeProfile extends AppCompatActivity {
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia ;
    StorageReference storageReference ;
    private boolean testing = false ;
    private EditText username;
    private EditText contact;
    private EditText description;
    private Button btnAttendeeSave;
    private Button profileEditImageButton;
    private String userId ;

    private Button profileDeleteImageButton;
    public User attendeeUser ;
    private ImageView profilePhotView ;
    private Context context ;
    /**
     * Called when the activity is first created. Responsible for initializing the activity's UI components,
     * setting up action bar title and back button, registering click listeners for buttons,
     * fetching existing user data from firebase, and register activity result for
     * upload/delete images.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customize_attendee_profile);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Customize Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = this ;
//        DeleteImage
        username = findViewById(R.id.editTextTextEmailAddress);
        contact = findViewById(R.id.editTextPhone);
        description = findViewById(R.id.editTextTextMultiLine);
        btnAttendeeSave = findViewById(R.id.AttendeeAccountSave);
        profilePhotView = findViewById(R.id.attendeeProfilePic) ;
        profileEditImageButton = findViewById(R.id.CustomizeImage);
        profileDeleteImageButton = findViewById(R.id.DeleteImage) ;


        storageReference = FirebaseStorage.getInstance().getReference() ;

        fetchDataFromFirestore();
        btnAttendeeSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });

        profileDeleteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteImage();
            }
        });

        profileEditImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });


        // Registers a photo picker activity launcher in single-select mode.
        // Include only one of the following calls to launch(), depending on the types
        // of media that you want to let the user choose from.
        pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    // Callback is invoked after the user selects a media item or closes the
                    // photo picker.
                    if (uri != null) {
                        RequestOptions requestOptions = RequestOptions.bitmapTransform(new CircleCrop());
                        Glide.with(context).load(uri).apply(requestOptions).into(profilePhotView);
                        UploadImage uploadImage = new UploadImage(uri) ;
                        uploadImage.uploadToFireStore();
                        Log.d("PhotoPicker", "Selected URI: " + uri);
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });
    }

    /**
     * Launches pick media that helps user to choose image
     */
    private void uploadImage() {
        // Launch the photo picker and let the user choose images and videos.
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageAndVideo.INSTANCE)
                .build());
    }

    /**
     * Deletes the user's profile image by updating the firebase entry with a reference to the default image.
     * after image deleted successfully, fetches the updated user data from firebase.
     */
    public void deleteImage(){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        CollectionReference usersRef = firestore.collection("Users");
        DocumentReference docRef = firestore.collection("defaultImage").document("NoImage") ;

        HashMap<String, Object> data = new HashMap<>();
        data.put("imageUrl", docRef);

        usersRef.document(user.getUid())
                .update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        fetchDataFromFirestore();
                    }});
    }

    /**
     * Fetches user data from Firestore and populates the EditText views with data.
     *
     */
    private void fetchDataFromFirestore() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String userId = user.getUid();

            DocumentReference userRef = FirebaseFirestore.getInstance().collection("Users").document(userId);
            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        // Retrieve data from Firestore document
                        String usernameText = documentSnapshot.getString("name");
                        String contactText = documentSnapshot.getString("contactInformation");
                        String descriptionText = documentSnapshot.getString("homepage");

                        attendeeUser = new User(user.getUid(), usernameText, contactText, descriptionText, "", "Attendee");

                        // Set data to EditText views
                        username.setText(attendeeUser.getName());
                        contact.setText(attendeeUser.getContactInformation());
                        description.setText(attendeeUser.getContactInformation());

                        // Check if 'imageUrl' field is present and of type DocumentReference
                        Object imageUrlObject = documentSnapshot.get("imageUrl");
                        if (imageUrlObject instanceof DocumentReference) {
                            DocumentReference imageRef = (DocumentReference) imageUrlObject;
                            getImageFromFireStore(imageRef, usernameText, contactText, descriptionText);
                        } else if (imageUrlObject != null) {
                            // Handle cases where 'imageUrl' field is present but not a DocumentReference
                            Log.e("CustomizeProfile", "'imageUrl' field is not a DocumentReference, it is: " + imageUrlObject.toString());
                            // Handle this case as necessary, such as displaying a default image or clearing the existing image
                        } else {
                            // Handle cases where 'imageUrl' field is not present
                            Log.e("CustomizeProfile", "'imageUrl' field is not present");
                            // Handle this case as necessary, such as displaying a default image or clearing the existing image
                        }


                    } else {
                        Log.d("CustomizeProfile", "document doesn't exist");
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("CustomizeProfile", "Error getting document", e);
                }
            });
        }
    }

    /**
     *Handles the selection of menu items in the activity's options menu.
     * @param item The menu item that was selected.
     *
     * @return super.onOptionsItemSelected(item);
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Navigate back to AttendeeAccount
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Saves changes made to the user profile by updating the database with the new information.
     * Displays a toast message indicating whether the changes were saved successfully.
     */
    private void saveChanges() {
        String usernameText = username.getText().toString().trim();
        String contactText = contact.getText().toString().trim();
        String descriptionText = description.getText().toString().trim();
//        May need to change the position of setters
        if(attendeeUser == null){
            attendeeUser = TestUser() ;
            Toast.makeText(this, "Changes saved", Toast.LENGTH_SHORT).show();
        }else{
            attendeeUser.setName(usernameText);
            attendeeUser.setContactInformation(contactText);
            attendeeUser.setHomepage(descriptionText);
            attendeeUser.setTypeOfUser("Attendee");
        }

        if (usernameText.isEmpty() || contactText.isEmpty() || descriptionText.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        updateProfileInDatabase(usernameText, contactText, descriptionText);
        //Toast.makeText(this, "Changes saved", Toast.LENGTH_SHORT).show();
    }

    /**
     * Updates the user profile information in the Firestore database.
     *
     * @param username    The new username to be updated.
     * @param contact     The new contact information to be updated.
     * @param description The new description or homepage to be updated.
     *
     */
    private void updateProfileInDatabase(String username, String contact, String description) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String userId = user.getUid();

            DocumentReference userRef = db.collection("Users").document(userId);

            Map<String, Object> updates = new HashMap<>();
            updates.put("name", username);
            updates.put("contactInformation", contact);
            updates.put("homepage", description);
            updates.put("typeOfUser", attendeeUser.getTypeOfUser());

            userRef.update(updates);
        }
    }

    /**
     * Retrieves the image URL from firebase based on the provided document reference,
     * and updates the user's profile information accordingly.
     *
     * @param imageRef       The document reference pointing to the image in Firestore.
     * @param usernameText   The username of the attendee.
     * @param contactText    The contact information of the attendee.
     * @param descriptionText The description of the attendee.
     */
    private void getImageFromFireStore(DocumentReference imageRef , String usernameText , String contactText , String descriptionText){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReferenceChecker documentReferenceChecker = new DocumentReferenceChecker() ;
        imageRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot imageDocumentSnapshot) {
                if (imageDocumentSnapshot.exists()) {
                    // Retrieve the URL field from the image document
                    String imageURL = imageDocumentSnapshot.getString("URL");
                    // Now you have the imageURL
                    attendeeUser = new User(user.getUid(), usernameText, contactText, descriptionText, imageURL, "Attendee");
                    RequestOptions requestOptions = RequestOptions.bitmapTransform(new CircleCrop());
                    Glide.with(context).load(attendeeUser.getImageURL()).apply(requestOptions).into(profilePhotView);
                } else {

                    documentReferenceChecker.emptyDocumentReferenceWrite(user.getUid());
                    attendeeUser = new User(user.getUid(), usernameText, contactText, descriptionText, "", "Attendee");
                    RequestOptions requestOptions = RequestOptions.bitmapTransform(new CircleCrop());
                    Glide.with(context).load(attendeeUser.getImageURL()).apply(requestOptions).into(profilePhotView);
                    Log.d("CustomizeProfile", "Image document doesn't exist");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                documentReferenceChecker.emptyDocumentReferenceWrite(user.getUid());
                attendeeUser = new User(user.getUid(), usernameText, contactText, descriptionText, "", "Attendee");
                RequestOptions requestOptions = RequestOptions.bitmapTransform(new CircleCrop());
                Glide.with(context).load(attendeeUser.getImageURL()).apply(requestOptions).into(profilePhotView);
                Log.e("CustomizeProfile", "Error getting image document", e);
            }
        });
    }

    private User TestUser(){
        User testUser = new User("123", "123", "123", "123", "123", "Attendee");
        testing = true ;
        return testUser;
    }
    public void onCustomizeProfileSaveClicked(View view) {
        // Implementation for saving profile changes
    }
    public void onCustomizeProfileCustomizeImageClicked(View view) {
        // Implementation for customizing image
    }

    public void onCustomizeProfileDeleteImageClicked(View view) {
        // Implementation for deleting image
    }

}
