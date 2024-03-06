package com.example.eventapp.attendee;

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

import com.example.eventapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AttendeeHome#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AttendeeHome extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    String[] homepageOptionsData = {"Create Event" , "Join Event" , "Notifications"} ;
    ListView homepageOptionsListView ;

    HomePageAdapter homePageAdapter ;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView welcomeMessageText;
    private CollectionReference userRef;


    public AttendeeHome() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AttendeeHome.
     */
    // TODO: Rename and change types and number of parameters
    public static AttendeeHome newInstance(String param1, String param2) {
        AttendeeHome fragment = new AttendeeHome();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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

        // Initialize welcome message
        welcomeMessageText = rootView.findViewById(R.id.welcome_message);

        // Set the welcome message
        setWelcomeMessage();

        return rootView;
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