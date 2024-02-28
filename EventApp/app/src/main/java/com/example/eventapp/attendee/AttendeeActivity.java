package com.example.eventapp.attendee;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.example.eventapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashSet;
import java.util.Set;

//Attached to activity_attendee
public class AttendeeActivity extends AppCompatActivity {

//    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendee);

        Set<Integer> topLevelDestinations = new HashSet<>();
        topLevelDestinations.add(R.id.attendeeEvent) ;
        topLevelDestinations.add(R.id.attendeeHome) ;
        topLevelDestinations.add(R.id.attendeeAccount) ;

//        Attaches the NavController to the Bottom Navigation Menu to enable navigation between the Fragments
//        Navigates between AttendeeAcount , AttendeeHome , and Attendee Event
//        Note: It doesn't navigate to any other fragment related this should be handled by the Navigation Controller

        BottomNavigationView attendeeNavigationView = findViewById(R.id.bottomNavigationAttendeeView) ;
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainerAttendeeView);

        NavController attendeeController = navHostFragment.getNavController() ;
        NavigationUI.setupWithNavController(attendeeNavigationView , attendeeController);

//        Changes the title on the Action Bar
//        Important Note: There is no xml from the Action Bar the Action Bar is an inbuilt component defined
//        In the theme folder in theme.xml
        attendeeController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int destinationId = destination.getId();

            if (destinationId == R.id.attendeeHome) {
                getSupportActionBar().setTitle("Home");
            } else if (destinationId == R.id.attendeeAccount) {
                getSupportActionBar().setTitle("Account");
            } else if (destinationId == R.id.attendeeEvent){
                getSupportActionBar().setTitle("Event");
            }
        });
    }


}