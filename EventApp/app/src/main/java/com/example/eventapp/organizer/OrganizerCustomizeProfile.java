package com.example.eventapp.organizer;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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
import androidx.appcompat.widget.SwitchCompat;

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
 * Activity for customizing and saving an organizer's profile, including image selection and database updates.
 */
public class OrganizerCustomizeProfile extends AppCompatActivity{

    private EditText username;
    private EditText contact;
    private EditText description;
    private Button btnOrganizerSave;
    private Context context;

    private ImageView profilePhtView;
    private SwitchCompat geolocationToggle;

    ActivityResultLauncher<PickVisualMediaRequest> pickMedia ;
    StorageReference storageReference;
    private User organizerUser;
    private ImageView profilePhotoView;
    private Button profileEditImageButton;
    private Button profileDeleteImageButton;

    /**
     * Initializes the activity for customizing the organizer's profile. This method sets up the
     * activity layout, initializes UI components, and sets the action bar title.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     *                           down then this Bundle contains the data it most recently supplied in
     *                           onSaveInstanceState(Bundle). Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_organizer_customize_profile);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Customize Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = this;

        username = findViewById(R.id.editTextTextEmailAddress);
        contact = findViewById(R.id.editTextPhone);
        description = findViewById(R.id.editTextTextMultiLine);
        btnOrganizerSave = findViewById(R.id.buttonSave);
        profilePhotoView = findViewById(R.id.organizerProfilePic);
        profileEditImageButton = findViewById(R.id.buttonCustomizeImage);
        profileDeleteImageButton = findViewById(R.id.buttonDeleteImage) ;

        storageReference = FirebaseStorage.getInstance().getReference();
        fetchDataFromFirestore();

        btnOrganizerSave.setOnClickListener(new View.OnClickListener() {
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
            // Callback is invoked after the user selects a media item or closes the photo picker.
            if (uri != null) {
                RequestOptions requestOptions = RequestOptions.bitmapTransform(new CircleCrop());
                Glide.with(context).load(uri).apply(requestOptions).into(profilePhotoView);
                UploadImage uploadImage = new UploadImage(uri) ;
                uploadImage.uploadToFireStore();
                Log.d("PhotoPicker", "Selected URI: " + uri);
            } else {
                Log.d("PhotoPicker", "No media selected");
            }
        });
    }

    /**
     * Launches a photo picker interface for the user to select either an image or a video. This method
     * creates a new {@link PickVisualMediaRequest} specifying the type of media to be picked - images
     * and videos in this case. It then launches the photo picker activity, allowing the user to choose
     * from their device's media. The selection result is handled by the activity result launcher
     * registered in the activity's {@code onCreate} method.
     *
     * This functionality provides a convenient way for users to update their profile picture or add
     * media content related to their profile directly from their device's gallery or other media sources.
     */
    private void uploadImage() {
        // Launch the photo picker and let the user choose images and videos.
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageAndVideo.INSTANCE)
                .build());
    }

    /**
     * Resets the profile image for the currently signed-in user to a default image. This operation
     * involves updating the user's document in the Firestore 'Users' collection to reference a
     * default image stored in the 'defaultImage' collection.
     *
     * The method retrieves the current {@link FirebaseUser} and updates their document in the 'Users'
     * collection. The 'imageUrl' field of the document is set to reference the 'NoImage' document
     * within the 'defaultImage' collection, effectively setting the user's profile image to a default.
     *
     * Upon successful update, {@code fetchDataFromFirestore()} is called to refresh the user's profile
     * information from Firestore, ensuring that any UI components relying on this data are updated to
     * reflect the change.
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
     * Retrieves and updates the UI with the current user's profile information from Firestore. Handles profile image retrieval based on 'imageUrl' field.
     * @implNote This method assumes that the user is logged in and that a valid FirebaseUser object
     *           exists. It requires the Firestore database to contain a "Users" collection with
     *           documents keyed by user IDs. Each document should at least contain fields for the
     *           user's name, contact information, homepage description, and optionally an 'imageUrl'
     *           that points to a profile image.
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
                        String userType = documentSnapshot.getString("typeOfUser");

                        organizerUser = new User(user.getUid(), usernameText, contactText, descriptionText, "", "Organizer");

                        // Set data to EditText views
                        username.setText(organizerUser.getName());
                        contact.setText(organizerUser.getContactInformation());
                        description.setText(organizerUser.getContactInformation());

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
     * Saves the updated user profile information to Firestore and displays a toast message based on success or required field validation.
     */
    private void saveChanges() {
        String usernameText = username.getText().toString().trim();
        String contactText = contact.getText().toString().trim();
        String descriptionText = description.getText().toString().trim();
//        May need to change the position of setters
        organizerUser.setName(usernameText);
        organizerUser.setContactInformation(contactText);
        organizerUser.setHomepage(descriptionText);
        organizerUser.setTypeOfUser("Organizer");
        if (usernameText.isEmpty() || contactText.isEmpty() || descriptionText.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        updateProfileInDatabase(usernameText, contactText, descriptionText);
        Toast.makeText(this, "Changes saved", Toast.LENGTH_SHORT).show();
    }

    /**
     * Updates the current user's profile information in Firestore with provided username, contact, and description.
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
            updates.put("typeOfUser", organizerUser.getTypeOfUser());

            userRef.update(updates);
        }
    }

    /**
     * Fetches the profile image URL from Firestore and updates the user's profile image in the UI.
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
                    organizerUser = new User(user.getUid(), usernameText, contactText, descriptionText, imageURL, "Organizer");
                    RequestOptions requestOptions = RequestOptions.bitmapTransform(new CircleCrop());
                    Glide.with(context).load(organizerUser.getImageURL()).apply(requestOptions).into(profilePhotoView);
                } else {

                    documentReferenceChecker.emptyDocumentReferenceWrite(user.getUid());
                    organizerUser = new User(user.getUid(), usernameText, contactText, descriptionText, "", "Organizer");
                    RequestOptions requestOptions = RequestOptions.bitmapTransform(new CircleCrop());
                    Glide.with(context).load(organizerUser.getImageURL()).apply(requestOptions).into(profilePhotoView);
                    Log.d("CustomizeProfile", "Image document doesn't exist");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                documentReferenceChecker.emptyDocumentReferenceWrite(user.getUid());
                organizerUser = new User(user.getUid(), usernameText, contactText, descriptionText, "", "Attendee");
                RequestOptions requestOptions = RequestOptions.bitmapTransform(new CircleCrop());
                Glide.with(context).load(organizerUser.getImageURL()).apply(requestOptions).into(profilePhotoView);
                Log.e("CustomizeProfile", "Error getting image document", e);
            }
        });
    }

    /**
     * Handles navigation when the up button is pressed in the action bar by mimicking the back button press.
     * @return true to indicate that the up navigation event was handled.
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}