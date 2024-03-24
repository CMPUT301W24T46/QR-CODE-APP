package com.example.eventapp.organizer;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventapp.R;
import com.example.eventapp.attendee.AttendeeNotification;
import com.example.eventapp.notification.Notification;
import com.example.eventapp.notification.NotificationAdapter;
import com.example.eventapp.notification.NotificationDB;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
import com.example.eventapp.users.UserDB;


public class OrganizerNotification extends AppCompatActivity implements CreateNotificationFragment.CreateNotificationListener, NotificationDB.NotificationRetrievalListener {
    private FirebaseFirestore db;
    private ListView notificationListView;
    private NotificationAdapter notificationAdapter;
    private ArrayList<Notification> notificationList;
    private NotificationAdapter adapter;
    private NotificationDB notificationDB;
    private String currentUserId;

    public OrganizerNotification() { }

    /**
     * Initializes the fragment. Establishes a connection to the Firestore database.
     *
     * @param savedInstanceState If non-null, this fragment is re-created from a previous saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_organizer_notification);
        getSupportActionBar().setTitle("Notification");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = FirebaseFirestore.getInstance();

        notificationListView = findViewById(R.id.OrganizerNotificationListView);
        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(this, notificationList);
        notificationListView.setAdapter(notificationAdapter);

        UserDB userDB = new UserDB(this, this, FirebaseFirestore.getInstance());
        String currentUserId = userDB.getCurrentUserId();
        if (currentUserId != null) {
            notificationDB = new NotificationDB(db);
            notificationDB.getUserNotifications(currentUserId, this);
        } else {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
        }
        fetchNotifications();

        // Pop up create notification window
        Button buttonCreateAnnouncement = findViewById(R.id.createNotificationButton);
        buttonCreateAnnouncement.setOnClickListener(v -> {
            // Show the create notification dialog
            CreateNotificationFragment dialogFragment = new CreateNotificationFragment();
            dialogFragment.show(getSupportFragmentManager(), "CreateNotificationDialog");
        });
    }

    /**
     * Fetches notifications from the "Notifications" collection in Firestore. Updates the list
     * and refreshes the adapter upon successful retrieval.
     */
    private void fetchNotifications() {
        db.collection("Notifications")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Clear the existing list before adding new notifications
                    notificationList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Notification notification = document.toObject(Notification.class);
                        notificationList.add(notification);
                    }
                    // Notify the adapter that the data set has changed
                    notificationAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Log.e(TAG, "Error fetching notifications: " + e.getMessage());
                });
    }


    /**
     *Handles the selection of menu items in the activity's options menu.
     * @param item The menu item that was selected.
     *
     * @return super.onOptionsItemSelected(item);
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNotificationCreated() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainerOrganizerView);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            navController.navigate(R.id.action_organizerHome_to_organizerNotification);
        }
        fetchNotifications();
    }

    @Override
    public void onNotificationsRetrieved(List<Notification> notifications) {
        // Add the retrieved notifications to the list
        notificationList.addAll(notifications);

        // Notify the adapter
        notificationAdapter.notifyDataSetChanged();

        // Update visibility of "no notification" text view
        updateNoNotificationTextView();
    }

    private void updateNoNotificationTextView() {
        TextView noNotifyText = findViewById(R.id.noNotifyText);
        if (notificationList.isEmpty()) {
            // If the list is empty, display the "no notification" text view
            noNotifyText.setVisibility(View.VISIBLE);
        } else {
            // If the list has notifications, hide the "no notification" text view
            noNotifyText.setVisibility(View.GONE);
        }
    }

    @Override
    public void onError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }
}