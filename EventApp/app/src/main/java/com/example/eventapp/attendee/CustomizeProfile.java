package com.example.eventapp.attendee;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.eventapp.R;
import com.example.eventapp.document_reference.DocumentReferenceChecker;
import com.example.eventapp.Image.UploadImage;
import com.example.eventapp.users.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Activity for customizing the user's profile.
 * This activity provides fields to update the user's username, contact information,
 * and personal description. It also allows the user to select a profile image from their device's gallery and enables
 * users to delete images
 * The activity interacts with Firestores database to edit, upload, and delete users Information
 *
 */
public class CustomizeProfile extends AppCompatActivity {
    private static final int GALLERY_REQUEST_CODE = 123;
    StorageReference storageReference ;
    private EditText username;
    private EditText contact;
    private boolean isDeleted = false ;
    private boolean isSaved = false ;
    private boolean testing = false ;
    private EditText description;
    private Button btnAttendeeSave;
    private Button profileEditImageButton;
    private Button profileDeleteImageButton;
    private User attendeeUser ;
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
    }

    /**
     * Initiates an intent to pick an image from the device's gallery.
     * The selected image will be used for updating the user's profile picture.
     * This method launches the gallery application via an intent where the user
     * can choose an image. Once the image is selected, the result is returned in
     * the onActivityResult method.
     */
    private void uploadImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    /**
     * Callback method from starting the image pick intent via {@link #uploadImage()}.
     * This method handles the result returned from the image selection activity.
     * @param requestCode The integer request code originally supplied to startActivityForResult(),
     *                    allowing you to identify who this result came from.
     * @param resultCode  The integer result code returned by the child activity through its setResult().
     * @param data        An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Log.d("Valid", "RESULT_OK");
            if (requestCode == GALLERY_REQUEST_CODE) {
                Log.d("Valid", "GALLERY_REQUEST_CODE detected.");
                if (data != null && data.getData() != null) {
                    Uri uri = data.getData();
                    Log.d("Valid", "URI: " + uri);
                    RequestOptions requestOptions = RequestOptions.bitmapTransform(new CircleCrop());
                    Glide.with(context).load(uri).apply(requestOptions).into(profilePhotView);
                    UploadImage uploadImage = new UploadImage(uri) ;
                    if(FirebaseAuth.getInstance().getCurrentUser() != null){
                        uploadImage.uploadToFireStore();
                    }
                }
            }
        }
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
                        isDeleted = true;
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

        if (attendeeUser == null) {
            Toast.makeText(this, "User data not loaded yet, please try again.", Toast.LENGTH_SHORT).show();
            return;
        }
        attendeeUser.setName(usernameText);
        attendeeUser.setContactInformation(contactText);
        attendeeUser.setHomepage(descriptionText);
        attendeeUser.setTypeOfUser("Attendee");

        if (usernameText.isEmpty() || contactText.isEmpty() || descriptionText.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        updateProfileInDatabase(usernameText, contactText, descriptionText);


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
            userRef.update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(CustomizeProfile.this, "Profile updated successfully.", Toast.LENGTH_SHORT).show();
                    isSaved = true;
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CustomizeProfile.this, "Failed to update profile.", Toast.LENGTH_SHORT).show();
                }
            });;

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


    /**
     * Checks if the profile or associated data is currently marked as deleted.
     *
     * @return true if the profile or data is marked as deleted, false otherwise.
     */
    public boolean isDeleting(){
        return isDeleted ;
    }

    /**
     * Checks if the profile or associated data is currently being saved.
     *
     * @return true if the data is marked as successfully saved, false otherwise.
     */
    public boolean isSaving(){
        return isSaved ;
    }
}
