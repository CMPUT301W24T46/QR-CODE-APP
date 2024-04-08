package com.example.eventapp.organizer;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.eventapp.R;
import com.example.eventapp.attendee.AttendeeNotification;
import com.example.eventapp.event.Event;
import com.example.eventapp.users.Organizer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Fragment for the organizer's home screen, displaying welcome messages and navigation options.
 */
public class OrganizerHome extends Fragment{


    private TextView organizer_welcomeMessageText;
    private CollectionReference userRef;

    /**
     * Constructor for OrganizerHome fragment. Used for initialization.
     */
    public OrganizerHome() {
        // Required empty public constructor
    }

    /**
     * Initializes the fragment. Sets up a reference to the 'Users' collection in Firestore.
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this holds the data.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialize user
        userRef = FirebaseFirestore.getInstance().collection("Users");
    }

    /**
     * Inflates the layout for this fragment, initializing and setting the welcome message.
     *
     * @param inflater LayoutInflater object to inflate views in the fragment.
     * @param container Parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, the fragment is being re-constructed from a saved state.
     * @return The View for the fragment's UI, or null.
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_organizer_home, container, false);

        // Initialize welcome message
        organizer_welcomeMessageText = rootView.findViewById(R.id.organizer_welcome_message);

        // Set the welcome message
        setWelcomeMessage();

        return rootView;

    }

    /**
     * Sets up listeners for UI elements after the view has been created. This includes initializing buttons
     * for creating events and displaying notifications, with click listeners to handle navigation.
     *
     * @param view The View returned by onCreateView(LayoutInflater, ViewGroup, Bundle).
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     */

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Pop up create event window
        Button buttonCreateEvent = view.findViewById(R.id.button_createEvent);
        buttonCreateEvent.setOnClickListener(v -> {
            // Navigate to the OrganizerEvent fragment
            // Show the create event dialog
            CreateEventFragment dialogFragment = new CreateEventFragment();
            dialogFragment.show(getChildFragmentManager(), "CreateEventFragment");
        });

        // Display notification page
        Button notificationButton = view.findViewById(R.id.button_notification);
        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start OrganizerNotificationActivity
                startActivity(new Intent(getActivity(), OrganizerNotification.class));
            }
        });
    }

    /**
     * Sets the welcome message for the user. Retrieves the user's name from Firestore based on their UID
     * and updates the welcome message TextView accordingly. If no name is found, a default message is set.
     */

    private void setWelcomeMessage() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(userRef == null){
            Log.d("UserRef" , "NULL") ;
        }else{
            Log.d("UserRef" , "NOT NULL") ;
        }
        if (currentUser != null) {
            String uid = currentUser.getUid();
            userRef.document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            String username = document.getString("name");
                            if (username != null && !username.isEmpty()) {
                                organizer_welcomeMessageText.setText(getString(R.string.welcome_message, username));
                            } else {
                                organizer_welcomeMessageText.setText(getString(R.string.default_attendee_home_text));
                            }
                        }
                    }
                }
            });
        }
    }
}