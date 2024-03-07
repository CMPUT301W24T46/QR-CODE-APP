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
    private FirebaseFirestore db;
    private SearchView searchView ;
    private CollectionReference userRef;
    private ArrayList<User> userDataList  ;
    private ListView profileList ;
    private UserAdapter userAdapter;
    private Map<String, DocumentReference> userImageRefMap;

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

        userImageRefMap = new HashMap<>();


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
                return false;
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





    public void getCurrentUserList(String searchText, boolean queryOrDisplay){
        userRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<User> searchResults = new ArrayList<>();

                // Iterate over all documents
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    String userID = doc.getId();
                    String name = doc.getString("name");
                    String contactInfo = doc.getString("contactInformation");
                    String homepage = doc.getString("homepage");
                    String typeOfUser = doc.getString("typeOfUser");


                    User user = new User(userID, name, contactInfo, homepage, "", typeOfUser);

                    // Check if filter is needed
                    if (queryOrDisplay && user.getName().toLowerCase().contains(searchText.toLowerCase())) {
                        searchResults.add(user);
                    } else if (!queryOrDisplay) {
                        searchResults.add(user); // Add all users when not searching
                    }
                }

                userAdapter.setFilter(searchResults);
                userAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(e -> Log.e("TAG", "Error getting documents: " + e));
    }

    private void subscribeToFireStore() {
        userRef.addSnapshotListener((querySnapshots, error) -> {
            if (error != null) {
                Log.e("Firestore", error.toString());
                return;
            }

            if (querySnapshots != null) {
                userAdapter.clear();
                userImageRefMap.clear();
                for (QueryDocumentSnapshot doc : querySnapshots) {
                    String userID = doc.getId();
                    String name = doc.getString("name");
                    String contactInfo = doc.getString("contactInformation");
                    String homepage = doc.getString("homepage");
                    String typeOfUser = doc.getString("typeOfUser");

                    DocumentReference imageRef = doc.getDocumentReference("imageUrl");
                    if (imageRef != null) {
                        userImageRefMap.put(userID, imageRef);
                    }

                    User user = new User(userID, name, contactInfo, homepage, "", typeOfUser);
                    userDataList.add(user);
                }
                userAdapter.notifyDataSetChanged();
                loadProfileImages(); // Load profile images after users are loaded
            }
        });
    }

    private void loadProfileImages() {
        if (userImageRefMap.isEmpty()) {
            return;
        }

        List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
        for (DocumentReference imageRef : userImageRefMap.values()) {
            tasks.add(imageRef.get());
        }

        Task<List<DocumentSnapshot>> allTasks = Tasks.whenAllSuccess(tasks);
        allTasks.addOnSuccessListener(documentSnapshots -> {
            for (DocumentSnapshot snapshot : documentSnapshots) {
                String imageUrl = snapshot.getString("URL");

                // Update the imageURL for all users with this image reference
                for (User user : userDataList) {
                    DocumentReference userImageRef = userImageRefMap.get(user.getId());
                    if (userImageRef != null && userImageRef.equals(snapshot.getReference())) {
                        user.setImageURL(imageUrl);
                    }
                }
            }
            userAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> Log.e("TAG", "Error loading images", e));
    }



    private String findUserIdByImageRef(DocumentReference imageRef) {
        for (Map.Entry<String, DocumentReference> entry : userImageRefMap.entrySet()) {
            if (entry.getValue().equals(imageRef)) {
                return entry.getKey();
            }
        }
        return null;
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