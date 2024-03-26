package com.example.eventapp.attendee;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.eventapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.navigation.Navigation;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AttendeeHome} factory method to
 * create an instance of this fragment.
 */
public class AttendeeHome extends Fragment {
    private TextView welcomeMessageText;
    private CollectionReference userRef;


    /**
     * Constructor of an instance of AttendeeHome
     */
    public AttendeeHome() {
        // Required empty public constructor
    }

    /**
     * Called to do initial creation of the fragment. This is called after onAttach(Activity)
     * and before onCreateView(LayoutInflater, ViewGroup, Bundle).
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state,
     *                             this is the state. This value may be null.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initialize user
        userRef = FirebaseFirestore.getInstance().collection("Users");

    }

    /**
     * Called to create the view hierarchy associated with the fragment.
     * This method is called when the fragment is instantiated and its UI needs to be created.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     *                           The fragment should not add the view itself, but this can be used to generate
     *                           the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state
     *                           as given here.
     * @return The root View of the inflated layout, or null if the fragment has no UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_attendee_home, container, false);
        return rootView;
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} has returned.
     * This is where you should initialize your UI components and set up any event listeners.
     *
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize welcome message TextView
        welcomeMessageText = view.findViewById(R.id.welcome_message);

        // Set the welcome message
        setWelcomeMessage();

        Button notificationButton = view.findViewById(R.id.notification);
        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start AttendeeNotificationActivity
                startActivity(new Intent(getActivity(), AttendeeNotification.class));
            }
        });

        // navigate to scan QR code
        Button joinEventButton = view.findViewById(R.id.join_event);
        joinEventButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(AttendeeHome.this)
                    .navigate(R.id.action_attendeeHome_to_attendeeQRCodeScan);
        });
    }


    /**
     * Set welcome message to anonymous when user not yet input username.
     * Update welcome message to "Welcome, (User Username)" after user input username.
     */
    private void setWelcomeMessage() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
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
                                welcomeMessageText.setText(getString(R.string.welcome_message, username));
                            } else {
                                welcomeMessageText.setText(getString(R.string.default_attendee_home_text));
                            }
                        }
                    }
                }
            });
        }
    }

}