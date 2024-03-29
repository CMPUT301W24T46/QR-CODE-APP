package com.example.eventapp.attendee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.eventapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashSet;
import java.util.Set;

//Attached to activity_attendee
/**
 * The AttendeeActivity class represents the main activity for the attendee user.
 * It handles navigation between different fragments such as the home, account, and event pages
 * using a bottom navigation bar.
 */
public class AttendeeActivity extends AppCompatActivity {
    private NavController backNavigation ;
    /**
     * Called when the activity is starting.
     * Initializes the activity's UI and sets up navigation.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously
     *                           being shut down then this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle).
     *                           Note: Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendee);

        Set<Integer> topLevelDestinations = new HashSet<>();
        topLevelDestinations.add(R.id.attendeeEventMenu) ;
        topLevelDestinations.add(R.id.attendeeHome) ;
        topLevelDestinations.add(R.id.attendeeAccount) ;

//        Attaches the NavController to the Bottom Navigation Menu to enable navigation between the Fragments
//        Navigates between AttendeeAcount , AttendeeHome , and Attendee Event
//        Note: It doesn't navigate to any other fragment related this should be handled by the Navigation Controller

        BottomNavigationView attendeeNavigationView = findViewById(R.id.bottomNavigationAttendeeView) ;
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainerAttendeeView);

        NavController attendeeController = navHostFragment.getNavController() ;
        backNavigation = attendeeController ;
        NavigationUI.setupWithNavController(attendeeNavigationView , attendeeController);

//        Changes the title on the Action Bar if the controller if the navigation changes
        attendeeController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int destinationId = destination.getId();
            if (destinationId == R.id.attendeeHome) {
                Log.d("Attendee Home" , "Clicked") ;
                getSupportActionBar().setTitle("Home");
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            } else if (destinationId == R.id.attendeeAccount) {
                Log.d("Attendee Account" , "Clicked") ;
                getSupportActionBar().setTitle("Account");
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            } else if (destinationId == R.id.attendeeEventMenu){
                Log.d("Attendee Event" , "Clicked") ;
                getSupportActionBar().setTitle("Event");
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        });

//        Navigated to corresponding page depending on the menu item clicked
        attendeeNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.attendeeEventMenu){
                    Log.d("Event Activity", "Main Event Page") ;
                    attendeeController.navigate(R.id.attendeeEventMenu);
                    return true ;
                }else if(item.getItemId() == R.id.attendeeHome){
                    Log.d("Event Activity", "Main Home Page") ;
                    attendeeController.navigate(R.id.attendeeHome);
                    return true ;
                }else if(item.getItemId() == R.id.attendeeAccount){
                    Log.d("Event Activity", "Main Account Page") ;
                    attendeeController.navigate(R.id.attendeeAccount);
                    return true ;
                }
                return false ;
            }
        });

    }

//    Navigates to the right page depending on instance and back button pressed
    /**
     * Called when a menu item is selected.
     *
     * @param item The selected menu item
     * @return true if the event has been consumed, false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavDestination currentDestination = backNavigation.getCurrentDestination();

            if (currentDestination.getId() == R.id.attendeeEventInformation) {
                // Fragment1 is currently being displayed
                Log.d("Event Page:" , "Back Navigation to Event Complete") ;
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                backNavigation.navigate(R.id.action_attendeeEventInformation_to_attendeeEvent);
            }
            else{
                Log.d("Navigation not possible" , "Add if statement") ;
            }
            return true; // Indicate that the event has been consumed
        }
        return super.onOptionsItemSelected(item);
    }
}