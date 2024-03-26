package com.example.eventapp.attendee;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.eventapp.R;
import com.example.eventapp.notification.Notification;
import com.example.eventapp.notification.NotificationAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AttendeeNotification} factory method to
 * create an instance of this fragment.
 */
public class AttendeeNotification extends AppCompatActivity {
    private FirebaseFirestore db;
    private ListView notificationListView;
    private ArrayAdapter<String> testNotificationAdapter ;
    private NotificationAdapter notificationAdapter;
    private ArrayList<Notification> notificationList;

    private ArrayList<String> testNotificationList;
    /**
     * Constructor of an instance of AttendeeNotification
     */
    public AttendeeNotification() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_attendee_notification);
        getSupportActionBar().setTitle("Notification");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = FirebaseFirestore.getInstance();

        notificationListView = findViewById(R.id.AttendeeNotificationListView);
        testNotificationList = new ArrayList<>() ;
        testNotificationAdapter = new ArrayAdapter<>(this , R.layout.notification_list_item, testNotificationList) ;

        notificationListView.setAdapter(testNotificationAdapter);

//        notificationList = new ArrayList<>();
//        notificationAdapter = new NotificationAdapter(this, notificationList);
//        notificationListView.setAdapter(notificationAdapter);

        fetchNotifications();
    }

    /**
     * Fetches notifications from the "Notifications" collection in Firestore. Updates the list
     * and refreshes the adapter upon successful retrieval.
     */
    private void fetchNotifications() {
        String uid = FirebaseAuth.getInstance().getUid() ;
        if(uid != null){
            DocumentReference notifiyDocUser = db.collection("Notifications").document(uid) ;
            notifiyDocUser.addSnapshotListener(this, (value, error) -> {
                if (error != null) {
                    // Handle errors
                    return;
                }

                if (value != null && value.exists()) {
                    // Get the updated array field from the document
                    ArrayList<String> updatedArray = (ArrayList<String>) value.get("allNotifications");
                    if (updatedArray != null) {
                        testNotificationList.clear();
                        testNotificationList.addAll(updatedArray);
                        testNotificationAdapter.notifyDataSetChanged();
                    }
                }
            });
        }


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

}
