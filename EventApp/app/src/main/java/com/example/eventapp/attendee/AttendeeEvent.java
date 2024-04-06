package com.example.eventapp.attendee;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;


import com.example.eventapp.R;
import com.example.eventapp.event.Event;
import com.example.eventapp.event.EventAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment representing the attendee's view of events.
 */
public class AttendeeEvent extends Fragment {
    private View rootView;
    private FirebaseFirestore db;
    private SearchView searchView ;
    private CollectionReference eventsRef;
    private ArrayList<Event> eventDataList  ;
    private ListView eventList ;
    private String uid ;
    private EventAdapter eventListArrayAdapter ;
    private boolean doneSearching = false ;

    /**
     * Constructor of an instance of AttendeeEvent
     */
    public AttendeeEvent() {
        // Required empty public constructor
    }


    /**
     * called at initial creation of fragment.
     *
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("Events") ;
        uid = FirebaseAuth.getInstance().getUid() ;
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to. The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_attendee_event, container, false);
    }

    /**
     * Called immediately after onCreateView(LayoutInflater, ViewGroup, Bundle) has returned,
     * but before any saved state has been restored in to the view.
     *
     * @param view               The View returned by onCreateView(LayoutInflater, ViewGroup, Bundle).
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.rootView = view;

        searchView = view.findViewById(R.id.eventSearcher);
        eventList = view.findViewById(R.id.eventListView) ;

        eventDataList = new ArrayList<>() ;

        // Implement the OnEventClickListener
        EventAdapter.OnEventClickListener eventClickListener = new EventAdapter.OnEventClickListener() {
            @Override
            public void onEventClick(Event event) {
                Bundle bundle = new Bundle();
                Log.d("Entered View Event" , event.getEventName()) ;
                bundle.putString("eventName", event.getEventName());
                bundle.putString("eventDate", event.getEventDate());
                bundle.putString("imageURL", event.getImageURL());
                bundle.putString("eventDescription", event.getEventDescription());
                bundle.putString("eventId" , event.getEventId());
                bundle.putLong("eventLim" , event.getEventLim());
                Log.d("Error Lim" , String.valueOf(event.getEventLim()));
//                bundle.putLong("eventLim" , 11) ;
                bundle.putLong("eventSignUps" , event.getNumberOfSignUps());
//                bundle.putLong("eventSignUps" , 5);
                Navigation.findNavController(rootView).navigate(R.id.action_attendeeEvent_to_attendeeEventInformation , bundle);
            }
        };

        eventListArrayAdapter = new EventAdapter(getContext() , eventDataList, eventClickListener) ;
        eventList.setAdapter(eventListArrayAdapter);

        if(uid != null){
            eventsRef.orderBy("eventDate").addSnapshotListener((value, error) -> {
                if (error != null) {
                    Log.e("Firestore", "Listen failed.", error);
                    return;
                }

                eventDataList.clear();
                for (QueryDocumentSnapshot doc : value) {
                    loadEventList(doc);
                }
                eventListArrayAdapter.notifyDataSetChanged();
            });
        }else{
            setStaticEventList();
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Perform search action here
                if(uid != null){
                    getCurrentEvenList(query , true);
                }else{
                    filterStaticEventList(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filter your data based on newText
                if(TextUtils.isEmpty(newText) && uid != null){
                    getCurrentEvenList("" , false);
                }else if(uid == null){
                    setStaticEventList();
                }
                return false;
            }
        });

    }

    /**
     * Retrieves the current list of events from firebase based on a search query.
     *
     * @param searchText     The text to search for in event names.
     * @param queryOrDisplay A boolean indicating whether to perform a query or display all events.
     */
    public void getCurrentEvenList(String searchText, boolean queryOrDisplay){
        Task<QuerySnapshot> query;

        query = eventsRef.orderBy("eventDate").get();
        query.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                eventDataList.clear();
                ArrayList<Event> searchResults = new ArrayList<>();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    // Retrieve data from each document
                    String eventName = documentSnapshot.getString("eventName");
                    String URL = documentSnapshot.getString("imageURL");
                    String eventDate = documentSnapshot.getString("eventDate");
                    String eventId = documentSnapshot.getId() ;
                    String eventDescription = documentSnapshot.getString("eventDescription") ;
                    Long eventLimit = documentSnapshot.getLong("attendeeLimit") ;
                    Long numberOfSignUps = documentSnapshot.getLong("Total Number of Sign Ups") ;

                    if(eventLimit == null){
                        eventLimit = (long) -1;
                    }

                    if(numberOfSignUps == null){
                        numberOfSignUps = 0L;
                    }

                    Log.d("Event Limit" ,  String.valueOf(eventLimit)) ;

                    if(!queryOrDisplay){
                        searchResults.add(new Event(eventName,  eventDate , URL , eventId , eventDescription, eventLimit, numberOfSignUps));
                        continue;
                    }

                    if (eventName.toLowerCase().contains(searchText.toLowerCase())) {
                        searchResults.add(new Event(eventName,  eventDate , URL , eventId , eventDescription, eventLimit, numberOfSignUps));
                    }
                }

                eventListArrayAdapter.setFilter(searchResults);
                eventListArrayAdapter.notifyDataSetChanged();

                if(!searchText.equals("")){
                    doneSearching = true ;
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle failure
                Log.e("TAG", "Error getting documents: " + e);
            }
        });
    }

    /**
     * Checks if the search operation is completed.
     *
     * @return true if the search operation is completed, false otherwise.
     */
    public boolean isDoneSearching(){
        return doneSearching ;
    }


    /**
     * Filters the static event list based on the provided search text.
     * This method is called when the application is in test mode
     * @param searchText The text to search for in event names.
     */
    public void filterStaticEventList(String searchText){
        String eventStaticName ;
        Event staticEvent ;
        ArrayList<Event> searchResults = new ArrayList<>();
        for(int i = 0 ; i < eventDataList.size() ; i++){
            staticEvent = eventDataList.get(i) ;
            eventStaticName = eventDataList.get(i).getEventName() ;
            if (eventStaticName.toLowerCase().contains(searchText.toLowerCase())) {
                searchResults.add(new Event(staticEvent.getEventName(),  staticEvent.getEventDate() , staticEvent.getImageURL() , staticEvent.getEventId() ,
                        staticEvent.getEventDescription()));
            }
        }
        eventListArrayAdapter.setFilter(searchResults);
        eventListArrayAdapter.notifyDataSetChanged();
    }

    /**
     * Sets the static event list with predefined events.
     * This method is intended to populate the event list with predefined events for testing purposes.
     */
    public void setStaticEventList(){
        ArrayList<Event> staticEvents = new ArrayList<>() ;
        staticEvents.add(new Event("First Event" , "19/72/43" , "" , "Test Id" , "Event for the Young")) ;
        staticEvents.add(new Event("Second Event" , "19/72/43" , "" , "Test Id" , "Event for the Young")) ;
        eventListArrayAdapter.setFilter(staticEvents);
        eventListArrayAdapter.notifyDataSetChanged();
    }

    public void loadEventList(QueryDocumentSnapshot documentSnapshot){
        String eventName = documentSnapshot.getString("eventName");
        String URL = documentSnapshot.getString("imageURL");
        String eventDate = documentSnapshot.getString("eventDate");
        String eventId = documentSnapshot.getId() ;
        String eventDescription = documentSnapshot.getString("eventDescription") ;
        Long eventLimit = documentSnapshot.getLong("attendeeLimit") ;
        Long numberOfSignUps = documentSnapshot.getLong("Total Number of Sign Ups") ;

        if(eventLimit == null){
            eventLimit = (long) -1;
        }

        if(numberOfSignUps == null){
            numberOfSignUps = 0L;
        }

        eventDataList.add(new Event(eventName,  eventDate , URL , eventId , eventDescription, eventLimit, numberOfSignUps));
    }

}