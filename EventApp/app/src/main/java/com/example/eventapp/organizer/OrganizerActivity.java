package com.example.eventapp.organizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
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

public class OrganizerActivity extends AppCompatActivity implements CreateEventFragment.CreateEventListener {

    private NavController back_organizerNavigation;

    /**
     * Called when the activity is starting. This method performs basic application startup logic
     * that should happen only once for the entire life of the activity. It sets the content view
     * to the activity's layout, initializes the BottomNavigationView for navigation, sets up a
     * NavController for managing UI navigation within a NavHost, and configures action bar titles
     * based on the current navigation destination.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     *                           down then this Bundle contains the data it most recently supplied in
     *                           onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer);


        Set<Integer> topLevelDestinations = new HashSet<>();
        topLevelDestinations.add(R.id.organizerHome);
        topLevelDestinations.add(R.id.organizerEvent);
        topLevelDestinations.add(R.id.organizerAccount);

//        Attaches the NavController to the Bottom Navigation Menu to enable navigation between the Fragments
//        Navigates between OrganizerAccount , OrganizerHome , and OrganizerEvent
//        Note: It doesn't navigate to any other fragment related this should be handled by the Navigation Controller
        BottomNavigationView organizerNavigationView = findViewById(R.id.bottomNavigationOrganizerView);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainerOrganizerView);
//
        NavController organizerController = navHostFragment.getNavController();
        back_organizerNavigation = organizerController;
        NavigationUI.setupWithNavController(organizerNavigationView, organizerController);

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
            } else if (destinationId == R.id.organizerEvent) {
                getSupportActionBar().setTitle("Event");
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            } else if (destinationId == R.id.organizerNotification) {
                getSupportActionBar().setTitle("Notification");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            } else if (destinationId == R.id.organizerEventInfo) {
                getSupportActionBar().setTitle("Event Information");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        });

        organizerNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {

            /**
             * Handles navigation item selections in the BottomNavigationView or a similar navigation component.
             * This method is called whenever an item in the navigation menu is selected by the user.
             * @param item The selected menu item. This parameter is annotated with {@code @NonNull} to indicate that
             *             it should never be null.
             * @return Returns true if the navigation action is successfully handled, false otherwise. Returning false
             *         typically indicates that the selected item ID does not match any known navigation actions, and
             *         no action was taken.
             */
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.organizerHome) {
                    organizerController.navigate(R.id.organizerHome);
                    return true;
                } else if (item.getItemId() == R.id.organizerNotification) {
                    organizerController.navigate(R.id.organizerNotification);
                    return true;
                } else if (item.getItemId() == R.id.organizerAccount) {
                    organizerController.navigate(R.id.organizerAccount);
                    return true;
                } else if (item.getItemId() == R.id.organizerEvent) {
                    organizerController.navigate(R.id.organizerEvent);
                    return true;
                }
                if (item.getItemId() == R.id.organizerEventInfo) {
                    organizerController.navigate(R.id.organizerEventInfo);
                    return true;
                } else
                    return false;
            }
        });


    }

    // Navigation back
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavDestination currentDestination = back_organizerNavigation.getCurrentDestination();
            if (currentDestination != null) {
                int destinationId = currentDestination.getId();
                // Check if we are in organizerEventInfo or organizerNotification
                if (destinationId == R.id.organizerEventInfo) {
                    // Navigate back from OrganizerEventInfo
                    back_organizerNavigation.navigate(R.id.action_organizerEventInfo_to_organizerEvent);
                    return true;
                } else if (destinationId == R.id.organizerNotification) {
                    // Navigate back from OrganizerNotification
                    back_organizerNavigation.navigate(R.id.action_organizerNotification_to_organizerHome);
                    return true;
                } else {
                    Log.d("Navigation", "Unhandled navigation for ID: " + destinationId);
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }




    /**
     * Handles the event creation callback. Navigates to the organizer event page.
     *
     * @param event The event that was created.
     */
    @Override
    public void onEventCreated(Event event) {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainerOrganizerView);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            navController.navigate(R.id.action_organizerHome_to_organizerEvent);
        }

    }
}