package com.example.eventapp.admin;

import android.os.Bundle;

import androidx.annotation.Nullable;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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




    // This method is called when the up button is pressed
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    // This method is used if you have an options menu
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