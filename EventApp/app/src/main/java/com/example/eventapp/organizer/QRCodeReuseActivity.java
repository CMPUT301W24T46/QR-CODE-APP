package com.example.eventapp.organizer;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.eventapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.integration.android.IntentIntegrator;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * An activity that allows scanning and reusing QR codes. It enables the user to either scan a QR code using the camera
 * or choose a QR code image from the gallery. Once a QR code is scanned or selected, it decodes the QR code to extract
 * information and uploads the QR code image to Firebase Storage. Finally, it saves the QR code details in Firestore.
 */

public class QRCodeReuseActivity extends AppCompatActivity {

    private static final String TAG = "QRCodeReuseActivity";
    private static final int REQUEST_CODE_GALLERY = 1001;
    private static final int REQUEST_CODE_PERMISSIONS = 10;

    private ImageView selectedImageView;
    private PreviewView previewView;
    private FusedLocationProviderClient fusedLocationClient;
    private String eventId;

    /**
     * Initializes the activity, sets the content view, and retrieves the event ID. It checks for camera permissions
     * and starts the camera if permissions are granted. Also, it animates a scanning line over the camera preview.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this
     *                           Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     *                           Note: Otherwise it is null.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_scanner);
        initializeViews();
        eventId = getIntent().getStringExtra("eventId");
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            requestCameraPermissions();
        }
        previewView.post(this::animateScanningLine);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    /**
     * Initializes view components like the selected image view, preview view, and buttons. Sets up onClick listeners
     * for the back and gallery buttons.
     */

    private void initializeViews() {
        selectedImageView = findViewById(R.id.selected_image_view);
        previewView = findViewById(R.id.scanner_previewView);
        ImageButton backButton = findViewById(R.id.backButton);
        ImageButton galleryButton = findViewById(R.id.galleryButton);

        backButton.setOnClickListener(v -> finish());
        galleryButton.setOnClickListener(v -> openGallery());
    }

    /**
     * Requests camera permissions from the user.
     */

    private void requestCameraPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_PERMISSIONS);
    }

    /**
     * Checks if all required permissions are granted.
     *
     * @return true if all permissions are granted, false otherwise.
     */

    private boolean allPermissionsGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Starts the camera and sets up image analysis for scanning QR codes.
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
                    try {
                        ByteBuffer buffer = imageProxy.getPlanes()[0].getBuffer();
                        byte[] data = new byte[buffer.capacity()];
                        buffer.get(data);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        decodeQRCodeFromBitmap(bitmap, qrCodeInfo -> {
                            if (qrCodeInfo != null) {
                                uploadAndSaveQRCode(bitmap, qrCodeInfo);
                                imageProxy.close();
                            }
                        });
                    } finally {
                        imageProxy.close();
                    }
                });

                preview.setSurfaceProvider(previewView.getSurfaceProvider());
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
            } catch (Exception e) {
                Toast.makeText(this, "Failed to start camera.", Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }


    /**
     * Handles the result of permission requests. If permissions are granted, the camera starts; otherwise, a toast
     * message is displayed and the activity finishes.
     *
     * @param requestCode  The request code passed in requestPermissions(android.app.Activity, String[], int).
     * @param permissions  The requested permissions.
     * @param grantResults The grant results for the corresponding permissions.
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS && allPermissionsGranted()) {
            startCamera();
        } else {
            Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Opens the device's gallery for the user to select a QR code image.
     */

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_GALLERY);
    }

    /**
     * Handles the result of the gallery intent. If a QR code image is selected, processes the image URI.
     *
     * @param requestCode The integer request code originally supplied to startActivityForResult(),
     *                    allowing you to identify who this result came from.
     * @param resultCode  The integer result code returned by the child activity through its setResult().
     * @param data        An Intent, which can return result data to the caller.
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQUEST_CODE_GALLERY || requestCode == IntentIntegrator.REQUEST_CODE) && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            processImageUri(imageUri);
        }
    }

    /**
     * Processes the selected image URI by decoding the QR code and uploading the details upon successful decoding.
     *
     * @param imageUri The URI of the selected image.
     */

    private void processImageUri(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            selectedImageView.setVisibility(View.VISIBLE);
            selectedImageView.setImageBitmap(bitmap);

            decodeQRCodeFromBitmap(bitmap, decodedInfo -> {
                if (decodedInfo != null) {
                    // Proceed with uploading the QR Code Image and saving details after successful decoding
                    uploadAndSaveQRCode(bitmap, decodedInfo);
                } else {
                    Toast.makeText(QRCodeReuseActivity.this, "Could not decode QR Code", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Decodes a QR code from a Bitmap and executes a callback with the decoded information.
     *
     * @param bitmap   The Bitmap of the QR code image.
     * @param callback A Consumer that accepts the decoded QR code information.
     */

    private void decodeQRCodeFromBitmap(Bitmap bitmap, Consumer<String> callback) {
        new Thread(() -> {
            try {
                int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
                bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
                RGBLuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), pixels);
                BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
                Result result = new MultiFormatReader().decode(binaryBitmap);
                runOnUiThread(() -> callback.accept(result.getText()));
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> callback.accept(null));
            }
        }).start();
    }

    /**
     * Uploads the QR code image to Firebase Storage and saves the QR code details in Firestore.
     *
     * @param bitmap     The Bitmap of the QR code image.
     * @param qrCodeInfo The decoded information from the QR code.
     */

    private void uploadAndSaveQRCode(Bitmap bitmap, String qrCodeInfo) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        String path = "qr_codes/" + UUID.randomUUID() + ".png";
        StorageReference qrCodeRef = FirebaseStorage.getInstance().getReference(path);

        UploadTask uploadTask = qrCodeRef.putBytes(data);
        uploadTask.addOnSuccessListener(taskSnapshot -> qrCodeRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String qrCodeImageUrl = uri.toString();
                    saveQRCodeDetails(eventId, "CheckIn", qrCodeImageUrl, qrCodeInfo);
                }))
                .addOnFailureListener(e -> Toast.makeText(QRCodeReuseActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    /**
     * Saves the QR code details to Firestore. If a QR code with the same information already exists, it deletes
     * the old QR code before saving the new one.
     *
     * @param eventId        The ID of the event associated with the QR code.
     * @param type           The type of QR code (e.g., "CheckIn").
     * @param qrCodeImageUrl The URL of the uploaded QR code image in Firebase Storage.
     * @param qrCodeInfo     The decoded information from the QR code.
     */

    private void saveQRCodeDetails(String eventId, String type, String qrCodeImageUrl, String qrCodeInfo) {
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
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "Existing QR code document deleted."))
                                .addOnFailureListener(e -> Log.e(TAG, "Error deleting existing QR code document", e));
                    }

                    // Proceed to add the new QR code info
                    Map<String, Object> qrCodeData = new HashMap<>();
                    qrCodeData.put("eventId", eventId);
                    qrCodeData.put("type", type);
                    qrCodeData.put("qrCodeUrl", qrCodeImageUrl);
                    qrCodeData.put("qrCodeInfo", qrCodeInfo);

                    db.collection("QRCode").add(qrCodeData)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(QRCodeReuseActivity.this, "Saved QR code info successfully!", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "QR code data saved successfully.");
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(QRCodeReuseActivity.this, "Error saving QR code info: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "Error saving QR code data", e);
                            });
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching QR code documents", e));
    }

    /**
     * Animates a line moving over the camera preview to simulate scanning.
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

}
