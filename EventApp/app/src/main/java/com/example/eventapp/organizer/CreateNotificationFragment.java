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
import com.example.eventapp.firestoreservice.NotificationSend;
import com.example.eventapp.notification.Notification;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
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

import org.w3c.dom.Document;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A DialogFragment subclass for creating and sending a new notification to event attendees.
 * It provides a user interface for entering notification details and sends the notification to all attendees of a specific event.
 */

public class CreateNotificationFragment extends DialogFragment {

    private TextView notificationTitleEditText;
    private EditText notificationDescriptionEditText;
    private ArrayList<Event> events = new ArrayList<>(); // Initialize events list
    private EventDB eventDB;
    private FirebaseFirestore db;
    private String eventId;

    /**
     * Listener interface for notification creation actions. The host activity must implement this interface to handle notification creation.
     */

    interface CreateNotificationListener {

        /**
         * Called when a notification is successfully created and sent.
         */

        void onNotificationCreated();
    }

    private CreateNotificationListener listener;

    /**
     * Attaches the fragment to its host activity and sets up the listener for notification creation.
     *
     * @param context The activity context.
     * @throws ClassCastException if the context does not implement CreateNotificationListener.
     */

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (CreateNotificationListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement CreateNotificationListener");
        }
    }

    /**
     * Creates the dialog view with input fields for notification details and sets up confirmation and cancellation actions.
     *
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return A Dialog instance to be displayed by the fragment.
     */

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_organizer_create_notification, null);
        String currentUserId = FirebaseAuth.getInstance().getUid() ;
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

        if(currentUserId != null){
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
        }


        return builder.create();
    }

    /**
     * Gathers the entered notification details from the input fields, creates a notification, and sends it to all attendees of the event.
     */

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
                                            String attendeeId = documentSnapshot.getString("attendeeId");
                                            Log.d("CreateNotification", "Attendee ID: " + attendeeId);
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
                                                                        sendPushNotifications(eventName , notification.getMessage());
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
                                                                        sendPushNotifications(eventName , notification.getMessage());
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

    /**
     * Sends push notifications to all event attendees using the Firebase Cloud Messaging service.
     *
     * @param eventName    The name of the event for which the notification is sent.
     * @param notification The notification message to be sent.
     */

    public void sendPushNotifications(String eventName , String notification){
//        Log.d("Push Notification" , "Called") ;
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("Events")
                .document(eventId)
                .collection("RegistrationTokens")
                .document("Tokens") ;

        docRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Retrieve the array
                                List<String> arrayTokens= (List<String>) document.get("token");
                                // Do something with your array
                                Log.d("Push Notification" , "Called") ;
                                NotificationSend notificationSend = new NotificationSend(arrayTokens, eventName , notification) ;
                                notificationSend.sendNotifications();
                            } else {
                                Log.d("TAG", "No such document");
                            }
                        } else {
                            Log.d("TAG", "get failed with ", task.getException());
                        }
                    }
                });
    }
}