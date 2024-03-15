package com.example.eventapp;

import static junit.framework.TestCase.assertEquals;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.Navigation;
import androidx.navigation.testing.TestNavHostController;
import androidx.test.core.app.ApplicationProvider;
import org.junit.Test;

public class AttendeeLoginTest {
    @Test
    public void testNavControllerWithTestNavHostControllerAttendee() {
        TestNavHostController navController = new TestNavHostController(
                ApplicationProvider.getApplicationContext());

        FragmentScenario<AccountSelection> scenario = FragmentScenario.launchInContainer(AccountSelection.class);
        scenario.onFragment(fragment -> {
            // Set the graph on the TestNavHostController
            navController.setGraph(R.navigation.app_nav_graph);
            // Make the NavController available via the Fragment
            Navigation.setViewNavController(fragment.requireView(), navController);

            // Perform the navigation action
            navController.navigate(R.id.action_accountSelection_to_attendeeActivity);

            // Assert the current destination is DetailFragment
            assertEquals(navController.getCurrentDestination().getId(), R.id.attendeeActivity);

            //Test whether Navigation can be performed in fragment
            fragment.testNavigateToAccount("Attendee" , navController);
        });
    }

    @Test
    public void testNavControllerWithTestNavHostControllerOrganizer() {
        TestNavHostController navController = new TestNavHostController(
                ApplicationProvider.getApplicationContext());

        FragmentScenario<AccountSelection> scenario = FragmentScenario.launchInContainer(AccountSelection.class);
        scenario.onFragment(fragment -> {
            // Set the graph on the TestNavHostController
            navController.setGraph(R.navigation.app_nav_graph);
            // Make the NavController available via the Fragment
            Navigation.setViewNavController(fragment.requireView(), navController);

            // Perform the navigation action
            navController.navigate(R.id.action_accountSelection_to_organizerActivity);

            // Assert the current destination is DetailFragment
            assertEquals(navController.getCurrentDestination().getId(), R.id.organizerActivity);
            //Test whether Navigation can be performed in fragment
            fragment.testNavigateToAccount("Organizer" , navController);
        });
    }

    @Test
    public void testNavControllerWithTestNavHostControllerAdmin() {
        TestNavHostController navController = new TestNavHostController(
                ApplicationProvider.getApplicationContext());

        FragmentScenario<AccountSelection> scenario = FragmentScenario.launchInContainer(AccountSelection.class);
        scenario.onFragment(fragment -> {
            // Set the graph on the TestNavHostController
            navController.setGraph(R.navigation.app_nav_graph);
            // Make the NavController available via the Fragment
            Navigation.setViewNavController(fragment.requireView(), navController);

            // Perform the navigation action
            navController.navigate(R.id.action_accountSelection_to_adminActivity);

            // Assert the current destination is DetailFragment
            assertEquals(navController.getCurrentDestination().getId(), R.id.adminActivity);
        });
    }

}