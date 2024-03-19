package com.example.eventapp.organizer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.eventapp.R;
import com.example.eventapp.event.Event;
import com.example.eventapp.event.EventDB;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.eventapp.event.EventDB.EventRetrievalListener;

import java.util.ArrayList;
import java.util.List;


public class CreateNotificationFragment extends DialogFragment implements EventDB.EventRetrievalListener {

    private Spinner eventSpinner;
    private TextView notificationTitleEditText;
    private EditText notificationDescriptionEditText;
    private EventSpinnerAdapter spinnerAdapter;

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

        eventSpinner = view.findViewById(R.id.eventSpinner);
        notificationTitleEditText = view.findViewById(R.id.CreateAnnouncementTitle);
        notificationDescriptionEditText = view.findViewById(R.id.EditEventDescription);
        Button backButton = view.findViewById(R.id.buttonArrow);
        Button confirmButton = view.findViewById(R.id.buttonConfirm);

        backButton.setOnClickListener(v -> dismiss());

        confirmButton.setOnClickListener(v -> createNotification());

        builder.setView(view);

        return builder.create();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventSpinner = view.findViewById(R.id.eventSpinner);
        notificationTitleEditText = view.findViewById(R.id.CreateAnnouncementTitle);
        notificationDescriptionEditText = view.findViewById(R.id.EditEventDescription);

        // Initialize EventDB
        EventDB eventDB = new EventDB(FirebaseFirestore.getInstance());

        // Get the current user's ID
        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Call getAllEventsForUser method to retrieve events
        eventDB.getAllEventsForUser(currentUserID, this);

        // Create the spinner adapter
        spinnerAdapter = new EventSpinnerAdapter(requireContext(), new ArrayList<>());
    }

    @Override
    public void onEventsRetrieved(List<Event> events) {
        // Handle retrieved events
        if (events != null && !events.isEmpty()) {
            // Log the list of events
            for (Event event : events) {
                Log.d("CreateNotificationFragment", "Event Name: " + event.getEventName());
                // Add more properties if needed
            }

            // Display a toast message with the number of events retrieved
            Toast.makeText(getContext(), events.size() + " events retrieved", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "No events found", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onError(String errorMessage) {
        // Handle error
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }


    private void createNotification() {
        String selectedEvent = eventSpinner.getSelectedItem().toString();
        String notificationTitle = notificationTitleEditText.getText().toString().trim();
        String notificationDescription = notificationDescriptionEditText.getText().toString().trim();

        if (notificationTitle.isEmpty() || notificationDescription.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Notify listener that notification is created
        if (listener != null) {
            listener.onNotificationCreated();
        }

        dismiss();
    }
}