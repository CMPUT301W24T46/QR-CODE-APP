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
import com.example.eventapp.users.User;
import com.example.eventapp.users.UserAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AdminBrowseProfile extends AppCompatActivity {
    private FirebaseFirestore db;
    private SearchView searchView ;
    private CollectionReference userRef;
    private ArrayList<User> userDataList  ;
    private ListView profileList ;
    private UserAdapter userAdapter;
    public AdminBrowseProfile() {
        // Required empty public constructor
    }

    // TODO: Replace icon with profile image

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_browse_profile);

        db = FirebaseFirestore.getInstance();
        userRef = db.collection("Users");

        searchView = findViewById(R.id.profileSearcher);
        profileList = findViewById(R.id.profileListView) ;

        userDataList = new ArrayList<>() ;
        userAdapter = new UserAdapter(this, userDataList) ;
        profileList.setAdapter(userAdapter);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Browse Profiles");
            actionBar.setDisplayHomeAsUpEnabled(true); // Enable the back button
        }
        setUpSearchView();
        subscribeToFireStore();

    }

    private void setUpSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getCurrentUserList(query, true);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filter your data based on newText
                if(TextUtils.isEmpty(newText)){
                    getCurrentUserList("", false);
                }else{
                    getCurrentUserList(newText, true);
                }
                return true;
            }
        });
    }


    private void subscribeToFireStore() {
        userRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots,
                                @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }

                if (querySnapshots != null) {
                    userAdapter.clear();
                    for (QueryDocumentSnapshot doc: querySnapshots) {
                        User user = doc.toObject(User.class);
                        Log.d("Firestore", user.getId());
                        userDataList.add(user);
                    }
                    userAdapter.notifyDataSetChanged();
                }
            }
        });
    }



    public void getCurrentUserList(String searchText, boolean queryOrDisplay){
        userRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<User> searchResults = new ArrayList<>();
                if (!queryOrDisplay) {
                    // If not searching, add all users
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        User user = documentSnapshot.toObject(User.class);
                        searchResults.add(user);
                    }
                } else {
                    // If searching, filter the list
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user.getName().toLowerCase().contains(searchText.toLowerCase())) {
                            searchResults.add(user);
                        }
                    }
                }

                userAdapter.setFilter(searchResults);
                userAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(e -> Log.e("TAG", "Error getting documents: " + e));
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