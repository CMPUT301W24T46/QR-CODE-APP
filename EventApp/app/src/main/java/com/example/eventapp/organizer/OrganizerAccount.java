package com.example.eventapp.organizer;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.eventapp.R;
import com.example.eventapp.attendee.CustomizeProfile;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrganizerAccount} factory method to
 * create an instance of this fragment.
 */
public class OrganizerAccount extends Fragment {

    public OrganizerAccount() {
        // Required empty public constructor
    }

    /**
     * Initializes the fragment. This method is called when the fragment is created. It is used to
     * perform initial setup, such as retrieving arguments passed to the fragment during creation.
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this
     *                           is the state. This value may be {@code null}.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Called to have the fragment instantiate its user interface view. This is optional, and non-graphical
     * fragments can return null. This method is called between {@code onCreate(Bundle)} and
     * {@code onActivityCreated(Bundle)}. It inflates the layout for the fragment and initializes the
     * fragment's UI components.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     *                  The fragment should not add the view itself, but this can be used to generate the
     *                  LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *                           saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_organizer_account, container, false);

        Button btnCustomizeProfile = rootView.findViewById(R.id.organizer_btnCustomizeProfile);

        btnCustomizeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change this to navigate to the OrganizerCustomizeProfile activity
                Intent intent = new Intent(getActivity(), OrganizerCustomizeProfile.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

}