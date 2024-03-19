package com.example.eventapp.organizer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.eventapp.R;


public class CreateNotificationFragment extends DialogFragment{

    private Spinner eventSpinner;
    private TextView notificationTitleEditText;
    private EditText notificationDescriptionEditText;

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

        notificationTitleEditText = view.findViewById(R.id.CreateAnnouncementTitle);
        notificationDescriptionEditText = view.findViewById(R.id.EditEventDescription);
        Button backButton = view.findViewById(R.id.buttonArrow);
        backButton.setOnClickListener(v -> dismiss());

        Button confirmButton = view.findViewById(R.id.buttonConfirm);
        confirmButton.setOnClickListener(v -> createNotification());

        Button selectEventsButton = view.findViewById(R.id.selectEventsButton);
        selectEventsButton.setOnClickListener(v -> openEventList());

        builder.setView(view);

        return builder.create();
    }

    private void openEventList() {
        Intent intent = new Intent(requireContext(), NotificationEventListActivity.class);
        startActivity(intent);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        notificationTitleEditText = view.findViewById(R.id.CreateAnnouncementTitle);
        notificationDescriptionEditText = view.findViewById(R.id.EditEventDescription);

        Button buttonNotificationEventList = view.findViewById(R.id.selectEventsButton);
        buttonNotificationEventList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NotificationEventListActivity.class);
                startActivity(intent);
            }
        });

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