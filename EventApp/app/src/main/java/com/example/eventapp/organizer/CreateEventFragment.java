package com.example.eventapp.organizer;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
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

import java.util.Calendar;

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
        EditText eventDateEditText = view.findViewById(R.id.EditEventDate); // // initialize date selection
        Button buttonConfirm = view.findViewById(R.id.buttonConfirm); // Confirm Button
        Button buttonArrow = view.findViewById(R.id.buttonArrow); // Previous Button

        // Setting up the Confirm button
        buttonConfirm.setOnClickListener(view1 -> {
            String eventName = eventNameEditText.getText().toString();
            String eventDate = eventDateEditText.getText().toString();
            // TODO: Generate image URL
            String imageURL = "URL_HERE";
            // Fetching the creatorId
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
                dismiss(); // closed the dialog
            } else {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_LONG).show();
            }
        });
        // Set up the event date EditText to show DatePickerDialog on click
        eventDateEditText.setOnClickListener(v -> showDatePickerDialog());

        // Setting up the Previous button
        buttonArrow.setOnClickListener(view12 -> {
            dismiss(); // closed the dialog
        });

        builder.setView(view);

        AlertDialog dialog = builder.create();
        // Auto-closing when the area outside the dialog is clicked.
        dialog.setCanceledOnTouchOutside(true);

        return dialog;
    }

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

    // Specific time selection
    private void showTimePickerDialog(final Calendar calendar) {
        // Initialize time with current time
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Use a theme to specify the spinner style
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                // OpenAI, 2024, ChatGPT, https://chat.openai.com/share/a00d4633-779c-4368-93b0-9906c6bb7824
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar, // spinner style
                (view, selectedHour, selectedMinute) -> {
                    // Your existing code to handle time selection
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

}