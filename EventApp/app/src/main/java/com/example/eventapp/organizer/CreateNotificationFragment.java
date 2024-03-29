package com.example.eventapp.organizer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import com.example.eventapp.notification.NotificationDB;
import java.text.SimpleDateFormat;
import java.util.Date;



public class CreateNotificationFragment extends DialogFragment {

    private TextView notificationTitleEditText;
    private EditText notificationDescriptionEditText;
    private ArrayList<Event> events = new ArrayList<>(); // Initialize events list
    private EventDB eventDB;

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
        String eventId = notificationDescriptionEditText.getTag() != null ? notificationDescriptionEditText.getTag().toString() : "";

        // Get current user ID
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        NotificationDB notificationDB = new NotificationDB(FirebaseFirestore.getInstance());
        // Create a Notification object with current user ID
        Notification notification = new Notification("", notificationDescription, timestamp.toString(), currentUserId, eventId);

        // Save the notification to the Firestore database
        notificationDB.saveNotificationToFirestore(notification);

        // Notify listener that notification is created
        if (listener != null) {
            listener.onNotificationCreated();
        }
        dismiss();
    }


}

