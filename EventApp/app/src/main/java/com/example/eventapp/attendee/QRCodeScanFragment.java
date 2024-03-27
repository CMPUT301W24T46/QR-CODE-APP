package com.example.eventapp.attendee;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.eventapp.R;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.concurrent.ExecutionException;

public class QRCodeScanFragment extends Fragment {

    private static final int REQUEST_CODE_CAMERA = 1;
    private static final int REQUEST_CODE_SCAN = 2;
    private PreviewView previewView;
    private Camera camera;
    private CameraSelector cameraSelector;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qr_code_scan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button scanButton = view.findViewById(R.id.btn_scan);
        scanButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(getContext(), QRCodeScannerActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SCAN);
            } else {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_CAMERA && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, start the QRCodeScannerActivity
            startActivity(new Intent(getContext(), QRCodeScannerActivity.class));
        } else {
            Toast.makeText(getContext(), "Camera permission is needed to scan QR codes", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            String eventId = data.getStringExtra("eventId");

            // Create a bundle to pass the eventId
            Bundle bundle = new Bundle();
            bundle.putString("eventId", eventId);

            // Perform navigation with NavController and action ID
            NavController navController = Navigation.findNavController(getView());
            navController.navigate(R.id.action_attendeeQRCodeScan_to_attendeeEventInformation, bundle);
        }
    }
}