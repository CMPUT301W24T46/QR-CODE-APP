package com.example.eventapp.AttendeeTest;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.anything;

import android.view.KeyEvent;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.UiObjectNotFoundException;

import com.example.eventapp.AccountSelection;
import com.example.eventapp.IdlingResourcesTests.LoginIdlingResource;
import com.example.eventapp.IdlingResourcesTests.SearchEventIdlingResource;
import com.example.eventapp.MainActivity;
import com.example.eventapp.R;
import com.example.eventapp.attendee.AttendeeActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SearchEventTest {
    @Rule
    public ActivityScenarioRule<AttendeeActivity> activityRule = new ActivityScenarioRule<>(AttendeeActivity.class);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(android.Manifest.permission.CAMERA);

    @Rule
    public GrantPermissionRule permissionRuleFineLocation = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

    @Rule
    public GrantPermissionRule permissionRuleCoarseLocation = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_COARSE_LOCATION);
    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testEventSearch() {

        onView(withId(R.id.bottomNavigationAttendeeView)).check(matches(isDisplayed())) ;
        onView(withId(R.id.attendeeEventMenu)).perform(click()) ;

        onView(withId(R.id.eventSearcher)).check(matches(isDisplayed())) ;

        onView(withId(R.id.eventSearcher))
                .perform(click()) // Click on the SearchView to expand it
                .perform(typeText("First Event"), pressKey(KeyEvent.KEYCODE_ENTER));
    }

    @Test
    public void testEventViewInfo() {

        onView(withId(R.id.bottomNavigationAttendeeView)).check(matches(isDisplayed())) ;
        onView(withId(R.id.attendeeEventMenu)).perform(click()) ;

        onView(withId(R.id.eventSearcher)).check(matches(isDisplayed())) ;

        onView(withId(R.id.eventSearcher))
                .perform(click()) // Click on the SearchView to expand it
                .perform(typeText("First Event"), pressKey(KeyEvent.KEYCODE_ENTER));

        onView(withId(R.id.btnViewEvent)).check(matches(isDisplayed())) ;
        onView(withId(R.id.btnViewEvent)).perform(click()) ;
    }

    @Test
    public void testEventSignUp() {

        onView(withId(R.id.bottomNavigationAttendeeView)).check(matches(isDisplayed())) ;
        onView(withId(R.id.attendeeEventMenu)).perform(click()) ;

        onView(withId(R.id.eventSearcher)).check(matches(isDisplayed())) ;

        onView(withId(R.id.eventSearcher))
                .perform(click()) // Click on the SearchView to expand it
                .perform(typeText("First Event"), pressKey(KeyEvent.KEYCODE_ENTER));

        onView(withId(R.id.btnViewEvent)).check(matches(isDisplayed())) ;
        onView(withId(R.id.btnViewEvent)).perform(click()) ;

        // Clicks the signup button and check whether it is displayed
        onView(withId(R.id.signUpForEventButton)).check(matches(isDisplayed())) ;
        onView(withId(R.id.signUpForEventButton)).perform(click()) ;
    }

    @Test
    public void testEventAlreadySignedUp() {

        onView(withId(R.id.bottomNavigationAttendeeView)).check(matches(isDisplayed())) ;
        onView(withId(R.id.attendeeEventMenu)).perform(click()) ;

        onView(withId(R.id.eventSearcher)).check(matches(isDisplayed())) ;

        onView(withId(R.id.eventSearcher))
                .perform(click()) // Click on the SearchView to expand it
                .perform(typeText("First Event"), pressKey(KeyEvent.KEYCODE_ENTER));

        onView(withId(R.id.btnViewEvent)).check(matches(isDisplayed())) ;
        onView(withId(R.id.btnViewEvent)).perform(click()) ;

        // Clicks the signup button and check whether it is displayed
        onView(withId(R.id.signUpForEventButton)).check(matches(isDisplayed())) ;
        onView(withId(R.id.signUpForEventButton)).perform(click()) ;

        // Checks if Already SignedUp Text is displayed
        onView(withId(R.id.alreadySigneUpTextView)).check(matches(isDisplayed())) ;
    }
}
