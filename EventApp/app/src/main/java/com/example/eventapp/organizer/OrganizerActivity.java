package com.example.eventapp.organizer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.example.eventapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashSet;
import java.util.Set;

public class OrganizerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer);


        Set<Integer> topLevelDestinations = new HashSet<>();
        topLevelDestinations.add(R.id.organizerHome) ;
        topLevelDestinations.add(R.id.organizerEvent) ;
        topLevelDestinations.add(R.id.organizerAccount) ;

//        Attaches the NavController to the Bottom Navigation Menu to enable navigation between the Fragments
//        Navigates between OrganizerAccount , OrganizerHome , and OrganizerEvent
//        Note: It doesn't navigate to any other fragment related this should be handled by the Navigation Controller
        BottomNavigationView organizerNavigationView = findViewById(R.id.bottomNavigationOrganizerView) ;
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainerOrganizerView);
//
        NavController organizerController = navHostFragment.getNavController() ;
        NavigationUI.setupWithNavController(organizerNavigationView , organizerController);

//        Changes the title on the Action Bar
//        Important Note: There is no xml from the Action Bar the Action Bar is an inbuilt component defined
//        In the theme folder in theme.xml
        organizerController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int destinationId = destination.getId();

            if (destinationId == R.id.organizerHome) {
                getSupportActionBar().setTitle("Home");
            } else if (destinationId == R.id.organizerAccount) {
                getSupportActionBar().setTitle("Account");
            } else if (destinationId == R.id.organizerEvent){
                getSupportActionBar().setTitle("Event");
            }
        });
    }
}