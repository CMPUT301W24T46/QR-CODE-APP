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

/**
 * QRCodeScannerActivity handles the QR code scanning functionality within the app.
 * It provides the interface for users to scan QR codes using the device camera or choose an image from the gallery.
 * The activity also integrates with Firebase Firestore to validate QR codes and check in users to events.
 * It requests necessary permissions for camera and location access to ensure a seamless scanning experience.
 * Upon successful QR code recognition, the activity processes the QR code data for event check-ins or navigating to event information.
 */

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

    private NavController navController;

    private boolean isCheckInPending = false;
    private Uri pendingCheckInUri = null; // Store the URI of the pending check-in


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
            requestLocationPermissions();
        }
        previewView.post(this::animateScanningLine);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    /**
     * Requests permissions required for camera access if they haven't been granted already.
     */

    private void requestCameraPermissions() {
        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
    }

    /**
     * Checks if all necessary permissions for camera access have been granted.
     *
     * @return {@code true} if all permissions are granted, {@code false} otherwise.
     */

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * Requests permissions required for accessing the device's location.
     */

    // Request location permissions
    private void requestLocationPermissions() {
        ActivityCompat.requestPermissions(this, LOCATION_PERMISSIONS, REQUEST_LOCATION_PERMISSIONS);
    }

    /**
     * Checks if all necessary location permissions have been granted to the application.
     * This method iterates through the {@code LOCATION_PERMISSIONS} array and uses
     * {@code ContextCompat.checkSelfPermission} to verify if each permission has been granted.
     *
     * @return {@code true} if all required location permissions are granted, {@code false} otherwise.
     */

    private boolean allLocationPermissionsGranted() {
        for (String permission : LOCATION_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

//    private void getLastLocationAndCheckIn(Bitmap bitmap, String bitmapUri) {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Ensure you have the user's ID
//        DocumentReference userRef = db.collection("Users").document(userId);
//
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // Handle permission denial gracefully
//            Toast.makeText(this, "Location permission is needed to check in.", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        fusedLocationClient.getLastLocation()
//                .addOnSuccessListener(this, location -> {
//                    // Got last known location, it could be null
//                    if (location != null) {
//                        // Proceed to scan the bitmap for the QR code and check in
//                        scanBitmapForQRCode(bitmap, bitmapUri, location.getLatitude(), location.getLongitude());
//                    } else {
//                        Toast.makeText(this, "Unable to retrieve location. Please ensure your location is on.", Toast.LENGTH_LONG).show();
//                    }
//                });
//    }

    /**
     * Attempts to retrieve the last known location of the user and uses it to check in by scanning a QR code from a bitmap.
     * This method first checks the user's preferences for geolocation in the Firestore database. If geolocation is enabled and
     * the necessary location permissions have been granted, it fetches the last known location using {@code FusedLocationProviderClient}.
     * If the location is available, it proceeds to scan the QR code from the provided bitmap and URI, including the location data.
     * If the location is not available or permissions are not granted, it attempts to scan the QR code without location data.
     * In case of failure to fetch user preferences or Firestore access issues, it also proceeds to scan the QR code without location data.
     *
     * @param bitmap The bitmap containing the QR code to be scanned.
     * @param bitmapUri The URI of the bitmap, used as an alternative identifier for the bitmap.
     */

    private void getLastLocationAndCheckIn(Bitmap bitmap, String bitmapUri) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference userRef = db.collection("Users").document(userId);


        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                Boolean isGeolocationEnabled = document.contains("isGeolocationEnabled") && Boolean.TRUE.equals(document.getBoolean("isGeolocationEnabled"));

                // Get check in location if geolocation is enabled and permissions are granted
                if (isGeolocationEnabled && allLocationPermissionsGranted()) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                            if (location != null) {
                                scanBitmapForQRCode(bitmap, bitmapUri, location.getLatitude(), location.getLongitude());
                            } else {
                                Toast.makeText(this, "Unable to retrieve location. Please ensure your location is on.", Toast.LENGTH_LONG).show();
                                // Location is unavailable, proceed without it
                                scanBitmapForQRCode(bitmap, bitmapUri, null, null);
                            }
                        });
                    }
                } else {
                    // Geolocation is disabled
                    scanBitmapForQRCode(bitmap, bitmapUri, null, null);
                }
            } else {
                Log.e("QRScanner", "Failed to fetch user preferences.");
                // Fail to fetch user preferences
                scanBitmapForQRCode(bitmap, bitmapUri, null, null);
            }
        }).addOnFailureListener(e -> {
            Log.e("QRScanner", "Error accessing Firestore.", e);
            // FireStore failure
            scanBitmapForQRCode(bitmap, bitmapUri,null, null);
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
        if (bitmap != null) {
            scanBitmapForQRCode(bitmap, null, 0.0, 0.0);
        }
        imageProxy.close();
    }

    /**
     * Updates the geolocation preference for the current user in the Firestore database.
     * This method checks if a Firebase user is currently authenticated. If a user is authenticated,
     * it retrieves the user's unique ID and uses it to access the user's document in the Firestore database.
     * The method then updates the 'isGeolocationEnabled' field in the user's document to reflect the new preference
     * passed as a parameter to the method. It logs a success message upon successful update and logs an error
     * message if the update fails.
     *
     * @param isGeolocationEnabled The new preference value for geolocation; true to enable geolocation, false to disable it.
     */

    private void updateUserGeolocationPreference(boolean isGeolocationEnabled) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();
            DocumentReference userRef = db.collection("Users").document(userId);

            // Update the isGeolocationEnabled field for the user
            userRef.update("isGeolocationEnabled", isGeolocationEnabled)
                    .addOnSuccessListener(aVoid -> Log.d("QRCodeScanner", "User geolocation preference updated."))
                    .addOnFailureListener(e -> Log.e("QRCodeScanner", "Error updating user geolocation preference.", e));
        }
    }

    /**
     * Handles the result of the request for permissions. Specifically, it deals with the result of the location permissions request.
     * If location permissions are granted, it updates the user's geolocation preference, attempts to fetch the last known location and proceeds with pending actions.
     * If a check-in was pending and the permissions are now granted, it attempts to perform the check-in using the last known location.
     * If the permissions are denied, it proceeds with the check-in without location data, if a check-in was pending.
     * After handling the permissions result, it resets any pending check-in flags and URIs.
     *
     * @param requestCode  The request code passed in {@code requestPermissions(android.app.Activity, String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions which is either {@code PackageManager.PERMISSION_GRANTED} or {@code PackageManager.PERMISSION_DENIED}. Never null.
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSIONS) {
            if (allLocationPermissionsGranted()) {
                updateUserGeolocationPreference(true);
                // If location permission is now granted and a check-in was pending, proceed
                fetchLastLocationAndProceed();
                if (isCheckInPending && pendingCheckInUri != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), pendingCheckInUri);
                        getLastLocationAndCheckIn(bitmap, pendingCheckInUri.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                // If permission is denied but a check-in was pending, proceed without location
                if (isCheckInPending && pendingCheckInUri != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), pendingCheckInUri);
                        scanBitmapForQRCode(bitmap, pendingCheckInUri.toString(), null, null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            // Reset the flag and URI
            isCheckInPending = false;
            pendingCheckInUri = null;
        }
    }

    /**
     * Fetches the last known location of the device. If the location permissions are not granted,
     * it shows a toast message indicating that location permission is denied. If the location can be retrieved,
     * it logs the location's latitude and longitude. If the location cannot be retrieved, it shows a toast message indicating the failure.
     */

    private void fetchLastLocationAndProceed() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Safety check if the permissions are not granted at this point
            Toast.makeText(this, "Location permission is denied.", Toast.LENGTH_LONG).show();
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

    /**
     * Animates a scanning line view by translating it vertically across the preview view. The animation repeats indefinitely
     * until the scanning line reaches the bottom of the preview view, at which point it resets to the top and starts again.
     * This creates a visual effect of continuous scanning.
     */

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

    /**
     * Launches the device's gallery application for the user to select an image.
     * The selected image can then be used for further processing such as scanning for QR codes.
     * This method constructs an intent for image selection and starts an activity for result, expecting the user to pick an image from the gallery.
     */

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_GALLERY);
    }

    /**
     * Handles the result from various activities started for result, specifically from the gallery selection and the custom QR code scanner.
     * If the result is from the gallery selection or QR code scanner and is successful, it processes the selected image.
     * The method checks for location permissions before proceeding with any location-dependent actions.
     * If permissions are not granted, it requests them. If permissions are granted, it attempts to get the last known location and proceed with check-in.
     * In case of any failure in loading the image from the URI, it logs the error and shows a toast message.
     *
     * @param requestCode The integer request code originally supplied to startActivityForResult(), allowing to identify who this result came from.
     * @param resultCode  The integer result code returned by the child activity through its setResult().
     * @param data        An Intent, which can return result data to the caller. Can be {@code null}.
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQUEST_CODE_GALLERY || requestCode == IntentIntegrator.REQUEST_CODE) && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                selectedImageView.setVisibility(View.VISIBLE);
                selectedImageView.setImageBitmap(bitmap);

                // Check for location permission before proceeding
                if (!allLocationPermissionsGranted()) {
                    isCheckInPending = true;
                    pendingCheckInUri = imageUri;
                    requestLocationPermissions();
                } else {
                    // If permission is already granted, proceed with check-in
                    getLastLocationAndCheckIn(bitmap, imageUri.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Scans a given bitmap for a QR code and attempts to decode it. If a QR code is found and decoded successfully,
     * it attempts to handle the decoded data by either checking it directly in Firestore or parsing it as JSON for further processing.
     * In case the QR code data matches a specific format, additional actions are taken, such as checking in the user or navigating to event information.
     * If the QR code cannot be processed or no QR code is found, a message is displayed to the user indicating an invalid QR code.
     *
     * @param bitmap     The bitmap image to scan for QR codes.
     * @param bitmapUri  The URI of the bitmap image, used for reference.
     * @param latitude   The latitude component of the user's current location, used for check-ins that require geolocation. Can be {@code null}.
     * @param longitude  The longitude component of the user's current location, used for check-ins that require geolocation. Can be {@code null}.
     */

    private void scanBitmapForQRCode(Bitmap bitmap, String bitmapUri, Double latitude, Double longitude) {
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
                } catch (JSONException e) {
                    Toast.makeText(QRCodeScannerActivity.this, "Invalid QR code format.", Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Log.e("QRCodeScanner", "Error in QR code data processing", e);
            showInvalidQRCodeMessage();
        }
    }

    /**
     * Validates the decoded QR code data and performs actions based on its contents.
     * If the QR code contains event check-in information, it attempts to check in the user.
     * If it contains event information, it navigates to the event information page.
     * In case the QR code data does not match the expected structure or values, it notifies the user of an invalid QR code.
     *
     * @param qrCodeId   The ID extracted from the QR code, if available.
     * @param eventId    The event ID extracted from the QR code, used for check-in or displaying event information.
     * @param type       The type of QR code, indicating the action to be taken (e.g., check-in or event info).
     * @param latitude   The latitude component of the user's current location, used for check-ins that require geolocation. Can be {@code null}.
     * @param longitude  The longitude component of the user's current location, used for check-ins that require geolocation. Can be {@code null}.
     */

    private void validateQRCode(String qrCodeId, String eventId, String type, Double latitude, Double longitude) {

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

    /**
     * Displays a message to the user indicating that the scanned QR code is invalid or could not be processed.
     * This method is called when the QR code scanning process fails or the QR code data does not match expected formats or values.
     */

    private void showInvalidQRCodeMessage() {
        Toast.makeText(this, "Invalid QR Code. Please try another.", Toast.LENGTH_LONG).show();
        if(selectedImageView != null) {
            selectedImageView.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Checks the provided QR code information in Firestore to find a matching document.
     * If a matching document is found, it proceeds with the check-in process for the associated event.
     * If no matching document is found, it attempts alternative processing or notifies the user.
     *
     * @param qrCodeInfo The information encoded in the QR code to be checked in Firestore.
     * @param latitude   The latitude component of the user's current location, used for check-ins that require geolocation. Can be {@code null}.
     * @param longitude  The longitude component of the user's current location, used for check-ins that require geolocation. Can be {@code null}.
     * @param onNotFound A runnable to be executed if no matching QR code information is found in Firestore. Can be used for further processing or error handling.
     */

    private void checkQRCodeInFirestore(String qrCodeInfo, Double latitude, Double longitude, Runnable onNotFound) {
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

    /**
     * Facilitates the check-in process for a user at an event identified by the provided event ID.
     * This method performs several key operations as part of the check-in process:
     * 1. It verifies that the current user is authenticated through Firebase Authentication.
     * 2. It retrieves or creates a document in the 'AttendedEvents' collection specific to the user to track their event attendance history.
     * 3. It generates a new document within the 'CheckIns' sub-collection of the specified event to record the user's check-in.
     * 4. It optionally includes the user's current geolocation as part of the check-in if available.
     * 5. Upon successful check-in, it triggers the generation of a milestone message to notify the event's organizer and potentially other attendees.
     *    This milestone message is based on the user's name, the event they checked into, and, if applicable, the achievement of a specific attendee count milestone.
     *
     * The method also handles various failure scenarios, logging errors and notifying the user as appropriate.
     *
     * @param eventId   The unique identifier of the event the user is checking into.
     * @param latitude  The latitude part of the user's current location, used to geotag the check-in. Can be {@code null} if location data is unavailable or permissions are not granted.
     * @param longitude The longitude part of the user's current location, used to geotag the check-in. Can be {@code null} if location data is unavailable or permissions are not granted.
     */

    private void checkInUser(String eventId, Double latitude, Double longitude) {
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
                    if (latitude != null && longitude != null) {
                        checkInData.put("checkInLocation", new GeoPoint(latitude, longitude));
                    }
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

    /**
     * Creates a new document in the 'AttendedEvents' collection for a user to record their attendance at a specific event.
     * This method is invoked when a user checks in at an event for which they do not have an existing attendance record.
     *
     * The method performs the following operations:
     * 1. Fetches event details from the 'Events' collection using the provided event ID.
     * 2. Constructs a data structure representing the attended event, including event name, date, description, and an image URL.
     * 3. Adds this event data to a list of attended events for the user.
     * 4. Creates a new document in the 'AttendedEvents' collection with the user's ID as the document ID, and the list of attended events as the content.
     *
     * Success and failure of the operation are logged, and appropriate actions are taken to notify the user or handle errors.
     *
     * @param userId  The unique identifier of the user who is checking in at the event.
     * @param db      An instance of {@link FirebaseFirestore}, used to access the Firestore database.
     * @param eventId The unique identifier of the event the user is attending, used to fetch event details from the database.
     */

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

    /**
     * Updates the array of attended events for a user in the 'AttendedEvents' collection.
     * This method fetches event details from the 'Events' collection using the given event ID
     * and updates the user's attended events array with the new event if it's not already present.
     *
     * The method operates as follows:
     * 1. Retrieves event details from the 'Events' collection based on the provided event ID.
     * 2. Constructs a map with the event details including event name, date, description, and an image URL.
     * 3. Checks if the event is already present in the user's attended events array to prevent duplication.
     * 4. If the event is not present, it adds the event to the 'allAttendedEvents' array in the user's document.
     *
     * It logs success and failure of the operation and takes appropriate actions accordingly.
     *
     * @param db                An instance of {@link FirebaseFirestore}, used to access the Firestore database.
     * @param attendedEventsRef A {@link DocumentReference} pointing to the user's document in the 'AttendedEvents' collection.
     * @param eventId           The unique identifier of the event the user is attending, used to fetch event details.
     */

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

    /**
     * Resets the QR code scanner by hiding and clearing the selected image view.
     * This method is called after a QR code has been successfully scanned or when
     * the scanning process needs to be reset for any reason. It ensures the image view
     * used to display the scanned QR code or selected image from the gallery is cleared
     * and made invisible, preparing the scanner for the next operation.
     */

    private void resetScanner() {
        // Hide the selected image view and clear any bitmap set to it
        selectedImageView.setVisibility(View.GONE);
        selectedImageView.setImageDrawable(null);
    }

    /**
     * Navigates to the event information page after a successful QR code scan.
     * This method is used to pass the event ID to another activity or fragment where
     * detailed information about the event can be displayed. It sets the result for
     * the current activity before finishing it, allowing data to be passed back to
     * the calling activity.
     *
     * @param eventId The unique identifier of the event to display information for,
     *                obtained from the scanned QR code.
     */

    private void navigateToEventInfoPage(String eventId) {
        Intent data = new Intent();
        data.putExtra("eventId", eventId);
        setResult(RESULT_OK, data);
        finish();
    }

}

