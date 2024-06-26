package com.example.eventapp.organizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.eventapp.R;
import com.example.eventapp.event.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * DialogFragment for creating a new event. It prompts the user to enter event details and passes the event back to the hosting activity.
 */
public class CreateEventFragment extends DialogFragment {
    private Uri imageUri;
    private ImageView eventImageView;

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
        View view = inflater.inflate(R.layout.fragment_organizer_create_event, null);

        EditText eventNameEditText = view.findViewById(R.id.EditEventName);
        EditText eventDateEditText = view.findViewById(R.id.EditEventDate); // // initialize date selection
        EditText eventDescriptionEditText = view.findViewById(R.id.EditEventDescription);
        Button buttonConfirm = view.findViewById(R.id.buttonConfirm); // Confirm Button
        Button buttonArrow = view.findViewById(R.id.buttonArrow); // Previous Button
        eventImageView = view.findViewById(R.id.EventImageView); // Initialize the ImageView
        EditText limitAttendeeEditText = view.findViewById(R.id.LimitAttendeesView);

        // Set input type to accept only numbers
        limitAttendeeEditText.setInputType(InputType.TYPE_CLASS_NUMBER);

        eventImageView.setOnClickListener(v -> pickImage());


        // Setting up the Confirm button
        buttonConfirm.setOnClickListener(view1 -> {
            String eventName = eventNameEditText.getText().toString().trim();
            String eventDate = eventDateEditText.getText().toString().trim();
            String eventDescription = eventDescriptionEditText.getText().toString().trim();
            String creatorId = FirebaseAuth.getInstance().getUid(); // Fetching the creatorId
            Integer attendeeLimit = -1;
            // Check if the attendee limit EditText is not empty
            if (!limitAttendeeEditText.getText().toString().isEmpty()) {
                // If not empty, parse the input value to an integer
                attendeeLimit = Integer.parseInt(limitAttendeeEditText.getText().toString().trim());
            }

            if (!eventName.isEmpty() && !eventDate.isEmpty() && creatorId != null && !eventDescription.isEmpty()) {
                createEvent(eventName, eventDate, creatorId, eventDescription, imageUri, attendeeLimit);
            } else {
                String uid = FirebaseAuth.getInstance().getUid();
                if(uid != null){
                    Toast.makeText(getContext(), "Please fill in all fields!", Toast.LENGTH_LONG).show();
                }
            }
        });
        // Set up the event date EditText to show DatePickerDialog on click
        eventDateEditText.setOnClickListener(v -> showDatePickerDialog());

        // Setting up the Previous button
        buttonArrow.setOnClickListener(view12 -> {
            dismiss(); // closed the dialog
        });

        builder.setView(view);

        // May not need these two line of code
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false); // Auto-closing when the area outside the dialog is clicked.

        return dialog;
    }

    /**
     * Displays a DatePickerDialog to select the event date.
     */

    // Date selection
    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        // Initialize date with current date
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, selectedYear, selectedMonth, dayOfMonth) -> {
            // Save selected date in Calendar instance
            calendar.set(Calendar.YEAR, selectedYear);
            calendar.set(Calendar.MONTH, selectedMonth);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            // After a date is picked, show TimePickerDialog
            showTimePickerDialog(calendar);

        }, year, month, day);

        // Set the DatePickerDialog to show dates from now to next 10 years
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        calendar.add(Calendar.YEAR, 10);
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());

        datePickerDialog.show();
    }

    /**
     * Displays a TimePickerDialog after a date has been selected, to select the event time.
     *
     * @param calendar The Calendar instance with the selected date to set the initial time.
     */

    // Specific time selection
    private void showTimePickerDialog(final Calendar calendar) {
        // Initialize time with current time
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        calendar.set(Calendar.SECOND, 0);

        // Use a theme to specify the spinner style
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                // OpenAI, 2024, ChatGPT, https://chat.openai.com/share/a00d4633-779c-4368-93b0-9906c6bb7824
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar, // spinner style
                (view, selectedHour, selectedMinute) -> {

                    calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                    calendar.set(Calendar.MINUTE, selectedMinute);

                    // Format and display the selected date and time
                    String formattedDateTime = java.text.DateFormat.getDateTimeInstance().format(calendar.getTime());
                    EditText eventDateEditText = getDialog().findViewById(R.id.EditEventDate);
                    eventDateEditText.setText(formattedDateTime);
                }, hour, minute, true); // 24-hour format

        // Show the TimePickerDialog with spinner style
        timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        timePickerDialog.show();
    }

    /**
     * Initiates an intent to pick an image from the device's gallery.
     */

    // Image Selection
    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickImage.launch(intent);
    }

    ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    eventImageView.setImageURI(imageUri);
                }
            }
    );

    /**
     * Gathers the entered event details from the input fields and calls {@link #saveEventToFirestore(Event)} to create the event.
     *
     * @param eventName        The name of the event.
     * @param eventDate        The date and time of the event.
     * @param creatorId        The ID of the event creator.
     * @param eventDescription The description of the event.
     * @param imageUri         The URI of the selected event image.
     * @param attendeeLimit    The attendee limit for the event.
     */

    private void createEvent(String eventName, String eventDate, String creatorId, String eventDescription, Uri imageUri, Integer attendeeLimit) {
        // If user decide to upload image at this time
        if (imageUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference("event_images");
            final StorageReference imageRef = storageRef.child(System.currentTimeMillis() + "-" + imageUri.getLastPathSegment());

            imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();
                Event event;
                if (attendeeLimit != null) {
                    event = new Event(eventName, eventDate, imageUrl, eventDescription, attendeeLimit);
                } else {
                    event = new Event(eventName, eventDate, imageUrl, eventDescription);
                }
                event.setCreatorId(creatorId);
                saveEventToFirestore(event);
            })).addOnFailureListener(e -> Toast.makeText(getContext(), "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            // Otherwise
        } else {
            // Create event without image URL
            String imageUrl = "NonImage"; // Default image URL
            Event event = new Event(eventName, eventDate, imageUrl, eventDescription, attendeeLimit);
            event.setCreatorId(creatorId);
            saveEventToFirestore(event);
        }
    }

    /**
     * Saves the created event to Firestore and notifies the listener upon success or failure.
     *
     * @param event The Event object to be saved.
     */

    private void saveEventToFirestore(Event event) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("eventName", event.getEventName());
        eventMap.put("eventDate", event.getEventDate());
        eventMap.put("imageURL", event.getImageURL());
        eventMap.put("creatorId", event.getCreatorId());
        eventMap.put("eventDescription", event.getEventDescription());
        eventMap.put("attendeeLimit", event.getAttendeeLimit());
        eventMap.put("Total Number of Sign Ups" , 0) ;

        db.collection("Events").add(eventMap)
                .addOnSuccessListener(documentReference -> {
                    // Handle success
                    if (listener != null) {
                        listener.onEventCreated(event);
                    }
                    dismiss();
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Toast.makeText(getContext(), "Failed to create event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}