package com.example.eventapp.organizer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.example.eventapp.R;
import com.example.eventapp.attendee.QRCodeScanFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * OrganizerEventInfo is a Fragment that displays detailed information about a specific event.
 * It shows the event's name, description, date, and image, and provides options to edit the event
 * or reuse the event's QR code.
 */

public class OrganizerEventInfo extends Fragment {

    private NavController navController;

    private String eventId ;

    /**
     * Called when the fragment is first created. This method initializes the fragment.
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return Return the View for the fragment's UI, or null.
     */

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_organizer_event_info, container, false);
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} has returned, but before any
     * saved state has been restored into the view. This method initializes the fragment's content and event handlers.
     *
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     */

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Event Information");
            actionBar.setDisplayHomeAsUpEnabled(true); // Enable the back button
        }

        // Retrieve the event details from the Bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            String eventName = bundle.getString("eventName");
            String eventDate = bundle.getString("eventDate");
            String imageUrl = bundle.getString("imageURL");
            String eventDescription = bundle.getString("eventDescription");
            eventId = bundle.getString("eventId");
//            Log.d("OrganizerEventInfo", "Event ID: " + eventId);

            TextView eventNameView = view.findViewById(R.id.eventName_info);
            TextView eventDescriptionView = view.findViewById(R.id.eventDescription_info);
            TextView eventDateView = view.findViewById(R.id.event_date_time);
            ImageView eventImageView = view.findViewById(R.id.organizer_biggerEventImage);

            eventNameView.setText(eventName);
            eventDescriptionView.setText(eventDescription);
            eventDateView.setText(eventDate);

            // Log.d("eventDescription", "Event Description: " + eventDescription);

            // Load the event image
            Glide.with(this).load(imageUrl).into(eventImageView);

            navController = Navigation.findNavController(view);
            View editEventButton = view.findViewById(R.id.button_editEvent_info);
            Button realTimeScanButton = view.findViewById(R.id.realTimeScanBtn) ;

            realTimeScanButton.setOnClickListener(v -> {
                IntentIntegrator.forSupportFragment(OrganizerEventInfo.this)
                        .setCaptureActivity(CaptureActivity.class)
                        .initiateScan();
            });

            editEventButton.setOnClickListener(v -> {
                Bundle newBundle = new Bundle();
                newBundle.putString("eventId", eventId);
//                Log.d("OrganizerEventInfo", "Navigating with Event ID: " + eventId);
                navController.navigate(R.id.action_organizerEventInfo_to_organizer_edit_event_selection,newBundle);
            });
            View reuseQRCodeButton = view.findViewById(R.id.btn_reuse_qrcode);
            reuseQRCodeButton.setOnClickListener(v -> {
                // Check if eventId is not null or empty
                if (eventId != null && !eventId.isEmpty()) {
                    Intent intent = new Intent(getContext(), QRCodeReuseActivity.class);
                    intent.putExtra("eventId", eventId);
                    startActivity(intent);
                } else {
                    // Log error or show a message if eventId is null or empty
                    Log.e("OrganizerEventInfo", "Event ID is null or empty. Cannot navigate to QRCodeReuseActivity.");
                    Toast.makeText(getContext(), "Error: No event ID found.", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(FirebaseAuth.getInstance().getUid() == null){
            Log.d("Enteredfor" , "Testing") ;
            return ;
        }
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                saveQRCodeDetails(eventId , "CheckIn" , result.getContents())  ;
                // Parse the QR code data
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

//    private void uploadAndSaveQRCode(Bitmap bitmap, String qrCodeInfo) {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//        byte[] data = baos.toByteArray();
//
//        String path = "qr_codes/" + UUID.randomUUID() + ".png";
//        StorageReference qrCodeRef = FirebaseStorage.getInstance().getReference(path);
//
//        UploadTask uploadTask = qrCodeRef.putBytes(data);
//        uploadTask.addOnSuccessListener(taskSnapshot -> qrCodeRef.getDownloadUrl().addOnSuccessListener(uri -> {
//                    String qrCodeImageUrl = uri.toString();
//                    saveQRCodeDetails(eventId, "CheckIn", qrCodeInfo);
//                }))
//                .addOnFailureListener(e -> Toast.makeText(getContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
//    }

    private void saveQRCodeDetails(String eventId, String type, String qrCodeInfo) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Check if a QR code with this info already exists.
        db.collection("QRCode")
                .whereEqualTo("qrCodeInfo", qrCodeInfo)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // QR code already exists, so delete the old one
                        String documentId = queryDocumentSnapshots.getDocuments().get(0).getId();
                        db.collection("QRCode").document(documentId).delete()
                                .addOnSuccessListener(aVoid -> Log.d("TAG", "Existing QR code document deleted."))
                                .addOnFailureListener(e -> Log.e("TAG", "Error deleting existing QR code document", e));
                    }

                    // Proceed to add the new QR code info
                    Map<String, Object> qrCodeData = new HashMap<>();
                    qrCodeData.put("eventId", eventId);
                    qrCodeData.put("type", type);
                    qrCodeData.put("qrCodeInfo", qrCodeInfo);

                    db.collection("QRCode").add(qrCodeData)
                            .addOnSuccessListener(documentReference -> {
//                                Toast.makeText(QRCodeReuseActivity.this, "Saved QR code info successfully!", Toast.LENGTH_SHORT).show();
                                Log.d("TAG", "QR code data saved successfully.");
                            })
                            .addOnFailureListener(e -> {
//                                Toast.makeText(QRCodeReuseActivity.this, "Error saving QR code info: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e("TAG", "Error saving QR code data", e);
                            });
                })
                .addOnFailureListener(e -> Log.e("TAG", "Error fetching QR code documents", e));
    }

}