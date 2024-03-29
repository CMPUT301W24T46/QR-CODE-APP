package com.example.eventapp.organizer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.eventapp.R;
import com.example.eventapp.event.Event;
import com.example.eventapp.event.EventDB;
import com.example.eventapp.notification.Notification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import com.google.firebase.Timestamp;
import com.example.eventapp.notification.NotificationDB;
import java.text.SimpleDateFormat;
import java.util.Date;



public class CreateNotificationFragment extends DialogFragment {

    private TextView notificationTitleEditText;
    private EditText notificationDescriptionEditText;
    private ArrayList<Event> events = new ArrayList<>(); // Initialize events list
    private EventDB eventDB;
    private FirebaseFirestore db;
    private String eventId;

    interface CreateNotificationListener {
        void onNotificationCreated();
    }

    private CreateNotificationListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (CreateNotificationListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement CreateNotificationListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_create_notification, null);
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        notificationTitleEditText = view.findViewById(R.id.CreateAnnouncementTitle);
        notificationDescriptionEditText = view.findViewById(R.id.EditEventDescription);
        Button backButton = view.findViewById(R.id.buttonArrow);
        backButton.setOnClickListener(v -> dismiss());

        Button confirmButton = view.findViewById(R.id.buttonConfirm);
        confirmButton.setOnClickListener(v -> createNotification());


        builder.setView(view);

        // Initialize EventDB instance
        eventDB = new EventDB(FirebaseFirestore.getInstance());

        // Fetch events from firebase
        eventDB.getAllEventsForUser(currentUserId, new EventDB.EventRetrievalListener() {
            @Override
            public void onEventsRetrieved(List<Event> eventList) {
                events.clear();
                events.addAll(eventList);
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        return builder.create();
    }

    private void createNotification() {
        String notificationDescription = notificationDescriptionEditText.getText().toString().trim();

        if (notificationDescription.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate current timestamp
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a");
        String timestamp = dateFormat.format(new Date());
        if (getArguments() != null) {
            eventId = getArguments().getString("eventId");
            Log.d("OrganizerSeeListOfAttendees","EventId" + eventId);
        }

        //Retrieve the event name
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("Events").document(eventId)
                .get()
                .addOnSuccessListener(eventDocument -> {
                    if (eventDocument.exists()) {
                        String eventName = eventDocument.getString("eventName");
                        if (eventName != null) {
                            // Create the notification using the event name
                            Notification notification = new Notification(eventName, notificationDescription, timestamp);

                            // Retrieve the list of attendees for the event
                            String collectionPath = "Events/" + eventId + "/CheckIns";
                            Log.d("Firestore", "Collection Path: " + collectionPath);
                            firestore.collection(collectionPath)
                                    .get()
                                    .addOnSuccessListener(queryDocumentSnapshots -> {
                                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                            String attendeeId = documentSnapshot.getId();

                                            // Create a new document for each attendee
                                            firestore.collection("Notifications").document(attendeeId)
                                                    .get()
                                                    .addOnSuccessListener(docSnapshot -> {
                                                        if (docSnapshot.exists()) {
                                                            // Document exists, update it
                                                            firestore.collection("Notifications").document(attendeeId)
                                                                    .update("allNotifications", FieldValue.arrayUnion(notification))
                                                                    .addOnSuccessListener(aVoid -> {
                                                                        Log.d("CreateNotification", "Notification added for attendee: " + attendeeId);
                                                                    })
                                                                    .addOnFailureListener(e -> {
                                                                        Log.e("CreateNotification", "Error adding notification for attendee: " + attendeeId, e);
                                                                    });
                                                        } else {
                                                            // Document doesn't exist, create it and add the notification
                                                            Map<String, Object> data = new HashMap<>();
                                                            data.put("allNotifications", Arrays.asList(notification));
                                                            firestore.collection("Notifications").document(attendeeId)
                                                                    .set(data)
                                                                    .addOnSuccessListener(aVoid -> {
                                                                        Log.d("CreateNotification", "New document created with notification for attendee: " + attendeeId);
                                                                    })
                                                                    .addOnFailureListener(e -> {
                                                                        Log.e("CreateNotification", "Error creating new document with notification for attendee: " + attendeeId, e);
                                                                    });
                                                        }
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Log.e("CreateNotification", "Error checking document existence for attendee: " + attendeeId, e);
                                                    });
                                        }
                                        // Notify listener
                                        if (listener != null) {
                                            listener.onNotificationCreated();
                                        }
                                        dismiss();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), "Error retrieving attendees: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Log.e("CreateNotification", "Event name is null");
                            Toast.makeText(getContext(), "Error: Event name is null", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("CreateNotification", "Event document does not exist");
                        Toast.makeText(getContext(), "Error: Event document does not exist", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("CreateNotification", "Error retrieving event details: " + e.getMessage());
                    Toast.makeText(getContext(), "Error retrieving event details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}