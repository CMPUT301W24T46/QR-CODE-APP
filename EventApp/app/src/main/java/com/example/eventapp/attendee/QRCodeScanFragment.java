package com.example.eventapp.attendee;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * QRCodeScanFragment is a Fragment subclass responsible for handling QR code scanning functionality within the app.
 * It integrates camera permissions, location services, and QR code scanning logic to provide a seamless experience for users
 * attempting to scan QR codes for event check-ins or accessing event information.
 *
 * Features include requesting necessary permissions, activating the device's camera for QR scanning, and handling the scanned
 * QR code data to check in users to events or navigate to event information pages.
 */

public class QRCodeScanFragment extends Fragment {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final int REQUEST_CODE_CAMERA = 1;
    private static final int REQUEST_CODE_SCAN = 2;
    private PreviewView previewView;
    private Camera camera;

    private Double longitude = null;
    private Double latitude = null ;
    private FusedLocationProviderClient fusedLocationClient;
    private Button buttonScan;
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

        buttonScan = view.findViewById(R.id.testButton);
        buttonScan.setOnClickListener(v -> {

            String uid = FirebaseAuth.getInstance().getUid();

            if(uid == null){
                IntentIntegrator.forSupportFragment(QRCodeScanFragment.this)
                    .setCaptureActivity(CaptureActivity.class)
                    .initiateScan();
            }else{
                getLastLocationAndCheckIn();
            }
        });

        requestLocationPermission() ;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity()) ;
    }


    private void requestLocationPermission() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted, proceed with location retrieval
//                getLastLocationAndCheckIn();
            } else {
                // Permission denied
                Toast.makeText(getContext(), "Location permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK && data != null) {
//            String eventId = data.getStringExtra("eventId");
//            if (eventId != null && !eventId.isEmpty()) {
//
//                Bundle bundle = new Bundle();
//                bundle.putString("eventId", eventId);
//
//                // Perform navigation with NavController and action ID
//                NavController navController = Navigation.findNavController(getView());
//                navController.navigate(R.id.action_attendeeQRCodeScan_to_noCheckInInfo, bundle);
//            }
//        }
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(FirebaseAuth.getInstance().getUid() == null){
            Log.d("Enteredfor" , "Testing") ;
            return ;
        }
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                // Parse the QR code data
                String qrCodeData = result.getContents() ;
                JSONObject qrData = null;
                String qrCodeId ;
                String eventId ;
                String type ;


                try {
                    qrData = new JSONObject(qrCodeData);
                    qrCodeId = qrData.getString("qrCodeId");
                    eventId = qrData.getString("eventId");
                    type = qrData.getString("type");

                    if(type.equals("EventInfo")){
                        Bundle bundle = new Bundle();
                        bundle.putString("eventId", eventId);
                        // Perform navigation with NavController and action ID
                        NavController navController = Navigation.findNavController(getView());
                        navController.navigate(R.id.action_attendeeQRCodeScan_to_noCheckInInfo, bundle);
                    }else if(type.equals("CheckIn")){
                        Log.d("Check In" , "Time to CheckIn") ;
                        checkInUser(eventId , latitude , longitude);
                        Log.d("Location " ,  String.valueOf(latitude) + " " + String.valueOf(longitude));
                    }
                } catch (JSONException e) {
                    Log.d("Promotion Code" , qrCodeData) ;
                    checkQRCodeInFirestore(qrCodeData , latitude , longitude ) ;
                }

//                try {
//                    qrCodeId = qrData.getString("qrCodeId");
//                    eventId = qrData.getString("eventId");
//                    type = qrData.getString("type");
//
//                    if(type.equals("EventInfo")){
//                        Bundle bundle = new Bundle();
//                        bundle.putString("eventId", eventId);
//                        // Perform navigation with NavController and action ID
//                        NavController navController = Navigation.findNavController(getView());
//                        navController.navigate(R.id.action_attendeeQRCodeScan_to_noCheckInInfo, bundle);
//                    }else if(type.equals("CheckIn")){
//                        Log.d("Check In" , "Time to CheckIn") ;
//                        checkInUser(eventId , latitude , longitude);
//                        Log.d("Location " ,  String.valueOf(latitude) + " " + String.valueOf(longitude));
//                    }
//                } catch (JSONException e) {
//                    throw new RuntimeException(e);
//                }
                Log.d("Scanned Event" , result.getContents()) ;
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void checkQRCodeInFirestore(String qrCodeInfo, Double latitude, Double longitude) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("QRCode")
                .whereEqualTo("qrCodeInfo", qrCodeInfo)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Found matching QR code info, proceed with event check-in
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        String eventId = document.getString("eventId");
                        Log.d("Event in firestore" ,"Test");
                        if (eventId != null) {
                            checkInUser(eventId, latitude, longitude);
                        }
                    } else {
                        // No direct match found, try JSON parsing
//                        onNotFound.run();
                        Log.d("Event Not in firestore" ,"Test");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("QRCodeScanner", "Error fetching QR code data from Firestore", e);
//                    onNotFound.run();
                });
    }

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
//                Toast.makeText(QRCodeScannerActivity.this, "Check-in successful!", Toast.LENGTH_SHORT).show();

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
//                Toast.makeText(QRCodeScannerActivity.this, "Check-in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
//            Toast.makeText(this, "User ID is null, cannot check in.", Toast.LENGTH_SHORT).show();
        }
    }

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

    private void getLastLocationAndCheckIn() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference userRef = db.collection("Users").document(userId);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                Boolean isGeolocationEnabled = document.contains("isGeolocationEnabled") && Boolean.TRUE.equals(document.getBoolean("isGeolocationEnabled"));

                // Check if geolocation is enabled and permissions are granted
                if (isGeolocationEnabled && allLocationPermissionsGranted()) {
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        fusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), location -> {
                            if (location != null) {
                                // Location is available, proceed with it
                                longitude = location.getLongitude() ;
                                latitude = location.getLatitude() ;
                                IntentIntegrator.forSupportFragment(QRCodeScanFragment.this)
                                        .setCaptureActivity(CaptureActivity.class)
                                        .initiateScan();
//                                scanBitmapForQRCode(bitmap, bitmapUri, location.getLatitude(), location.getLongitude());
                            } else {
                                // Location is unavailable, show a message and proceed without it
                                Toast.makeText(getContext(), "Unable to retrieve location. Please ensure your location is on.", Toast.LENGTH_LONG).show();
                                IntentIntegrator.forSupportFragment(QRCodeScanFragment.this)
                                        .setCaptureActivity(CaptureActivity.class)
                                        .initiateScan();
//                                scanBitmapForQRCode(bitmap, bitmapUri, null, null);
                            }
                        });
                    }
                } else {
                    // Geolocation is disabled or permission not granted, proceed without location
                    IntentIntegrator.forSupportFragment(QRCodeScanFragment.this)
                            .setCaptureActivity(CaptureActivity.class)
                            .initiateScan();
//                    scanBitmapForQRCode(bitmap, bitmapUri, null, null);
                }
            } else {
                Log.e("QRScanner", "Failed to fetch user preferences.");
                // Fail to fetch user preferences, proceed without location
                IntentIntegrator.forSupportFragment(QRCodeScanFragment.this)
                        .setCaptureActivity(CaptureActivity.class)
                        .initiateScan();
//                scanBitmapForQRCode(bitmap, bitmapUri, null, null);
            }
        }).addOnFailureListener(e -> {
            Log.e("QRScanner", "Error accessing Firestore.", e);
            // Firestore failure, proceed without location
//            scanBitmapForQRCode(bitmap, bitmapUri, null, null);
            IntentIntegrator.forSupportFragment(QRCodeScanFragment.this)
                    .setCaptureActivity(CaptureActivity.class)
                    .initiateScan();
        });
    }

    private boolean allLocationPermissionsGranted() {
        // Assuming this method checks for both FINE and COARSE location permissions
        // You'll need to implement the logic to verify if the permissions are granted
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

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

    private void navigateToEventInfoPage(String eventId) {
        Bundle bundle = new Bundle();
        bundle.putString("eventId", eventId);
        // Perform navigation with NavController and action ID
        NavController navController = Navigation.findNavController(getView());
        navController.navigate(R.id.action_attendeeQRCodeScan_to_noCheckInInfo, bundle);
    }

    private void showInvalidQRCodeMessage() {
        Toast.makeText(getContext(), "Invalid QR Code. Please try another.", Toast.LENGTH_LONG).show();
    }

}