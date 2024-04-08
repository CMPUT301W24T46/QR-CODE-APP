package com.example.eventapp.organizer;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.eventapp.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OrganizerQRCode_EventInfo extends Fragment {

    private ImageView qrCodeImageView;
    private String eventId;
    private String qrCodeUrl;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventId = getArguments().getString("eventId");
            Log.d("OrganizerQRCode","EventId" + eventId);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qrcode_eventinfo, container, false);
        qrCodeImageView = view.findViewById(R.id.organizer_qrcode_view_1);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String eventId = bundle.getString("eventId");
            if (eventId != null) {
                checkAndDisplayExistingQRCode(eventId);
            }
        }
        Button shareQRCodeButton = view.findViewById(R.id.btn_share_qrcode);
        shareQRCodeButton.setOnClickListener(v -> {
            if (qrCodeUrl != null && !qrCodeUrl.isEmpty()) {
                // Pass the QR code URL to the ShareQRCodeFragment
                ShareQRCodeFragment bottomSheet = ShareQRCodeFragment.newInstance(qrCodeUrl, eventId);
                bottomSheet.show(getParentFragmentManager(), bottomSheet.getTag());
            } else {
                // Handle the case where the QR code URL is not available yet
                Toast.makeText(getContext(), "Please wait, QR code is being prepared.", Toast.LENGTH_SHORT).show();
            }
        });
        Button regenerateQRCodeButton = view.findViewById(R.id.btn_regenerateQRCode_1);
        regenerateQRCodeButton.setOnClickListener(v -> {
            deleteExistingQRCode(eventId, () -> generateQRCode(eventId));
        });
    }

    private void checkAndDisplayExistingQRCode(String eventId) {
        FirebaseFirestore.getInstance().collection("QRCode")
                .whereEqualTo("eventId", eventId)
                .whereEqualTo("type", "EventInfo")
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // QR Code already exists, display it
                        String existingQRCodeUrl = queryDocumentSnapshots.getDocuments().get(0).getString("qrCodeUrl");
                        if (existingQRCodeUrl != null) {
                            qrCodeUrl = existingQRCodeUrl;
                            displayQRCodeImageByUrl(existingQRCodeUrl);
                        }
                    } else {
                        // No existing QR Code, generate a new one
                        generateQRCode(eventId);
                    }
                })
                .addOnFailureListener(e -> Log.e("OrganizerQRCode_EventInfo", "Error checking for existing QR code", e));
    }


    private void generateQRCode(String eventId) {
        String qrCodeId = UUID.randomUUID().toString(); // Generate unique QR code ID with 36 hex digit
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            String qrCodeContent = "{ \"eventId\": \"" + eventId + "\", \"qrCodeId\": \"" + qrCodeId + "\", \"type\": \"EventInfo\" }";
            BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeContent, BarcodeFormat.QR_CODE, 600, 600);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            getActivity().runOnUiThread(() -> qrCodeImageView.setImageBitmap(bitmap));
            saveQRCodeToFirebase(qrCodeId, eventId, bitmap);
            Toast.makeText(getContext(), "QR Code regenerated successfully.", Toast.LENGTH_SHORT).show();
        } catch (WriterException e) {
            Log.e("OrganizerQRCode_EventInfo", "Error generating QR code: ", e);
        }
    }
    private void saveQRCodeToFirebase(String qrCodeId, String eventId, Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();
        String path = "qr_codes/" + qrCodeId + ".png"; // Use QR code ID in path
        StorageReference qrCodeRef = FirebaseStorage.getInstance().getReference(path);
        qrCodeRef.putBytes(data).addOnSuccessListener(taskSnapshot -> {
            qrCodeRef.getDownloadUrl().addOnSuccessListener(uri -> {
                qrCodeUrl = uri.toString();
                saveQrCodeDetailsToFirestore(qrCodeId, eventId, qrCodeUrl);
            });
        }).addOnFailureListener(e -> Log.e("OrganizerQRCode_EventInfo", "Error saving QR code to Firebase Storage: ", e));
    }

    private void saveQrCodeDetailsToFirestore(String qrCodeId, String eventId, String qrCodeUrl) {
        Map<String, Object> qrCodeDetails = new HashMap<>();
        qrCodeDetails.put("eventId", eventId);
        qrCodeDetails.put("type", "EventInfo");
        qrCodeDetails.put("qrCodeUrl", qrCodeUrl);
        FirebaseFirestore.getInstance().collection("QRCode").document(qrCodeId)
                .set(qrCodeDetails)
                .addOnSuccessListener(aVoid -> Log.d("OrganizerQRCode_EventInfo", "QR code details saved successfully"))
                .addOnFailureListener(e -> Log.e("OrganizerQRCode_EventInfo", "Error saving QR code details to Firestore", e));
    }

    private void deleteExistingQRCode(String eventId, Runnable onSuccess) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("QRCode")
                .whereEqualTo("eventId", eventId)
                .whereEqualTo("type", "EventInfo")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String docId = queryDocumentSnapshots.getDocuments().get(0).getId();
                        String qrCodeUrlToDelete = queryDocumentSnapshots.getDocuments().get(0).getString("qrCodeUrl");

                        // Delete the document from Firestore
                        db.collection("QRCode").document(docId).delete();
                        String filePath = extractFilePathFromUrl(qrCodeUrlToDelete);
                        if (filePath != null) {
                            // Delete the file from Firebase Storage
                            FirebaseStorage.getInstance().getReference().child(filePath).delete()
                                    .addOnSuccessListener(aVoid -> onSuccess.run())
                                    .addOnFailureListener(e -> Log.e("OrganizerQRCode_EventInfo", "Error deleting QR code from Firebase Storage", e));
                        } else {
                            onSuccess.run();
                        }
                    } else {
                        onSuccess.run();
                    }
                })
                .addOnFailureListener(e -> Log.e("OrganizerQRCode_EventInfo", "Error finding existing QR code to delete", e));
    }

    private String extractFilePathFromUrl(String qrCodeUrl) {
        if (qrCodeUrl == null || qrCodeUrl.isEmpty()) {
            return null;
        }
        try {
            Uri uri = Uri.parse(qrCodeUrl);
            String path = uri.getPath();
            if (path != null) {
                int startIndex = path.indexOf("/o/") + 3;
                int endIndex = path.indexOf("?alt=media");
                if (startIndex != -1 && endIndex != -1) {
                    String filePath = path.substring(startIndex, endIndex);
                    return filePath.replace("%2F", "/");
                }
            }
        } catch (Exception e) {
            Log.e("OrganizerQRCode_EventInfo", "Error extracting file path from URL", e);
        }
        return null;
    }

    private void displayQRCodeImageByUrl(String imageUrl) {
        if (getContext() == null) return;
        Glide.with(getContext())
                .load(imageUrl)
                .into(qrCodeImageView);
    }
}