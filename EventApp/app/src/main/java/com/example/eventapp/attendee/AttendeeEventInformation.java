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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.eventapp.R;

/**
 * Fragment showing detail information about event for attendees.
 */
public class AttendeeEventInformation extends Fragment {

    private ImageView bigEventImageView ;
    private TextView eventNameView ;
    private TextView eventDescriptionView;
    private TextView eventDateView;

    private View toolBarBinding ;

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
     * a previous saved state, this is the state.
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
            eventNameView = view.findViewById(R.id.eventTitleDescrip) ;
            bigEventImageView = view.findViewById(R.id.biggerEventImage) ;
            eventDescriptionView = view.findViewById(R.id.eventFullDescription);
            eventDateView = view.findViewById(R.id.attendee_event_date_time);
            eventNameView.setText(eventName);
            eventDateView.setText(eventDate);
            eventDescriptionView.setText(eventDescription);
            Log.d("EventInfo", "Event Description: " + eventDescription);

            Glide.with(requireContext()).load(URL).centerCrop().into(bigEventImageView) ;
        }


    }

}