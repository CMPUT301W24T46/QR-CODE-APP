package com.example.eventapp.organizer;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventapp.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OrganizerQRCode extends Fragment {

    private ImageView qrCodeImageView;
    private String eventId;

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
        View view = inflater.inflate(R.layout.fragment_qrcode_info, container, false);
        qrCodeImageView = view.findViewById(R.id.organizer_qrcode_view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String eventId = bundle.getString("eventId");
            if (eventId != null) {
                generateQRCode(eventId);
                Log.d("OrganizerQRCode", "EventId" + eventId);
            }
        }
    }

    private void generateQRCode(String eventId) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(eventId, BarcodeFormat.QR_CODE, 600, 600);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

            getActivity().runOnUiThread(() -> qrCodeImageView.setImageBitmap(bitmap));

            // Convert bitmap to byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();

            // Save byte array to Firebase Storage
            String path = "qr_codes/" + eventId + ".png";
            StorageReference qrCodeRef = FirebaseStorage.getInstance().getReference(path);
            UploadTask uploadTask = qrCodeRef.putBytes(data);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                // Get the download URL and update Firestore
                qrCodeRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();
                    saveQrCodeUrlToFirestore(eventId, downloadUrl);
                });
            }).addOnFailureListener(e -> {
                // Handle unsuccessful uploads
                e.printStackTrace();
            });

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
    private void saveQrCodeUrlToFirestore(String eventId, String qrCodeUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> qrCodeMap = new HashMap<>();
        qrCodeMap.put("qrCodeUrl", qrCodeUrl);

        db.collection("Events").document(eventId).update(qrCodeMap)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "DocumentSnapshot successfully updated!"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error updating document", e));
    }
}
