package com.example.eventapp.attendee;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.eventapp.R;

/**
 * Account page for Attendee to access profile editing function and others.
 * A simple {@link Fragment} subclass.
 * Use the {@link AttendeeAccount} factory method to
 * create an instance of this fragment.
 */
public class AttendeeAccount extends Fragment {

    /**
     * Constructor of an instance of AttendeeAccount
     */
    public AttendeeAccount() {
        // Required empty public constructor
    }

    /**
     * Called to do initial creation of the fragment.
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state,
     *                           this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Inflates the layout for the attendee account fragment and initializes UI components.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment.
     * @param container          The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState A Bundle containing the fragment's previously saved state, if any.
     * @return The root View of the inflated layout for the fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_attendee_account, container, false);

        Button btnCustomizeProfile = rootView.findViewById(R.id.btnCustomizeProfile);
        Button btnHistory = rootView.findViewById(R.id.EventHistory);

        btnCustomizeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CustomizeProfile.class);
                startActivity(intent);
            }
        });
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AttendedEvents.class);
                startActivity(intent);
            }
        });

        return rootView;
    }
}