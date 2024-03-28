package com.example.eventapp.organizer;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.eventapp.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UpdateEventFragment extends Fragment {
    private EditText eventNameInput, eventDateInput, eventDescriptionInput;
    private ImageView eventImageView;
    private Button updateConfirmButton;
    private String eventId;
    private Uri imageUri;
    private StorageReference storageRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_organizer_update_event, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventId = getArguments().getString("eventId");
            Log.d("UpdateEventFragment", "Received EventId: " + eventId);
            if(eventId == null) {
                Log.e("UpdateEventFragment", "Event ID is null. Check argument passing.");
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventNameInput = view.findViewById(R.id.update_event_name_input);
        eventDateInput = view.findViewById(R.id.update_event_date_input);
        eventDescriptionInput = view.findViewById(R.id.update_event_description_input);
        eventImageView = view.findViewById(R.id.update_event_image_view);
        updateConfirmButton = view.findViewById(R.id.update_confirm_button);

        fetchEventDetails(eventId);

        storageRef = FirebaseStorage.getInstance().getReference("imageURL");

        eventImageView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            pickImage.launch(intent);
        });
        eventDateInput.setOnClickListener(v -> showDatePickerDialog());

        updateConfirmButton.setOnClickListener(v -> updateEventDetails());
    }

    // Image selection handling
    ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    Glide.with(this).load(imageUri).into(eventImageView);
                }
            }
    );

    private void fetchEventDetails(String eventId) {
        FirebaseFirestore.getInstance().collection("Events").document(eventId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                eventNameInput.setText(documentSnapshot.getString("eventName"));
                eventDateInput.setText(documentSnapshot.getString("eventDate"));
                eventDescriptionInput.setText(documentSnapshot.getString("eventDescription"));
                String imageUrl = documentSnapshot.getString("imageURL");
                if (imageUrl != null && !imageUrl.equals("NonImage")) {
                    Glide.with(this).load(imageUrl).into(eventImageView);
                }
            } else {
                Toast.makeText(getContext(), "Event not found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Error fetching event details", Toast.LENGTH_SHORT).show());
    }

    private void updateEventDetails() {
        String name = eventNameInput.getText().toString().trim();
        String date = eventDateInput.getText().toString().trim();
        String description = eventDescriptionInput.getText().toString().trim();

        if (imageUri != null) {
            final StorageReference imageRef = storageRef.child(eventId + "-" + System.currentTimeMillis() + "-" + imageUri.getLastPathSegment());
            imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();
                saveEventToFirestore(name, date, description, imageUrl);
            })).addOnFailureListener(e -> Toast.makeText(getContext(), "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            saveEventToFirestore(name, date, description, null);
        }
    }

    private void saveEventToFirestore(String name, String date, String description, @Nullable String imageUrl) {
        Map<String, Object> eventUpdates = new HashMap<>();
        eventUpdates.put("eventName", name);
        eventUpdates.put("eventDate", date);
        eventUpdates.put("eventDescription", description);
        if (imageUrl != null) {
            eventUpdates.put("imageURL", imageUrl);
        }

        FirebaseFirestore.getInstance().collection("Events").document(eventId)
                .update(eventUpdates)
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Event updated successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error updating event: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(getContext(), (view, year1, month1, day) -> {
            calendar.set(Calendar.YEAR, year1);
            calendar.set(Calendar.MONTH, month1);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            showTimePickerDialog(calendar);
        }, year, month, dayOfMonth).show();
    }

    private void showTimePickerDialog(Calendar calendar) {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        calendar.set(Calendar.SECOND, 0);

        new TimePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar, (view, hourOfDay, minute1) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute1);
            String selectedDate = java.text.DateFormat.getDateTimeInstance().format(calendar.getTime());
            eventDateInput.setText(selectedDate);
        }, hour, minute, false).show();
    }
}
