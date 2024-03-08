package com.example.eventapp.organizer;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.eventapp.R;
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
public class OrganizerHome extends Fragment {


    private TextView organizer_welcomeMessageText;
    private CollectionReference userRef;


    public OrganizerHome() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialize user
        userRef = FirebaseFirestore.getInstance().collection("Users");
    }

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
                Navigation.findNavController(v).navigate(R.id.action_organizerHome_to_organizerNotification);
            }
        });
    }

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