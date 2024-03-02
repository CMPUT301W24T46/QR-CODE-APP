package com.example.eventapp.attendee;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;


import com.example.eventapp.R;
import com.example.eventapp.event.Event;
import com.example.eventapp.event.EventAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AttendeeEvent extends Fragment {
    private FirebaseFirestore db;
    private SearchView searchView ;
    private CollectionReference eventsRef;
    private ArrayList<Event> eventDataList  ;
    private ListView eventList ;
    private EventAdapter eventListArrayAdapter ;


    public AttendeeEvent() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("Events") ;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_attendee_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchView = view.findViewById(R.id.eventSearcher);
        eventList = view.findViewById(R.id.eventListView) ;

        eventDataList = new ArrayList<>() ;
        eventListArrayAdapter = new EventAdapter(getContext() , eventDataList) ;
        eventList.setAdapter(eventListArrayAdapter);

        eventsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots,
                                @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    eventDataList.clear();
                    for (QueryDocumentSnapshot doc: querySnapshots) {
                        String eventId = doc.getId();
                        String eventName = doc.getString("Name");
                        String imageURL = doc .getString("URL") ;
                        Log.d("Firestore", String.format("Name(%s, %s) fetched", eventId, eventName));
                        eventDataList.add(new Event(eventName , imageURL));
                    }
                    eventListArrayAdapter.notifyDataSetChanged();
                }
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Perform search action here
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filter your data based on newText
                if(TextUtils.isEmpty(newText)){
                    getCurrentEvenList("" , false);
                }else{
                    getCurrentEvenList(newText , true);
                }
                return false;
            }
        });
    }

    public void getCurrentEvenList(String searchText, boolean queryOrDisplay){
        eventsRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                eventDataList.clear();
                ArrayList<Event> searchResults = new ArrayList<>();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    // Retrieve data from each document
                    String eventName = documentSnapshot.getString("Name");
                    String URL = documentSnapshot.getString("URL");
                    if(!queryOrDisplay){
                        searchResults.add(new Event(eventName, URL));
                        continue;
                    }

                    if (eventName.toLowerCase().contains(searchText.toLowerCase())) {
                        searchResults.add(new Event(eventName, URL));
                    }
                }

                eventListArrayAdapter.setFilter(searchResults);
                eventListArrayAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle failure
                Log.e("TAG", "Error getting documents: " + e);
            }
        });
    }
}