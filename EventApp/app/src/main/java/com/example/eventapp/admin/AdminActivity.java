package com.example.eventapp.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.eventapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashSet;
import java.util.Set;


/**
 *  {@link androidx.appcompat.app.AppCompatActivity}
 * Displays bottom navigation and fragments for each navigation item
 * Sets up up navigation using NavController and BottomNavigationView for switching between admin-related fragments.
 *
 * <p>Navigation is managed by a {@link NavController} that is attached to a {@link BottomNavigationView}. The
 * NavController orchestrates fragment transactions to display the appropriate admin pages. The activity also
 * handles savedInstanceState to restore previous states if the activity has been reinitialized.</p>
 *
 * <p>Important: The ActionBar titles for different destinations are hardcoded as "Home" and "Account" for the
 * respective fragments. These values should be externalized in string resources for localization.</p>
 *
 * <p>Usage: This activity is intended to be used as part of the app's admin module. It should be declared in the
 * AndroidManifest.xml file with the appropriate intent-filters if it's intended to be an entry point from other
 * parts of the app or external applications.</p>
 */

//This class is attached to activity_admin
public class AdminActivity extends AppCompatActivity {
    private NavController adminController;

    /**
     * Called when the activity is starting. Initiates the activity
     *
     * @param savedInstanceState A previously saved state if the activity has been run before
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Set<Integer> topLevelDestinations = new HashSet<>();
        topLevelDestinations.add(R.id.adminHome) ;
        topLevelDestinations.add(R.id.adminAccount) ;



        // Attaches the NavController to the Bottom Navigation Menu to enable navigation between the Fragments
        // Navigates between AdminHome and AdminAccount
        BottomNavigationView adminNavigationView = findViewById(R.id.bottomNavigationAdminView) ;
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainerAdminView);
        adminController = navHostFragment.getNavController() ;
        NavigationUI.setupWithNavController(adminNavigationView , adminController);


        // Changes the title on the Action Bar
        // Important Note: There is no xml from the Action Bar the Action Bar is an inbuilt component defined
        // In the theme folder in theme.xml
        adminController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int destinationId = destination.getId();

            if (destinationId == R.id.adminHome) {
                getSupportActionBar().setTitle("Home");
            } else if (destinationId == R.id.adminAccount) {
                getSupportActionBar().setTitle("Account");
            }
        });
    }

}