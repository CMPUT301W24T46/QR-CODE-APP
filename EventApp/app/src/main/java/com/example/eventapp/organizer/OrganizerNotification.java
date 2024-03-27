package com.example.eventapp.organizer;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.example.eventapp.R;
import com.example.eventapp.notification.Notification;
import com.example.eventapp.notification.NotificationAdapter;
import com.example.eventapp.notification.NotificationDB;
import com.example.eventapp.notification.NotificationDB.NotificationRetrievalListener;
import com.example.eventapp.users.UserDB;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class OrganizerNotification extends AppCompatActivity implements NotificationRetrievalListener {

    private NotificationDB notificationDB;
    private ListView notificationListView;
    private NotificationAdapter notificationAdapter;
    private ArrayList<Notification> notificationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_organizer_notification);
        getSupportActionBar().setTitle("Notification");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        notificationDB = new NotificationDB(FirebaseFirestore.getInstance());

        notificationListView = findViewById(R.id.OrganizerNotificationListView);
        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(this, notificationList);
        notificationListView.setAdapter(notificationAdapter);

        String currentUserId = getCurrentUserId();
        if (currentUserId != null) {
            notificationDB.getUserNotifications(currentUserId, this);
        } else {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNotificationsRetrieved(List<Notification> notifications) {
        // Filter notifications to display only those related to attendees joining events
        List<Notification> attendeeJoinNotifications = filterAttendeeJoinNotifications(notifications);

        // Update the list with filtered notifications
        notificationList.clear();
        notificationList.addAll(attendeeJoinNotifications);
        notificationAdapter.notifyDataSetChanged();
    }

    @Override
    public void onError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    private String getCurrentUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return user.getUid();
        } else {
            return null; // User is not authenticated
        }
    }


    private List<Notification> filterAttendeeJoinNotifications(List<Notification> notifications) {
        List<Notification> filteredNotifications = new ArrayList<>();
        for (Notification notification : notifications) {
            if (notification != null && notification.getType() != null && notification.getType().equals(Notification.TYPE_ATTENDEE_JOIN)) {
                filteredNotifications.add(notification);
            }
        }
        return filteredNotifications;
    }

}
