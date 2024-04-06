package com.example.eventapp.AdminTest;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

import android.view.KeyEvent;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.eventapp.R;
import com.example.eventapp.admin.AdminActivity;
import com.example.eventapp.admin.AdminBrowseProfile;
import com.example.eventapp.admin.AdminDeleteProfile;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AdminBrowseProfileTest {
    @Rule
    public ActivityScenarioRule<AdminActivity> activityRule = new ActivityScenarioRule<>(AdminActivity.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testProfileSearch() {

        // Browse to Admin Browse Profile section
        onData(anything()).inAdapterView(withId(R.id.adminOptions)).atPosition(0).perform(click());

        // Intent Verification
        intended(hasComponent(AdminBrowseProfile.class.getName()));


        onView(withId(R.id.profileSearcher)).check(matches(isDisplayed())) ;

        onView(withId(R.id.profileSearcher))
                .perform(click()) // Click on the SearchView to expand it
                .perform(typeText("TestProfile1"), pressKey(KeyEvent.KEYCODE_ENTER));
    }

    @Test
    public void testDeleteProfile() {

        // Browse to Admin Browse Profile section
        onData(anything()).inAdapterView(withId(R.id.adminOptions)).atPosition(0).perform(click());

        // Intent Verification
        intended(hasComponent(AdminBrowseProfile.class.getName()));


        onView(withId(R.id.profileSearcher)).check(matches(isDisplayed())) ;

        onView(withId(R.id.profileSearcher))
                .perform(click()) // Click on the SearchView to expand it
                .perform(typeText("TestProfile1"), pressKey(KeyEvent.KEYCODE_ENTER));

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
    }
}
