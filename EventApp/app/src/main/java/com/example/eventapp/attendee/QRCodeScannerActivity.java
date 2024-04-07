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
 * An activity that allows attendees to scan QR codes for event check-ins or retrieving event information.
 * It uses the device's camera to continuously scan for QR codes. Once a QR code is detected and successfully
 * decoded, it performs a check-in operation or navigates to event information based on the QR code content.
 *
 * The activity also supports scanning QR codes from images selected from the device's gallery.
 *
 * Permissions:
 * - Camera: Required for scanning QR codes using the device's camera.
 * - Location: Optional, used for geolocation-based check-ins if enabled by the user.
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

    /**
     * Sets up the activity, requesting necessary permissions and initializing the camera and other UI elements.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     *                           Otherwise, it is null.
     */


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
     * Requests camera permissions from the user.
     */

    private void requestCameraPermissions() {
        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
    }

    /**
     * Checks if all required permissions have been granted.
     *
     * @return true if all required permissions are granted, false otherwise.
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
     * Requests location permissions from the user to enable geolocation-based check-ins.
     */

    // Request location permissions
    private void requestLocationPermissions() {
        ActivityCompat.requestPermissions(this, LOCATION_PERMISSIONS, REQUEST_LOCATION_PERMISSIONS);
    }

    /**
     * Checks if all location permissions have been granted.
     *
     * @return true if all location permissions are granted, false otherwise.
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
     * Fetches the user's last known location and proceeds with the check-in process.
     * This method is called if location permissions are granted after a check-in was initiated.
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


    /**
     * Starts the camera and begins scanning for QR codes.
     */


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

    /**
     * Processes an {@link Image} from the camera feed, converts it into a {@link Bitmap}, and scans it for QR codes.
     * After converting the image to a bitmap, it invokes {@link #scanBitmapForQRCode(Bitmap, String, Double, Double)}
     * to look for QR codes within the image. Once the QR code is processed or if none is found, it closes the
     * {@link ImageProxy} to allow processing of the next image.
     *
     * @param image      The image captured from the camera feed.
     * @param imageProxy The proxy to allow for the closing of the image once it is processed.
     */

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
     * Updates the user's geolocation preference in the Firestore database. This method checks if the user is currently
     * authenticated. If so, it retrieves the user's ID and updates the 'isGeolocationEnabled' field in the user's document
     * within the 'Users' collection in Firestore. This method is useful for keeping track of user preferences regarding
     * the usage of geolocation features within the app.
     *
     * @param isGeolocationEnabled The new value of the user's geolocation preference to be updated in the database.
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
     * Handles the result of permission request callbacks.
     *
     * @param requestCode The integer request code originally supplied to requestPermissions(android.app.Activity, String[], int),
     *                    allowing you to identify who this result came from.
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
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
     * Attempts to retrieve the device's last known location and logs the latitude and longitude.
     * This method checks for location permissions before attempting to access the location. If permissions
     * are not granted, a toast message informs the user. If permissions are granted, it tries to fetch the
     * last known location. If successful, it logs the location; if not, it informs the user that the location
     * could not be retrieved.
     *
     * This method could be used in scenarios where an app needs to fetch the user's current location for
     * operations like check-ins or location-based filtering. The method ensures that all necessary permissions
     * are checked before accessing the device's location services to adhere to privacy guidelines.
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
     * Animates a scanning line to simulate the scanning process visually.
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
     * Opens the device's gallery for the user to select an image for QR code scanning.
     */

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_GALLERY);
    }

    /**
     * Handles activity result callbacks, specifically for image selection from the gallery.
     *
     * @param requestCode The integer request code originally supplied to startActivityForResult(Intent, int),
     *                    allowing you to identify who this result came from.
     * @param resultCode The integer result code returned by the child activity through its setResult().
     * @param data An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
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
     * Scans a given bitmap image for QR codes, attempts to decode any found QR code,
     * and then processes the QR code data by either checking it in Firestore or parsing it if needed.
     *
     * @param bitmap The bitmap image to scan for QR codes.
     * @param bitmapUri The URI of the bitmap image as a string, used for logging purposes.
     * @param latitude The latitude part of the location where the QR code is being scanned (can be null).
     * @param longitude The longitude part of the location where the QR code is being scanned (can be null).
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
     * Validates the decoded QR code data by checking its type and then either performing a check-in
     * or navigating to event information based on the QR code content.
     *
     * @param qrCodeId The ID extracted from the QR code, used to identify the QR code in Firestore.
     * @param eventId The ID of the event associated with the QR code.
     * @param type The type of action to be taken, e.g., "CheckIn" or "EventInfo".
     * @param latitude Optional latitude for geolocation-based check-ins.
     * @param longitude Optional longitude for geolocation-based check-ins.
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
     * Displays a message to the user indicating that the scanned QR code is invalid.
     */

    private void showInvalidQRCodeMessage() {
        Toast.makeText(this, "Invalid QR Code. Please try another.", Toast.LENGTH_LONG).show();
        if(selectedImageView != null) {
            selectedImageView.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Checks Firestore for the existence of the scanned QR code information. If the QR code
     * is found, proceeds with the check-in process. If not found, attempts to parse the QR code data.
     *
     * @param qrCodeInfo The information or data encoded within the QR code.
     * @param latitude The latitude where the QR code was scanned (for geolocation-based check-ins).
     * @param longitude The longitude where the QR code was scanned (for geolocation-based check-ins).
     * @param onNotFound A runnable to execute if the QR code information is not found in Firestore.
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
     * Checks in the user for an event based on the event ID. This method updates Firestore
     * with the user's check-in information, including geolocation if provided.
     *
     * @param eventId The ID of the event to check the user into.
     * @param latitude Optional latitude for geolocation-based check-ins.
     * @param longitude Optional longitude for geolocation-based check-ins.
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
     * Creates a new document in the "AttendedEvents" collection for a user, adding the current event
     * to their attended events list.
     *
     * @param userId The ID of the user attending the event.
     * @param db The instance of the Firestore database.
     * @param eventId The ID of the event being attended.
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
     * Updates an existing "AttendedEvents" document for a user with a new event,
     * adding it to their list of attended events.
     *
     * @param db The instance of the Firestore database.
     * @param attendedEventsRef A reference to the user's "AttendedEvents" document in Firestore.
     * @param eventId The ID of the event being added to the user's attended events.
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
     * Resets the QR code scanner by hiding the image view used to display the selected image
     * and clearing any bitmap set to it.
     */

    private void resetScanner() {
        // Hide the selected image view and clear any bitmap set to it
        selectedImageView.setVisibility(View.GONE);
        selectedImageView.setImageDrawable(null);
    }

    /**
     * Navigates to the event information page for a given event ID.
     *
     * @param eventId The ID of the event for which information is to be displayed.
     */

    private void navigateToEventInfoPage(String eventId) {
        Intent data = new Intent();
        data.putExtra("eventId", eventId);
        setResult(RESULT_OK, data);
        finish();
    }

}

