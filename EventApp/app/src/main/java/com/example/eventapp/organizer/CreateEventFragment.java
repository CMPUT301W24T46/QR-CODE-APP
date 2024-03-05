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

public class CreateEventFragment extends DialogFragment {

    interface CreateEventListener {
        void onEventCreated(Event event);
    }

    private CreateEventListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof CreateEventListener) {
            listener = (CreateEventListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement CreateEventListener");
        }
    }

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

            if(!eventName.isEmpty() && !eventDate.isEmpty()) {
                Event event = new Event(eventName, eventDate);
                if (listener != null) {
                    listener.onEventCreated(event);
                }
                dismiss(); // Dismiss the dialog
            } else {
                Toast.makeText(getContext(), "Please fill in all fields.", Toast.LENGTH_LONG).show();
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
