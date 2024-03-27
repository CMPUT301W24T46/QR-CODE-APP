package com.example.eventapp.attendee;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;

import com.example.eventapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

                preview.setSurfaceProvider(previewView.getSurfaceProvider());
                cameraProvider.bindToLifecycle(this, cameraSelector, preview);
            } catch (Exception e) {
                Toast.makeText(this, "Failed to start camera.", Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
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
                    requestLocationPermissions(); // This method now also needs to handle the bitmap and URI after permissions are granted
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
            String qrContent = result.getText();

            // Log the QR code content or extracted event ID
            String eventId = extractEventIdFromUrl(qrContent); // Assuming this method extracts the event ID
            if (eventId != null) {
                Log.d("QRCodeScanner", "Extracted Event ID: " + eventId + " from Bitmap URI: " + bitmapUri);
                checkInUser(eventId, bitmapUri, latitude, longitude);
            } else {
                // QR code no event id
                Log.d("QRCodeScanner", "No valid event ID found in QR code.");
                Toast.makeText(this, "QR Code does not contain a valid event ID.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            // No QR code image
            Log.d("QRCodeScanner", "Failed to decode QR code from Bitmap URI: " + bitmapUri, e);
            Toast.makeText(this, "No QR Code found. Please try another image.", Toast.LENGTH_LONG).show();
        }
    }

    private void checkInUser(String eventId, String bitmapUri, double latitude, double longitude) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            DocumentReference checkInDocRef = db.collection("Events").document(eventId)
                    .collection("CheckIns").document(userId);

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
                checkInData.put("attendee ID", userId);
                checkInData.put("checkInTime", FieldValue.serverTimestamp());
                checkInData.put("checkInTimes", checkInTimes);
                checkInData.put("location", new GeoPoint(latitude, longitude));
                // Update the document with the new data
                transaction.set(checkInDocRef, checkInData);
                return null; // To satisfy the Transaction.Function interface
            }).addOnSuccessListener(aVoid -> {
                Log.d("QRCodeScanner", "User " + userId + " checked in successfully for event " + eventId);
                Toast.makeText(QRCodeScannerActivity.this, "Check-in successful!", Toast.LENGTH_SHORT).show();
                Log.d("QRCodeScanner", "Scanned Bitmap URI: " + bitmapUri);
                resetScanner();
                finish(); // Close the activity
            }).addOnFailureListener(e -> {
                Log.e("QRCodeScanner", "Check-in failed for user " + userId + " at event " + eventId, e);
                Toast.makeText(QRCodeScannerActivity.this, "Check-in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            Toast.makeText(this, "User ID is null, cannot check in.", Toast.LENGTH_SHORT).show();
        }
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
}

