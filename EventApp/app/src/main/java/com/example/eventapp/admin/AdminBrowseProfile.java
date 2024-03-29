package com.example.eventapp.admin;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SearchView;


import com.example.eventapp.R;
import com.example.eventapp.users.Admin;
import com.example.eventapp.users.User;
import com.example.eventapp.users.UserAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link androidx.appcompat.app.AppCompatActivity}
 * An activity that provides the functionality for an admin
 * to view a list of user profiles and interact with them.
 */
public class AdminBrowseProfile extends AppCompatActivity {
    private SearchView searchView ;
    private ArrayList<User> userDataList  ;
    private ListView profileList ;
    private UserAdapter userAdapter;
    private Map<String, DocumentReference> userImageRefMap;
    private AdminController adminController;

    public AdminBrowseProfile() {
        // Required empty public constructor
    }


    /**
     * Called when the activity is starting.
     * Sets up the UI components and profile browsing functionalities.
     *
     * @param savedInstanceState a previously saved state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_browse_profile);

        adminController = new AdminController(this);

        searchView = findViewById(R.id.profileSearcher);
        profileList = findViewById(R.id.profileListView) ;

        userDataList = new ArrayList<>() ;
        userAdapter = new UserAdapter(this, userDataList) ;
        profileList.setAdapter(userAdapter);

        userImageRefMap = new HashMap<>();


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Browse Profiles");
            actionBar.setDisplayHomeAsUpEnabled(true); // Enable the back button
        }

        setUpSearchView();
        adminController.subscribeToUserDB(userAdapter);

    }

    /**
     * Sets up the search view to filter profiles based on the entered text.
     */
    private void setUpSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filter your data based on newText
                if(TextUtils.isEmpty(newText)){
                    adminController.getCurrentUserList("", false, userAdapter);
                }else{
                    adminController.getCurrentUserList(newText, true, userAdapter);

                }
                return true;
            }
        });
    }




    /**
     * Handles the action when the up button is pressed to navigate back.
     *
     * @return Return true to indicate that the action has been handled.
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    /**
     * Handles item selections in the options menu.
     *
     * @param item The menu item that was selected.
     * @return  Return false to allow normal menu processing, true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Check if the correct item was clicked
        if (item.getItemId() == android.R.id.home) {
            // Handle the action when the up button is pressed
            return onSupportNavigateUp();
        }
        return super.onOptionsItemSelected(item);
    }
}