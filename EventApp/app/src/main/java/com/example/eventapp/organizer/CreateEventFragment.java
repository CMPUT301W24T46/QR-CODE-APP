package com.example.eventapp.organizer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.eventapp.R;
import com.example.eventapp.event.Event;
import com.example.eventapp.event.EventDB;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * DialogFragment for creating a new event. It prompts the user to enter event details and passes the event back to the hosting activity.
 */
public class CreateEventFragment extends DialogFragment {

    /**
     * Listener interface for event creation actions.
     */
    interface CreateEventListener {
        void onEventCreated(Event event);
    }

    private CreateEventListener listener;

    /**
     * Attaches the context as a listener for event creation.
     * @param context The activity context.
     * @throws ClassCastException if context does not implement CreateEventListener.
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof CreateEventListener) {
            listener = (CreateEventListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement CreateEventListener");
        }
    }

    /**
     * Creates and returns the dialog for event creation with input fields and confirmation.
     * @param savedInstanceState The last saved instance state of the Fragment, or null if this is a freshly created Fragment.
     * @return A new dialog instance.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_create_event, null);

        EditText eventNameEditText = view.findViewById(R.id.EditEventName);
        EditText eventDateEditText = view.findViewById(R.id.EditEventDate);
        Button buttonConfirm = view.findViewById(R.id.buttonConfirm); // Confirm Button
        ImageButton buttonArrow = view.findViewById(R.id.buttonArrow); // Previous Button

        // Setting up the Confirm button
        buttonConfirm.setOnClickListener(view1 -> {
            String eventName = eventNameEditText.getText().toString();
            String eventDate = eventDateEditText.getText().toString();
            // Assume imageURL is retrieved or generated somehow
            String imageURL = "URL_HERE";
            // Fetching the creatorId (current user's ID from Firebase Auth)
            String creatorId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            if (!eventName.isEmpty() && !eventDate.isEmpty() && creatorId != null) {
                Event event = new Event(eventName, eventDate, imageURL, creatorId);
                EventDB eventDB = new EventDB(FirebaseFirestore.getInstance());
                eventDB.addorganizerEvent(event.getEventName(), event.getEventDate(), event.getImageURL(), event.getCreatorId());

                // Now navigate to the organizer event page or wherever you need
                // Navigation code goes here if needed
                if (listener != null) {
                    listener.onEventCreated(event);
                }
                dismiss(); // Dismiss the dialog
            } else {
                Toast.makeText(getContext(), "Please fill in all fields and log in.", Toast.LENGTH_LONG).show();
            }
        });


        // Setting up the Previous button
        buttonArrow.setOnClickListener(view12 -> {
            dismiss(); // Dismiss the dialog
        });

        builder.setView(view)
                .setTitle("Create Event");

        AlertDialog dialog = builder.create();
        // Auto-closing when the area outside the dialog is clicked.
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

}