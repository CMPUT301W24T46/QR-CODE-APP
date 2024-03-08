package com.example.eventapp;

// Import statements
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;

import android.view.KeyEvent;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;


import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import com.example.eventapp.admin.AdminActivity;
import com.example.eventapp.admin.AdminBrowseEvent;
import com.example.eventapp.admin.AdminBrowseImage;
import com.example.eventapp.admin.AdminController;
import com.example.eventapp.admin.AdminDeleteEvent;
import com.example.eventapp.admin.AdminDeleteImage;
import com.example.eventapp.admin.AdminDeleteProfile;
import com.google.android.gms.tasks.Tasks;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;


import java.util.UUID;
import java.util.concurrent.ExecutionException;

@RunWith(AndroidJUnit4.class)
@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AdminInstrumentedTest {

    @Rule
    public IntentsTestRule<AdminActivity> intentsTestRule = new IntentsTestRule<>(AdminActivity.class);

    private AdminController adminController;
    private String randomId, newRandomId;
    private IdlingResource idlingResource;


    @Before
    public void setUp() {
        randomId = UUID.randomUUID().toString();
        // for testing searches


        // Get the current activity and pass it as context to AdminController
        AdminActivity currentActivity = intentsTestRule.getActivity();
        adminController = new AdminController(currentActivity);
        adminController.addMockData(randomId);

        // Register IdlingResource for synchronization in tests
        IdlingRegistry.getInstance().register(AdminController.idlingResource);
    }

    @After
    public void tearDown() {
        adminController.deleteMockdata(randomId);

        if (newRandomId != null) {
            adminController.deleteMockdata(newRandomId);

        }

        IdlingRegistry.getInstance().unregister(AdminController.idlingResource);

    }



//    @Test
//    public void testBrowseProfile() throws InterruptedException {
//
//        newRandomId = UUID.randomUUID().toString();
//
//        // Add one more user so that there is at least 2
//        adminController.addMockData(newRandomId);
//
//        // Browse to Admin Browse Profile section
//        onData(anything()).inAdapterView(withId(R.id.adminOptions)).atPosition(0).perform(click());
//
//        // Intentional delay for UI updates
//        try {
//            Thread.sleep(1000); // Consider replacing with IdlingResource in future
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        // Intent Verification
//        intended(hasComponent(AdminBrowseProfile.class.getName()));
//
//        // Capture count of list items before search
//        int initialCount = getCountFromListView(R.id.profileListView);
//
//        // Searching and asserting profile
//        onView(withId(R.id.profileSearcher)).perform(click(), typeText(randomId));
//
//
//        onData(anything())
//                .inAdapterView(withId(R.id.profileListView))
//                .atPosition(0) // Adjust position if needed
//                .onChildView(withId(R.id.profileName)) // Replace with the actual ID of the view button
//                .check(matches(withText(containsString(randomId))));
//
//        // Clear search query
//        for (int i = 0; i < randomId.length(); i++) {
//            onView(withId(R.id.profileSearcher)).perform(ViewActions.pressKey(KeyEvent.KEYCODE_DEL));
//        }
//
//        Thread.sleep(1000);
//
//
//        // Capture count of list items after clearing search
//        int finalCount = getCountFromListView(R.id.profileListView);
//
//        // Compare initial and final counts
//        assertEquals("Count should be the same before and after clearing search", initialCount, finalCount);
//
//    }

    @Test
    public void testDeleteProfile() throws InterruptedException {
        // Navigate to Admin Browse Profile section (reuse from testBrowseProfile)
        onData(anything()).inAdapterView(withId(R.id.adminOptions)).atPosition(0).perform(click());
        adminController = new AdminController(intentsTestRule.getActivity());

        // Intentional delay for UI updates
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.profileSearcher)).perform(click(), typeText(randomId));
        // Click on the view button next to the list item
        onData(anything())
                .inAdapterView(withId(R.id.profileListView))
                .atPosition(0) // Adjust position if needed
                .onChildView(withId(R.id.btnViewProfile))
                .perform(click());

        //  Assert that we have navigated to AdminDeleteProfile
        intended(hasComponent(AdminDeleteProfile.class.getName()));

        onView(withId(R.id.btnDeleteUser)).perform(click());
        // deletes the user (handled by the AdminController)
        onView(withText(android.R.string.yes)).inRoot(isDialog()).check(matches(isDisplayed())).perform(click());

        // wait for the database to sync
        Thread.sleep(2500);

//          check if the profile still exists
        try {
            // Wait for the search result
            Boolean exists = Tasks.await(adminController.searchForProfile(randomId));
            assertFalse("User should be deleted" + randomId, exists);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testBrowseEvent() throws InterruptedException {
        // Adding additional mock data for testing
        newRandomId = UUID.randomUUID().toString();
        adminController.addMockData(newRandomId);

        // Navigate to the 'Browse Event' section in the app
        onData(anything()).inAdapterView(withId(R.id.adminOptions)).atPosition(1).perform(click());
        Thread.sleep(1000); // Delay for UI update

        // Check if the correct activity is opened
        intended(hasComponent(AdminBrowseEvent.class.getName()));

        // Capture the initial count of items in the event list view
        int initialCount = getCountFromListView(R.id.eventListView);

        // Perform search operation in the event section
        onView(withId(R.id.eventSearcher)).perform(click(), typeText(randomId));

        // Validate that the searched item is displayed
        onData(anything())
                .inAdapterView(withId(R.id.eventListView))
                .atPosition(0)
                .onChildView(withId(R.id.eventDescription))
                .check(matches(withText(containsString(randomId))));

        // Clear the search query
        for (int i = 0; i < randomId.length(); i++) {
            onView(withId(R.id.eventSearcher)).perform(ViewActions.pressKey(KeyEvent.KEYCODE_DEL));
        }

        Thread.sleep(1000); // Delay for UI update after clearing search

        // Capture count of list items after clearing the search query
        int finalCount = getCountFromListView(R.id.eventListView);

        // Verify that the item count is the same before and after clearing the search
        assertEquals("Count should be the same before and after clearing search", initialCount, finalCount);
    }


    @Test
    public void testDeleteEvent() throws InterruptedException {
        // Navigate to Admin Browse Event section
        onData(anything()).inAdapterView(withId(R.id.adminOptions)).atPosition(1).perform(click());
        adminController = new AdminController(intentsTestRule.getActivity());

        // Intentional delay for UI updates
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.eventSearcher)).perform(click(), typeText(randomId));
        // Click on the view button next to the list item
        onData(anything())
                .inAdapterView(withId(R.id.eventListView))
                .atPosition(0) // Adjust position if needed
                .onChildView(withId(R.id.btnViewEvent))
                .perform(click());

        // Assert that we have navigated to AdminDeleteEvent
        intended(hasComponent(AdminDeleteEvent.class.getName()));

        onView(withId(R.id.btnDeleteEvent)).perform(click());
        // deletes the event (handled by the AdminController)
        onView(withText(android.R.string.yes)).inRoot(isDialog()).check(matches(isDisplayed())).perform(click());

        // wait for the database to sync
        Thread.sleep(2500);

        // check if the event still exists
        try {
            // Wait for the search result
            Boolean exists = Tasks.await(adminController.searchForEvent(randomId));
            assertFalse("Event should be deleted" + randomId, exists);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testBrowseImage() throws InterruptedException {
        // Adding additional mock data for testing
        newRandomId = UUID.randomUUID().toString();
        adminController.addMockData(newRandomId);

        // Navigate to the 'Browse Image' section in the app
        onData(anything()).inAdapterView(withId(R.id.adminOptions)).atPosition(2).perform(click());
        Thread.sleep(1000); // Delay for UI update

        // Check if the correct activity is opened
        intended(hasComponent(AdminBrowseImage.class.getName()));

        // Custom ViewAction to get the item count from the GridView's adapter
        AdapterItemCountAction itemCountAction = new AdapterItemCountAction();
        onView(withId(R.id.imageGridView)).perform(itemCountAction);
        int initialCount = itemCountAction.getItemCount();

        // Perform search operation in the image section
        onView(withId(R.id.imageSearcher)).perform(click(), typeText(randomId));
        Thread.sleep(1000); // Allow time for the search to take effect

        // Reapply the ViewAction to get the count after search
        onView(withId(R.id.imageGridView)).perform(itemCountAction);
        int countAfterSearch = itemCountAction.getItemCount();

        // Clear the search query
        for (int i = 0; i < randomId.length(); i++) {
            onView(withId(R.id.imageSearcher)).perform(ViewActions.pressKey(KeyEvent.KEYCODE_DEL));
        }
        Thread.sleep(1000); // Allow time for the clearing to take effect

        // Reapply the ViewAction to get the count after clearing the search query
        onView(withId(R.id.imageGridView)).perform(itemCountAction);
        int finalCount = itemCountAction.getItemCount();

        // Verify that the item count is the same before and after clearing the search
        assertEquals("Count should be the same before and after clearing search", initialCount, finalCount);
    }


    @Test
    public void testDeleteImage() throws InterruptedException {
        // Navigate to Admin Browse Image section
        onData(anything()).inAdapterView(withId(R.id.adminOptions)).atPosition(2).perform(click());
        adminController = new AdminController(intentsTestRule.getActivity());

        // Intentional delay for UI updates
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Search for the image using randomId
        onView(withId(R.id.imageSearcher)).perform(click(), typeText(randomId));

        onData(anything())
                .inAdapterView(withId(R.id.imageGridView))
                .atPosition(0)
                .onChildView(withId(R.id.gridImageItem))
                .perform(click());

        intended(hasComponent(AdminDeleteImage.class.getName()));


        onView(withId(R.id.btnDeleteImage)).perform(click());
        // Delete the image (handled by the AdminController)
        onView(withText(android.R.string.yes)).inRoot(isDialog()).check(matches(isDisplayed())).perform(click());

        // Wait for the database to sync
        Thread.sleep(2500);

        // Check if the image still exists
        try {
            // Wait for the search result
            Boolean exists = Tasks.await(adminController.searchForImage(randomId));
            assertFalse("Image should be deleted" + randomId, exists);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    /* --- HELPER FUNCTIONS *** */

    private int getCountFromListView(int listViewId) {
        final int[] count = {0};
        onView(withId(listViewId)).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isDisplayed();
            }

            @Override
            public String getDescription() {
                return "Get count from ListView";
            }

            @Override
            public void perform(UiController uiController, View view) {
                ListView listView = (ListView) view;
                count[0] = listView.getCount();
            }
        });
        return count[0];
    }


    private static class AdapterItemCountAction implements ViewAction {
        private int itemCount;

        @Override
        public Matcher<View> getConstraints() {
            return isAssignableFrom(GridView.class);
        }

        @Override
        public String getDescription() {
            return "Get item count from GridView's adapter";
        }

        @Override
        public void perform(UiController uiController, View view) {
            GridView gridView = (GridView) view;
            BaseAdapter adapter = (BaseAdapter) gridView.getAdapter();
            itemCount = adapter.getCount();
        }

        public int getItemCount() {
            return itemCount;
        }
    }


}
