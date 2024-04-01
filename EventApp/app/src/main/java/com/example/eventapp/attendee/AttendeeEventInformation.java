package com.example.eventapp.attendee;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.eventapp.R;
import com.example.eventapp.helpers.CheckForEventHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fragment showing detail information about event for attendees.
 */
public class AttendeeEventInformation extends Fragment {

    private ImageView bigEventImageView ;
    private TextView eventNameView ;

    private List<String> eventArrayList = new ArrayList<>();
    private TextView alreadySignedUpTextView ;
    private String eventId;
    private TextView eventDescriptionView;
    private TextView eventDateView;

    private View toolBarBinding;

    /**
     * Constructor of an instance of AttendeeEventInformation
     */
    public AttendeeEventInformation() {
        // Required empty public constructor
    }

    /**
     * Called at initial creation of this fragment
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /**
     * Inflates the layout for this fragment.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to. The fragment should not add the view itself, but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return The inflated view for this fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_attendee_event_information, container, false);
    }

    /**
     * Called immediately after onCreateView(LayoutInflater, ViewGroup, Bundle) has returned
     *
     * @param view               The View returned by onCreateView(LayoutInflater, ViewGroup, Bundle).
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Event Information");
            actionBar.setDisplayHomeAsUpEnabled(true); // Enable the back button
        }

        Bundle args = getArguments();
        if (args != null) {
            // Extract information from the bundle
            String eventName = args.getString("eventName");
            String URL = args.getString("imageURL");
            String eventDate = args.getString("eventDate");
            String eventDescription = args.getString("eventDescription");
            eventId = args.getString("eventId") ;
            eventNameView = view.findViewById(R.id.eventTitleDescrip) ;
            bigEventImageView = view.findViewById(R.id.biggerEventImage) ;
            eventDescriptionView = view.findViewById(R.id.eventFullDescription);
            eventDateView = view.findViewById(R.id.attendee_event_date_time);
            eventNameView.setText(eventName);
            eventDateView.setText(eventDate);
            eventDescriptionView.setText(eventDescription);
            Log.d("EventInfo", "Event Description: " + eventDescription);

            Glide.with(requireContext()).load(URL).centerCrop().into(bigEventImageView);
        }
        Button signUpButton = view.findViewById(R.id.signUpForEventButton) ;
        alreadySignedUpTextView = view.findViewById(R.id.alreadySigneUpTextView);
        alreadySignedUpTextView.setVisibility(View.INVISIBLE);

        signUpButton.setOnClickListener(v -> {
            if(FirebaseAuth.getInstance().getUid() != null){
                addEventToMyEventList();
            }else{
                alreadySignedUpTextView.setVisibility(View.VISIBLE);
            }
        });

        if(FirebaseAuth.getInstance().getUid() != null){
            notifyAboutEventsAttended();
        }
    }

    /**
     * Adds the current event to the user's event list in Firestore. This method
     * retrieves the current user's ID, then updates their 'EventList' field in the
     * Firestore database to include the current event ID. It handles success and failure
     * of the update operation with appropriate logging. If the update is successful,
     * it also informs the event about the new sign-up by calling {@link #informEventAboutSignUp(String)}.
     */
    private void addEventToMyEventList(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        if(userId != null){
            DocumentReference documentReference = db.collection("Users").document(userId) ;
            documentReference.update("EventList", FieldValue.arrayUnion(eventId))
                    .addOnSuccessListener(aVoid -> {
                        Log.d("User", "Event Signed Up for,  Event Id: " + eventId + " " + userId) ;
                        informEventAboutSignUp(userId);

                    })
                    .addOnFailureListener(e ->
                            Log.w("Event", "Error updating document", e)
                    );
        }
    }

    /**
     * Informs the specified event about a new user sign-up by adding the user's ID to
     * the 'Event Attendees' field in Firestore. It updates the specific event document
     * by adding the current user's ID to the CheckIn subcollection. On success and failure of the
     * operation, appropriate log messages are generated.
     *
     * @param userId The ID of the user signing up for the event.
     */
    private void informEventAboutSignUp(String userId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("Events").document(eventId) ;
        CollectionReference eventSubCollection = db.collection("Events").document(eventId).collection("Registrations");

        Map<String, Object> data = new HashMap<>();
        data.put("attendeeId", userId);
        data.put("registrationDate", FieldValue.serverTimestamp());

        // Add the document to the sub collection with the specified ID
        eventSubCollection.document(userId).set(data)
                .addOnSuccessListener(aVoid -> {
                    // Document added successfully
                    Log.d("Sucessful" , "Sign Up") ;
                })
                .addOnFailureListener(e -> {
                    // Handle any errors
                    Log.d("UnSucessful" , "Sign Up") ;
                });
    }

    /**
     * Attaches a snapshot listener to the current user's 'EventList' field in Firestore
     * and updates the local eventArrayList with any changes. This method keeps the local
     * list of events attended by the user up-to-date with the database. It also checks if the
     * user has already signed up for the current event and updates the UI accordingly.
     */
    private void notifyAboutEventsAttended () {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        DocumentReference documentReference = db.collection("Users").document(userId) ;
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("Listen", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    // Assuming the field you are interested in is named "interestingField"
                    if (snapshot.contains("EventList")) {
                        List<String> newArray = (List<String>) snapshot.get("EventList");
                        eventArrayList.clear();
                        eventArrayList.addAll(newArray) ;
                        boolean alreadySignedUp = CheckForEventHelper.checkForEvent(eventId , (ArrayList<String>) eventArrayList);

                        if(alreadySignedUp){
                            alreadySignedUpTextView.setVisibility(View.VISIBLE);
                            Log.d("User" , "Alreay Signed Up " + eventId) ;
                        }else{
                            alreadySignedUpTextView.setVisibility(View.INVISIBLE);
                            Log.d("User" , "NotSignedUp") ;
                        }
                    }
                } else {
                    Log.d("User", "Current data: null");
                }
            }
        });
    }
}
