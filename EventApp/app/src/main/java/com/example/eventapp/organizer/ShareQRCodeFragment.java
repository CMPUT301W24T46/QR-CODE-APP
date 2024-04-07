package com.example.eventapp.organizer;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.example.eventapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public class ShareQRCodeFragment extends BottomSheetDialogFragment {
    private static String qrCodeUrl;
    private static String eventId;
    private static String qrCodeId;
    Button downloadButton, shareButton;

    /**
     * Creates a new instance of ShareQRCodeFragment with QR code URL and event ID.
     * @param qrCodeUrl The URL of the QR code to be shared.
     * @param eventId The ID of the event associated with the QR code.
     * @return A ShareQRCodeFragment instance with QR code URL and event ID passed as arguments.
     */
    public static ShareQRCodeFragment newInstance(String qrCodeUrl, String eventId) {
        ShareQRCodeFragment fragment = new ShareQRCodeFragment();
        Bundle args = new Bundle();
        args.putString("qrCodeUrl", qrCodeUrl);
        args.putString("eventId", eventId);
        args.putString("qrCodeId", qrCodeId);
        fragment.setArguments(args);
        return fragment;
    }
    //TODO:ADD OTHER SHARING METHOD
    /**
     * Initializes the fragment. Retrieves and stores QR code URL and event ID from the fragment's arguments.
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            qrCodeUrl = getArguments().getString("qrCodeUrl");
            eventId = getArguments().getString("eventId");
            qrCodeId = getArguments().getString("qrCodeId");
        }

    }

    /**
     * Inflates the layout for this fragment.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_share_qrcode, container, false);
    }

    /**
     * Sets up click listeners for the view components after the view has been created.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        downloadButton = view.findViewById(R.id.download_qrcode);
        shareButton = view.findViewById(R.id.share_qr_code);
        downloadButton.setOnClickListener(v -> {
            if (qrCodeUrl != null && !qrCodeUrl.isEmpty()) {
                downloadAndSaveQRCode(qrCodeUrl);
            } else {
                Toast.makeText(getContext(), "QR Code URL is not available.", Toast.LENGTH_SHORT).show();
            }
        });

        shareButton.setOnClickListener(v -> {
            if (qrCodeUrl != null && !qrCodeUrl.isEmpty()) {
                shareQRCode(qrCodeUrl);
            } else {
                Toast.makeText(getContext(), "QR Code URL is not available.", Toast.LENGTH_SHORT).show();
            }

        });

    }

    /**
     * Downloads a QR code image from the given URL and saves it to the device's gallery.
     * @param qrCodeUrl URL of the QR code image to download.
     */
    private void downloadAndSaveQRCode(String qrCodeUrl) {
        new Thread(() -> {
            try {
                InputStream in = new java.net.URL(qrCodeUrl).openStream();
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                getActivity().runOnUiThread(() -> saveImageToGallery(bitmap));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Saves the given bitmap image to the device's gallery with a unique file name that includes the event ID.
     * @param bitmap The bitmap image to be saved.
     */
    private void saveImageToGallery(Bitmap bitmap) {
        if (eventId == null || eventId.isEmpty()) {
            Log.e("ShareQRCodeFragment", "Event ID is null or empty. Cannot save image with event ID.");
            return;
        }

        String fileName = "QRCode_" + eventId + "_" + qrCodeId + "_" + System.currentTimeMillis();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.IS_PENDING, 1);
        }

        ContentResolver resolver = getActivity().getContentResolver();
        Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        if (uri == null) {
            Log.e("ShareQRCodeFragment", "Failed to create new MediaStore record.");
            return;
        }

        Log.d("ShareQRCodeFragment", "Image URI: " + uri.toString());

        try (OutputStream out = resolver.openOutputStream(uri)) {
            if (out == null) {
                Log.e("ShareQRCodeFragment", "Failed to open output stream for new MediaStore record.");
                return;
            }
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Image downloaded successfully!", Toast.LENGTH_SHORT).show());
        } catch (IOException e) {
            Log.e("ShareQRCodeFragment", "Error saving image to gallery", e);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.clear();
            values.put(MediaStore.Images.Media.IS_PENDING, 0);
            resolver.update(uri, values, null, null);
        }


    }

    private void shareQRCode(String qrCodeUrl) {
        new Thread(() -> {
            try {
                InputStream in = new java.net.URL(qrCodeUrl).openStream();
                Bitmap bitmap = BitmapFactory.decodeStream(in);

                String filename = qrCodeId != null ? "QRCode_" + qrCodeId + ".png" :
                        "QRCode" + qrCodeUrl.hashCode() + ".png";

                Uri imageUri = saveImageToCache(bitmap, filename);

                getActivity().runOnUiThread(() -> shareImage(imageUri));
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("ShareQRCodeFragment", "Error sharing QR code directly", e);
            }
        }).start();
    }

    private Uri saveImageToCache(Bitmap bitmap, String filename) {

        File cachePath = new File(getActivity().getCacheDir(), "images");
        cachePath.mkdirs();

        // Create the file to save the bitmap
        File imageFile = new File(cachePath, filename);
        try (FileOutputStream stream = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        } catch (IOException e) {
            Log.e("ShareQRCodeFragment", "Error saving image to cache", e);
            return null;
        }

        // Get the uri for the image file
        Uri imageUri = FileProvider.getUriForFile(
                getActivity(),
                getActivity().getPackageName() + ".provider",
                imageFile);

        return imageUri;
    }


    private void shareImage(Uri imageUri) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType("image/png");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share QR Code"));
    }


}