package com.example.eventapp.organizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.eventapp.R;
import com.example.eventapp.event.Event;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashSet;
import java.util.Set;

public class OrganizerActivity extends AppCompatActivity implements CreateEventFragment.CreateEventListener{

    private NavController back_organizerNavigation ;

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
        back_organizerNavigation = organizerController ;
        NavigationUI.setupWithNavController(organizerNavigationView , organizerController);

//        Changes the title on the Action Bar
//        Important Note: There is no xml from the Action Bar the Action Bar is an inbuilt component defined
//        In the theme folder in theme.xml
        organizerController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int destinationId = destination.getId();

            if (destinationId == R.id.organizerHome) {
                getSupportActionBar().setTitle("Home");
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            } else if (destinationId == R.id.organizerAccount) {
                getSupportActionBar().setTitle("Account");
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            } else if (destinationId == R.id.organizerEvent){
                getSupportActionBar().setTitle("Event");
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }else if (destinationId == R.id.organizerNotification){
                getSupportActionBar().setTitle("Notification");
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        });

        organizerNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.organizerHome){
                    organizerController.navigate(R.id.organizerHome);
                    return true ;
                }else if(item.getItemId() == R.id.organizerNotification){
                    organizerController.navigate(R.id.organizerNotification);
                    return true ;
                }else if(item.getItemId() == R.id.organizerAccount){
                    organizerController.navigate(R.id.organizerAccount);
                    return true ;
                }else if(item.getItemId() == R.id.organizerEvent) {
                    organizerController.navigate(R.id.organizerEvent);
                    return true;
                }
                return false ;
            }
        });


    }

    //    Navigates to the right page depending on instance and back button pressed
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavDestination currentDestination = back_organizerNavigation.getCurrentDestination();

            if (currentDestination.getId() == R.id.organizerNotification) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                back_organizerNavigation.navigate(R.id.action_organizerNotification_to_organizerHome);
            }
            else{
                Log.d("Navigation not possible" , "Add if statement") ;
            }
            return true; // Indicate that the event has been consumed
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onEventCreated(Event event) {
        // Handle the event
    }
}