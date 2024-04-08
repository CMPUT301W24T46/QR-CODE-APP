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

public class QRCodeReuseActivity extends AppCompatActivity {

    private static final String TAG = "QRCodeReuseActivity";
    private static final int REQUEST_CODE_GALLERY = 1001;
    private static final int REQUEST_CODE_PERMISSIONS = 10;

    private ImageView selectedImageView;
    private PreviewView previewView;
    private FusedLocationProviderClient fusedLocationClient;
    private String eventId;

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

    private void initializeViews() {
        selectedImageView = findViewById(R.id.selected_image_view);
        previewView = findViewById(R.id.scanner_previewView);
        ImageButton backButton = findViewById(R.id.backButton);
        ImageButton galleryButton = findViewById(R.id.galleryButton);

        backButton.setOnClickListener(v -> finish());
        galleryButton.setOnClickListener(v -> openGallery());
    }

    private void requestCameraPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_PERMISSIONS);
    }

    private boolean allPermissionsGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
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

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQUEST_CODE_GALLERY || requestCode == IntentIntegrator.REQUEST_CODE) && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            processImageUri(imageUri);
        }
    }

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
