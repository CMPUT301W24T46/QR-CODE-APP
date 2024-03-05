package com.example.eventapp.organizer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

        builder.setView(view)
                .setTitle("Create Event")
                .setPositiveButton("Confirm", null) // Placeholder to later override
                .setNegativeButton("Previous", (dialog, id) -> dialog.dismiss());

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view1 -> {
                String eventName = eventNameEditText.getText().toString();
                String eventDate = eventDateEditText.getText().toString();

                if(!eventName.isEmpty() && !eventDate.isEmpty()) {
                    // Assuming Event has a constructor that takes a name and a date
                    Event event = new Event(eventName, eventDate);
                    listener.onEventCreated(event);
                    dialog.dismiss();
                } else {
                    Toast.makeText(getContext(), "Please fill in all fields.", Toast.LENGTH_LONG).show();
                }
            });
        });

        return dialog;
    }
}
