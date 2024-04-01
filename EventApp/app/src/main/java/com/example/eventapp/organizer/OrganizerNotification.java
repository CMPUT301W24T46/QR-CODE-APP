package com.example.eventapp.organizer;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.eventapp.R;
import com.example.eventapp.attendeeNotification.AttendeeNotifAdapter;
import com.example.eventapp.attendeeNotification.NotificationItem;
import com.example.eventapp.notification.Notification;
import com.example.eventapp.notification.NotificationAdapter;
import com.example.eventapp.notification.NotificationDB;
import com.example.eventapp.notification.NotificationDB.NotificationRetrievalListener;
import com.example.eventapp.users.UserDB;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OrganizerNotification extends AppCompatActivity {

    private FirebaseFirestore db;
    private ListView notificationListView;
    private AttendeeNotifAdapter notificationAdapter;
    private ArrayList<NotificationItem> originalNotificationList;
    private ArrayList<NotificationItem> notificationList;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_organizer_notification);
        getSupportActionBar().setTitle("Notification");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = FirebaseFirestore.getInstance();

        notificationListView = findViewById(R.id.OrganizerNotificationListView);
        notificationList = new ArrayList<>();
        notificationAdapter = new AttendeeNotifAdapter(this, notificationList);
        notificationListView.setAdapter(notificationAdapter);
        spinner = findViewById(R.id.spinner2);
        fetchNotifications();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Fetches notifications specific to the organizer from the "Milestones" collection in Firestore.
     * Updates the list and refreshes the adapter upon successful retrieval.
     */
    private void fetchNotifications() {
        String organizerId = FirebaseAuth.getInstance().getUid();
        if (organizerId != null) {
            DocumentReference milestonesDocOrganizer = db.collection("Milestones").document(organizerId);
            milestonesDocOrganizer.addSnapshotListener(this, (value, error) -> {
                if (error != null) {
                    Log.e("OrganizerNotification", "Error fetching notifications: " + error.getMessage(), error);
                    return;
                }

                if (value != null && value.exists()) {
                    // Get updated array field from document
                    ArrayList<HashMap<String, String>> updatedArray = (ArrayList<HashMap<String, String>>) value.get("allMilestones");
                    if (updatedArray != null) {
                        originalNotificationList = new ArrayList<>();
                        for (HashMap<String, String> notificationMap : updatedArray) {
                            String title = notificationMap.get("title");
                            String timestamp = notificationMap.get("timestamp");
                            String content = notificationMap.get("message");

                            NotificationItem notificationItem = new NotificationItem(title, timestamp, content);
                            originalNotificationList.add(notificationItem);
                        }

                        // Reverse the notification list to sort it by how recent the notification is
                        Collections.reverse(originalNotificationList);

                        // Populate the spinner with distinct titles
                        Set<String> distinctTitles = new HashSet<>();
                        for (NotificationItem item : originalNotificationList) {
                            distinctTitles.add(item.getTitle());
                        }
                        ArrayList<String> distinctTitlesList = new ArrayList<>(distinctTitles);
                        Collections.sort(distinctTitlesList); // Sort titles alphabetically
                        distinctTitlesList.add(0, "All"); // Add "All" option at beginning
                        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, distinctTitlesList);
                        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(spinnerAdapter);

                        // Set up Spinner function
                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                String selectedItem = parent.getItemAtPosition(position).toString();
                                filterNotifications(selectedItem);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                //nothing
                            }
                        });
                    }
                }
            });
        } else {
            Log.e("OrganizerNotification", "Organizer ID is null");
        }
    }

    /**
     * Filter notifications based on the selected item from the spinner.
     * @param selectedItem The title of the item selected for filtering.
     */
    private void filterNotifications(String selectedItem) {
        ArrayList<NotificationItem> filteredList = new ArrayList<>();
        if (selectedItem.equals("All")) {
            // show all notifications from original list
            filteredList.addAll(originalNotificationList);
        } else {
            // Filter by selected item from original list every time
            for (NotificationItem item : originalNotificationList) {
                if (item.getTitle().equals(selectedItem)) {
                    filteredList.add(item);
                }
            }
        }
        notificationAdapter.setData(filteredList);
    }


}
