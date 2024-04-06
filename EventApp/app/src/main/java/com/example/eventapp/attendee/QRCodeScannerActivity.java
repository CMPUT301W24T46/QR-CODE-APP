package com.example.eventapp.attendee;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.eventapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.integration.android.IntentIntegrator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class QRCodeScannerActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final int REQUEST_CODE_GALLERY = 1001;
    private static final int REQUEST_LOCATION_PERMISSIONS = 11;

    private static final String[] REQUIRED_PERMISSIONS = {Manifest.permission.CAMERA};
    private static final String[] LOCATION_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private PreviewView previewView;
    private ImageButton backButton, galleryButton;
    private ImageView selectedImageView;

    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_scanner);
        selectedImageView = findViewById(R.id.selected_image_view);

        previewView = findViewById(R.id.scanner_previewView);
        backButton = findViewById(R.id.backButton);
        galleryButton = findViewById(R.id.galleryButton);

        backButton.setOnClickListener(v -> finish());
        galleryButton.setOnClickListener(v -> openGallery());

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            requestCameraPermissions();
        }
        previewView.post(this::animateScanningLine);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void requestCameraPermissions(){
        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    // Request location permissions
    private void requestLocationPermissions() {
        ActivityCompat.requestPermissions(this, LOCATION_PERMISSIONS, REQUEST_LOCATION_PERMISSIONS);
    }

    private boolean allLocationPermissionsGranted() {
        for (String permission : LOCATION_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void getLastLocationAndCheckIn(Bitmap bitmap, String bitmapUri) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Handle permission denial gracefully
            Toast.makeText(this, "Location permission is needed to check in.", Toast.LENGTH_SHORT).show();
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    // Got last known location, it could be null
                    if (location != null) {
                        // Proceed to scan the bitmap for the QR code and check in
                        scanBitmapForQRCode(bitmap, bitmapUri, location.getLatitude(), location.getLongitude());
                    } else {
                        Toast.makeText(this, "Unable to retrieve location. Please ensure your location is on.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), imageProxy -> {
                    @SuppressLint("UnsafeOptInUsageError") Image image = imageProxy.getImage();
                    if (image != null) {
                        scanImage(image, imageProxy);
                    }
                });

                preview.setSurfaceProvider(previewView.getSurfaceProvider());
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
            } catch (Exception e) {
                Toast.makeText(this, "Failed to start camera: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }
    private void scanImage(Image image, ImageProxy imageProxy) {
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] data = new byte[buffer.capacity()];
        buffer.get(data);

        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, null);
        // Assume you have a method scanBitmapForQRCode that takes a Bitmap
        if (bitmap != null) {
            scanBitmapForQRCode(bitmap, null, 0, 0); // latitude and longitude are placeholders
        }

        imageProxy.close(); // Make sure to close the imageProxy
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else if (requestCode == REQUEST_LOCATION_PERMISSIONS) {
            // Check if location permissions are granted and proceed with getting the location
            if (allLocationPermissionsGranted()) {
                fetchLastLocationAndProceed();
            } else {
                Toast.makeText(this, "Location permissions not granted.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void fetchLastLocationAndProceed() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Safety check if the permissions are not granted at this point
            Toast.makeText(this, "Location permission is required.", Toast.LENGTH_LONG).show();
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        // Use the location for whatever you need
                        Log.d("Location", "Got last known location. Lat: " + location.getLatitude() + ", Lon: " + location.getLongitude());
                    } else {
                        Toast.makeText(this, "Location could not be retrieved.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void animateScanningLine() {
        View scanningLine = findViewById(R.id.scanning_line);
        final float startY = 0f;
        final float endY = previewView.getHeight();

        ObjectAnimator animation = ObjectAnimator.ofFloat(scanningLine, "translationY", startY, endY);
        animation.setDuration(3000);
        animation.setRepeatCount(0);
        animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // Reset the position to the top and start the animation again
                scanningLine.setTranslationY(0f);
                animateScanningLine();
            }
        });
        animation.start();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQUEST_CODE_GALLERY || requestCode == IntentIntegrator.REQUEST_CODE) && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                // Show the selected image
                selectedImageView.setVisibility(View.VISIBLE);
                selectedImageView.setImageBitmap(bitmap);
                Log.d("QRCodeScanner", "Scanned Bitmap URI: " + imageUri);

                if (allLocationPermissionsGranted()) {
                    getLastLocationAndCheckIn(bitmap, imageUri.toString());
                } else {
                    requestLocationPermissions();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void scanBitmapForQRCode(Bitmap bitmap, String bitmapUri, double latitude, double longitude) {
        int[] intArray = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(intArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        LuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), intArray);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

        try {
            Result result = new MultiFormatReader().decode(binaryBitmap);
            String qrCodeData = result.getText();
            Log.d("QRCodeData", "QRCodeData: " + qrCodeData);

            // Directly attempt to handle the QR code info
            checkQRCodeInFirestore(qrCodeData, latitude, longitude, () -> {
                // If direct handling fails, try parsing JSON
                try {
                    JSONObject qrData = new JSONObject(qrCodeData);
                    String qrCodeId = qrData.optString("qrCodeId", "");
                    String eventId = qrData.optString("eventId", "");
                    String type = qrData.optString("type","");
                    validateQRCode(qrCodeId, eventId, type, latitude, longitude);
                    if (!qrCodeId.isEmpty() && !eventId.isEmpty()) {
                        checkInUser(eventId, latitude, longitude);
                    } else {
                        Toast.makeText(QRCodeScannerActivity.this, "QR code info not found.", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(QRCodeScannerActivity.this, "Invalid QR code format.", Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Log.e("QRCodeScanner", "Error in QR code data processing", e);
            showInvalidQRCodeMessage();
        }
    }

    private void validateQRCode(String qrCodeId, String eventId, String type, double latitude, double longitude) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (type.equals("CheckIn")) {
            checkInUser(eventId,latitude, longitude);
        } else if (type.equals("EventInfo")) {
            navigateToEventInfoPage(eventId);
        } else {
            // QR code data does not match expected structure or values
            showInvalidQRCodeMessage();
        }
    }

    private void showInvalidQRCodeMessage() {
        Toast.makeText(this, "Invalid QR Code. Please try another.", Toast.LENGTH_LONG).show();
        if(selectedImageView != null) {
            selectedImageView.setVisibility(View.INVISIBLE);
        }
    }

    private void checkQRCodeInFirestore(String qrCodeInfo, double latitude, double longitude, Runnable onNotFound) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("QRCode")
                .whereEqualTo("qrCodeInfo", qrCodeInfo)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Found matching QR code info, proceed with event check-in
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        String eventId = document.getString("eventId");
                        if (eventId != null) {
                            checkInUser(eventId, latitude, longitude);
                        }
                    } else {
                        // No direct match found, try JSON parsing
                        onNotFound.run();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("QRCodeScanner", "Error fetching QR code data from Firestore", e);
                    onNotFound.run();
                });
    }


    private void checkInUser(String eventId, double latitude, double longitude) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

                // Document reference for the AttendedEvents document
                DocumentReference attendedEventsRef = db.collection("AttendedEvents").document(userId);

                // Create or get the AttendedEvents document
                attendedEventsRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            updateAttendedEventsArray(db, document.getReference(), eventId);
                        } else {
                            // Document does not exist, create a new one and then update the allAttendedEvents array
                            createNewAttendedEventsDocument(userId, db, eventId);
                        }

                    } else {
                        Log.d("QRCodeScanner", "Failed to get document: ", task.getException());
                    }
                });

                DocumentReference checkInDocRef = db.collection("Events").document(eventId)
                        .collection("CheckIns").document(); // randomly generate document id


                db.runTransaction(transaction -> {
                    DocumentSnapshot checkInSnapshot = transaction.get(checkInDocRef);
                    long checkInTimes = 1;
                    if (checkInSnapshot.exists()) {
                        Number times = checkInSnapshot.getLong("CheckInTimes");
                        if (times != null) {
                            checkInTimes = times.longValue() + 1;
                        }
                    }
                    // Prepare the data to update
                    Map<String, Object> checkInData = new HashMap<>();
                    checkInData.put("attendeeId", userId);
                    checkInData.put("checkInDate", FieldValue.serverTimestamp());
    //                checkInData.put("checkInTimes", checkInTimes);
                    checkInData.put("checkInLocation", new GeoPoint(latitude, longitude));
                    // Update the document with the new data
                    transaction.set(checkInDocRef, checkInData);
                    return null; // To satisfy the Transaction.Function interface
                }).addOnSuccessListener(aVoid -> {
                    Log.d("QRCodeScanner", "User " + userId + " checked in successfully for event " + eventId);
                    Toast.makeText(QRCodeScannerActivity.this, "Check-in successful!", Toast.LENGTH_SHORT).show();
                    resetScanner();
                    finish(); // Close the activity

                    // Add milestone message after successful check-in
                    DocumentReference eventDocRef = db.collection("Events").document(eventId);
                    eventDocRef.get().addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String eventName = documentSnapshot.getString("eventName");
                            String organizerId = documentSnapshot.getString("creatorId");
                            if (organizerId != null) {
                                // Retrieve attendee username
                                DocumentReference attendeeRef = db.collection("Users").document(userId);
                                attendeeRef.get().addOnSuccessListener(attendeeSnapshot -> {
                                    if (attendeeSnapshot.exists()) {
                                        String attendeeUsername = attendeeSnapshot.getString("name");
                                        // Construct milestone message
                                        String milestoneMessage = attendeeUsername + " has checked in to event " + eventName;
                                        // Get current timestamp
                                        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a");
                                        String timestamp = dateFormat.format(new Date());

                                        // Create milestone data
                                        Map<String, Object> milestoneData = new HashMap<>();
                                        milestoneData.put("title", "Check-in Alert!");
                                        milestoneData.put("message", milestoneMessage);
                                        milestoneData.put("timestamp", timestamp);

                                        // Check if the distinct user count has reached a certain amount
                                        CollectionReference checkInsRef = db.collection("Events").document(eventId).collection("CheckIns");
                                        checkInsRef.get().addOnCompleteListener(checkInTask -> {
                                            if (checkInTask.isSuccessful()) {
                                                QuerySnapshot querySnapshot = checkInTask.getResult();
                                                if (querySnapshot != null) {
                                                    Set<String> attendeeIds = new HashSet<>();
                                                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                                        String attendeeId = document.getString("attendeeId");
                                                        if (attendeeId != null) {
                                                            attendeeIds.add(attendeeId);
                                                        }
                                                    }
                                                    int distinctAttendeeCount = attendeeIds.size();
                                                    if (distinctAttendeeCount == 5 || distinctAttendeeCount == 10 || distinctAttendeeCount == 50 || distinctAttendeeCount == 100) {
                                                        // Construct milestone message
                                                        String eventMilestoneMessage = distinctAttendeeCount + " users have checked in to event " + eventName + "!";
                                                        // Get current timestamp
                                                        String eventTimestamp = dateFormat.format(new Date());

                                                        // Create milestone data
                                                        Map<String, Object> eventMilestoneData = new HashMap<>();
                                                        eventMilestoneData.put("title", "Event Milestone");
                                                        eventMilestoneData.put("message", eventMilestoneMessage);
                                                        eventMilestoneData.put("timestamp", eventTimestamp);

                                                        // Check if same message exist in milestones
                                                        db.collection("Milestones").document(organizerId)
                                                                .get()
                                                                .addOnCompleteListener(milestoneTask -> {
                                                                    if (milestoneTask.isSuccessful()) {
                                                                        DocumentSnapshot milestoneSnapshot = milestoneTask.getResult();
                                                                        if (milestoneSnapshot != null && milestoneSnapshot.exists()) {
                                                                            List<Map<String, Object>> allMilestones = (List<Map<String, Object>>) milestoneSnapshot.get("allMilestones");
                                                                            boolean messageExists = false;
                                                                            if (allMilestones != null) {
                                                                                for (Map<String, Object> milestone : allMilestones) {
                                                                                    if (milestone.get("message").equals(eventMilestoneMessage)) {
                                                                                        messageExists = true;
                                                                                        break;
                                                                                    }
                                                                                }
                                                                            }
                                                                            if (!messageExists) {
                                                                                // Message not exist, add it
                                                                                DocumentReference milestoneRef = db.collection("Milestones").document(organizerId);
                                                                                milestoneRef.update("allMilestones", FieldValue.arrayUnion(eventMilestoneData))
                                                                                        .addOnSuccessListener(aVoids -> Log.d("QRCodeScanner", "Event milestone added"))
                                                                                        .addOnFailureListener(e -> Log.e("QRCodeScanner", "Failed to add event milestone", e));
                                                                            } else {
                                                                                // Message exists, don't add
                                                                                Log.d("QRCodeScanner", "Event milestone already exists: " + eventMilestoneMessage);
                                                                            }
                                                                        }
                                                                    } else {
                                                                        Log.e("QRCodeScanner", "Error checking event milestone: ", milestoneTask.getException());
                                                                    }
                                                                });
                                                    }
                                                }
                                            } else {
                                                Log.e("QRCodeScanner", "Error checking if attendees joined: ", checkInTask.getException());
                                            }
                                        });


                                        // add the milestone message
                                        DocumentReference milestoneRef = db.collection("Milestones").document(organizerId);
                                        milestoneRef.get().addOnSuccessListener(organizerSnapshot -> {
                                            if (!organizerSnapshot.exists()) {
                                                // if organizer's Milestones document doesn't exist, create it
                                                Map<String, Object> initialData = new HashMap<>();
                                                initialData.put("allMilestones", new ArrayList<>());
                                                db.collection("Milestones").document(organizerId)
                                                        .set(initialData)
                                                        .addOnSuccessListener(aVoid1 -> {
                                                            Log.d("QRCodeScanner", "Milestones document created for organizer " + organizerId);
                                                            // Now add the milestone message
                                                            milestoneRef.update("allMilestones", FieldValue.arrayUnion(milestoneData))
                                                                    .addOnSuccessListener(aVoids -> Log.d("QRCodeScanner", "Milestone added for check-in"))
                                                                    .addOnFailureListener(e -> Log.e("QRCodeScanner", "Failed to add milestone for check-in", e));
                                                        })
                                                        .addOnFailureListener(e -> Log.e("QRCodeScanner", "Failed to create Milestones document for organizer " + organizerId, e));
                                            } else {
                                                // else if organizer's Milestones document exists, directly add the milestone message
                                                milestoneRef.update("allMilestones", FieldValue.arrayUnion(milestoneData))
                                                        .addOnSuccessListener(aVoid1 -> Log.d("QRCodeScanner", "Milestone added for check-in"))
                                                        .addOnFailureListener(e -> Log.e("QRCodeScanner", "Failed to add milestone for check-in", e));
                                            }
                                        }).addOnFailureListener(e -> Log.e("QRCodeScanner", "Failed to check Milestones document for organizer " + organizerId, e));
                                    } else {
                                        Log.e("QRCodeScanner", "Attendee document not found for ID " + userId);
                                    }
                                }).addOnFailureListener(e -> Log.e("QRCodeScanner", "Failed to retrieve attendee document for ID " + userId, e));
                            } else {
                                Log.e("QRCodeScanner", "Organizer ID not found for event " + eventId);
                            }
                        } else {
                            Log.e("QRCodeScanner", "Event document not found for ID " + eventId);
                        }
                    }).addOnFailureListener(e -> {
                        Log.e("QRCodeScanner", "Failed to retrieve event document for ID " + eventId, e);
                    });

                }).addOnFailureListener(e -> {
                    Log.e("QRCodeScanner", "Check-in failed for user " + userId + " at event " + eventId, e);
                    Toast.makeText(QRCodeScannerActivity.this, "Check-in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
        } else {
            Toast.makeText(this, "User ID is null, cannot check in.", Toast.LENGTH_SHORT).show();
        }
    }

    private void createNewAttendedEventsDocument(String userId, FirebaseFirestore db, String eventId) {
        db.collection("Events").document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String eventName = documentSnapshot.getString("eventName");
                        String eventDate = documentSnapshot.getString("eventDate");
                        String eventDescription = documentSnapshot.getString("eventDescription");
                        String imageURL = documentSnapshot.getString("imageURL");

                        Map<String, Object> attendedEventsData = new HashMap<>();
                        List<Map<String, Object>> allAttendedEvents = new ArrayList<>();

                        // put info
                        Map<String, Object> attendedEvent = new HashMap<>();
                        attendedEvent.put("eventId", eventId);
                        attendedEvent.put("eventName", eventName);
                        attendedEvent.put("eventDate", eventDate);
                        attendedEvent.put("eventDescription", eventDescription);
                        attendedEvent.put("imageURL", imageURL);

                        // Add to list
                        allAttendedEvents.add(attendedEvent);
                        attendedEventsData.put("allAttendedEvents", allAttendedEvents);

                        // Create the document in AttendedEvents collection
                        db.collection("AttendedEvents").document(userId)
                                .set(attendedEventsData)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("QRCodeScanner", "New AttendedEvents document created for user: " + userId);
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("QRCodeScanner", "Error creating AttendedEvents document for user: " + userId, e);
                                });
                    } else {
                        Log.e("QRCodeScanner", "Event document not found for ID: " + eventId);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("QRCodeScanner", "Error fetching event details for event ID: " + eventId, e);
                });
    }

    private void updateAttendedEventsArray(FirebaseFirestore db, DocumentReference attendedEventsRef, String eventId) {
        db.collection("Events").document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String eventName = documentSnapshot.getString("eventName");
                        String eventDate = documentSnapshot.getString("eventDate");
                        String eventDescription = documentSnapshot.getString("eventDescription");
                        String imageURL = documentSnapshot.getString("imageURL");

                        // put info
                        Map<String, Object> attendedEvent = new HashMap<>();
                        attendedEvent.put("eventId", eventId);
                        attendedEvent.put("eventName", eventName);
                        attendedEvent.put("eventDate", eventDate);
                        attendedEvent.put("eventDescription", eventDescription);
                        attendedEvent.put("imageURL", imageURL);


                        // Check if the event already exists in list
                        List<Map<String, Object>> allAttendedEvents = (List<Map<String, Object>>) documentSnapshot.get("allAttendedEvents");
                        if (allAttendedEvents == null || !allAttendedEvents.contains(attendedEvent)) {
                            attendedEventsRef.update("allAttendedEvents", FieldValue.arrayUnion(attendedEvent))
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("QRCodeScanner", "Event added to AttendedEvents for user: " + attendedEventsRef.getId());
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("QRCodeScanner", "Error updating AttendedEvents for user: " + attendedEventsRef.getId(), e);
                                    });
                        } else {
                            Log.d("QRCodeScanner", "Event already exists in the array");
                        }
                    } else {
                        Log.e("QRCodeScanner", "Event document not found for ID: " + eventId);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("QRCodeScanner", "Error fetching event details for event ID: " + eventId, e);
                });
    }

    private String extractEventIdFromUrl(String qrContent) {
        // Direct event ID (simple case)
        if (qrContent != null && qrContent.matches("^[\\w-]+$")) {
            return qrContent;
        }
        // URL with event ID as a part of the path or query parameter
        try {
            Uri uri = Uri.parse(qrContent);
            List<String> segments = uri.getPathSegments();
            for (int i = 0; i < segments.size(); i++) {
                // Assuming the event ID follows a specific path segment (e.g., '/events/{eventId}')
                if ("events".equalsIgnoreCase(segments.get(i)) && i + 1 < segments.size()) {
                    return segments.get(i + 1);
                }
            }
            // Assuming the event ID might be a query parameter (e.g., '?eventId={eventId}')
            String eventIdQueryParam = uri.getQueryParameter("eventId");
            if (eventIdQueryParam != null && !eventIdQueryParam.isEmpty()) {
                return eventIdQueryParam;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void resetScanner() {
        // Hide the selected image view and clear any bitmap set to it
        selectedImageView.setVisibility(View.GONE);
        selectedImageView.setImageDrawable(null);
    }

    private void navigateToEventInfoPage(String eventId) {
        Intent data = new Intent();
        data.putExtra("eventId", eventId);
        setResult(RESULT_OK, data);
        finish();
    }

}

