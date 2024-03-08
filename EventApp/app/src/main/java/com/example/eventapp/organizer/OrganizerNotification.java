package com.example.eventapp.organizer;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.example.eventapp.R;
import com.example.eventapp.notification.Notification;
import com.example.eventapp.notification.NotificationAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;

public class OrganizerNotification extends Fragment {
    private FirebaseFirestore db;
    private ListView notificationListView;
    private NotificationAdapter notificationAdapter;
    private ArrayList<Notification> notificationList;

    public OrganizerNotification() { }

    /**
     * Initializes the fragment. Establishes a connection to the Firestore database.
     *
     * @param savedInstanceState If non-null, this fragment is re-created from a previous saved state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Inflates the layout for the notification fragment.
     *
     * @param inflater LayoutInflater object to inflate any views in the fragment.
     * @param container Parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, the fragment is being re-constructed from a previously saved state.
     * @return The View for the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_organizer_notification, container, false);
    }

    /**
     * Sets up the ListView for displaying notifications after the view has been created. Initializes the adapter
     * and starts fetching notifications from Firebase to populate the list.
     *
     * @param view The View returned by onCreateView(LayoutInflater, ViewGroup, Bundle).
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previously saved state.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        notificationListView = view.findViewById(R.id.OrganizerNotificationListView);
        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(getContext(), notificationList);
        notificationListView.setAdapter(notificationAdapter);

        fetchNotifications(); // Implement this method to fetch notifications from Firebase

    }

    /**
     * Fetches notifications from the "Notifications" collection in Firestore. Updates the list
     * and refreshes the adapter upon successful retrieval.
     */
    private void fetchNotifications() {
        // TODO: Replace with actual collection name
        db.collection("Notifications")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    notificationList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Notification notification = document.toObject(Notification.class);
                        notificationList.add(notification);
                    }
                    notificationAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {/* Handle failure */});
    }
}
