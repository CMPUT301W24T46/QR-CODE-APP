package com.example.eventapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.eventapp.admin.AdminActivity;
import com.example.eventapp.attendee.AttendeeActivity;
import com.example.eventapp.organizer.OrganizerActivity;
import com.example.eventapp.users.UserDB;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A {@link Fragment} subclass used for handling the selection of different account types.
 * Users can choose between attending an event, organizing an event, or operating as an admin.
 * This class is responsible for displaying the options and handling the navigation based on
 * the user's selection.
 */
public class AccountSelection extends Fragment {

    String[] accountOptionsData = {"Attend Event" , "Organize Event" , "Admin"} ;
    ListView accountOptionsListView ;

    boolean isLoggedInTest = true;
    SelectOptionsAdapter accountOptionsAdapter ;

    Activity activity ;

    Context context ;

    public FirebaseFirestore dbQRApp ;

    private UserDB userDB ;

    /**
     * Required empty public constructor.
     */
    public AccountSelection() {
        // Required empty public constructor
    }

    /**
     * Called to do initial creation of a fragment.
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = requireActivity() ;
        context = requireContext() ;
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account_selection, container, false);
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * has returned, but before any saved state has been restored into the view.
     * This gives subclasses a chance to initialize themselves once they know their view
     * hierarchy has been completely created. This class sets up the adapter for the List View and sets an on
     * click listener
     *
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is re-constructed from a previous saved state.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        accountOptionsListView = view.findViewById(R.id.accountOptionList) ;
        accountOptionsAdapter = new SelectOptionsAdapter(requireContext(), accountOptionsData) ;
        accountOptionsListView.setAdapter(accountOptionsAdapter);

        accountOptionsListView.setOnItemClickListener((parent , v , position , id)->{
            navigateToAccount(v , parent , position);
        });
    }

    /**
     * Called when the Fragment is visible to the user.
     */
    @Override
    public void onStart() {
        super.onStart();
        dbQRApp = FirebaseFirestore.getInstance();
        userDB = new UserDB(context ,activity , dbQRApp) ;
    }

    /**
     * Handles the navigation to activities depending on the item in the
     * list view that is clicked
     *
     * @param view The View that was clicked.
     * @param parent The AdapterView where the click happened.
     * @param position The position of the view in the adapter.
     */
    private void navigateToAccount(View view , AdapterView<?> parent , int position){
        String selectedAccount = (String) parent.getItemAtPosition(position) ;
        if(selectedAccount.equals("Attend Event")){
            userDB.setTypeOfUser("Attendee");
            userDB.getUserInfoAttendee(new UserDB.AuthCallbackAttendee(){
                @Override
                public void onSuccess(){
                    Log.d("Callback Worked" , "Call Intent now") ;
                    Intent intent = new Intent(getActivity(), AttendeeActivity.class);
                    startActivity(intent);
                    isLoggedInTest = false ;
                }
            });
        }else if(selectedAccount.equals("Organize Event")){
            userDB.setTypeOfUser("Organizer");
            userDB.getUserInfoAttendee(new UserDB.AuthCallbackAttendee(){
                @Override
                public void onSuccess(){
                    Log.d("Callback Worked" , "Call Intent now") ;
                    Intent intent = new Intent(getActivity(), OrganizerActivity.class);
                    startActivity(intent);
                    isLoggedInTest = false ;
                }
            });
        }else if(selectedAccount.equals("Admin")){
            userDB.setTypeOfUser("Administrator");
            userDB.getUserInfoAttendee(new UserDB.AuthCallbackAttendee(){
                @Override
                public void onSuccess(){
                    Intent intent = new Intent(getActivity(), AdminActivity.class);
                    startActivity(intent);
                    isLoggedInTest = false ;
                }
            });
        }
    }

    /**
     * Checks if a login or account selection process is currently in progress.
     *
     * @return true if login is in process and false if login has been completed
     */
    public boolean isLoginInProgress() {
        return isLoggedInTest ;
    }
}