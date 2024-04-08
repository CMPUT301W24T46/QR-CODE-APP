package com.example.eventapp.attendee;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.eventapp.R;

/**
 * The QrCodeRealTimeScanner class extends AppCompatActivity and provides functionality
 * for real-time QR code scanning within the EventApp application. This activity is designed
 * to utilize the camera to actively scan and decode QR codes, offering instant feedback and
 * actions based on the content encoded in the QR codes.
 * This class primarily sets up the user interface for the QR code scanning activity and initializes
 * any necessary components for real-time scanning, including camera access and QR code decoding
 * capabilities. The activity's layout is defined in R.layout.activity_qr_code_real_time_scanner.
 */

public class QrCodeRealTimeScanner extends AppCompatActivity {

    /**
     * Called when the activity is starting. This is where most initialization should go:
     * calling setContentView(int) to inflate the activity's UI, using findViewById(int) to
     * programmatically interact with widgets in the UI, setting up listeners, and so on.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being
     *                           shut down then this Bundle contains the data it most recently
     *                           supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_real_time_scanner);
    }


}