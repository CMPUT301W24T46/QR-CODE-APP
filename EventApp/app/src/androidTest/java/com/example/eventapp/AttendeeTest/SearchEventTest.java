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
    private LoginIdlingResource idlingResource;
    private SearchEventIdlingResource searchEventIdlingResource;

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp() {
        Intents.init();
        // Gets the Account Selection Fragment and registers it as an idling resource
        activityRule.getScenario().onActivity(activity -> {
            NavController navController = Navigation.findNavController(activity, R.id.fragmentContainerView);
            NavHostFragment navHostFragment = (NavHostFragment) activity.getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
            Fragment currentFragment = null;
            if (navHostFragment != null) {
                currentFragment = navHostFragment.getChildFragmentManager().getFragments().get(0);
            }
            AccountSelection fragment = (AccountSelection) currentFragment;
            idlingResource = new LoginIdlingResource(fragment);
        });
    }

    @After
    public void tearDown() {
        Intents.release();
        IdlingRegistry.getInstance().unregister(idlingResource);
        IdlingRegistry.getInstance().unregister(searchEventIdlingResource);
    }

    @Test
    public void searchEventTest() {
        // Have to make sure view is present
        onView(withId(R.id.accountOptionList)).check(matches(isDisplayed()));
        onData(anything())  // You can use a matcher here to match specific data
                .inAdapterView(withId(R.id.accountOptionList))
                .atPosition(0)
                .perform(click());
        // Wait for firestore to be communicated with
        IdlingRegistry.getInstance().register(idlingResource);
        intended(hasComponent(AttendeeActivity.class.getName()));

        onView(withId(R.id.bottomNavigationAttendeeView)).check(matches(isDisplayed())) ;
        onView(withId(R.id.attendeeEventMenu)).perform(click()) ;

        onView(withId(R.id.eventSearcher)).check(matches(isDisplayed())) ;

        onView(withId(R.id.eventSearcher))
                .perform(click()) // Click on the SearchView to expand it
                .perform(typeText("ABSDD"), pressKey(KeyEvent.KEYCODE_ENTER));

        IdlingRegistry.getInstance().register(searchEventIdlingResource);
    }

    @Test
    public void signUpEventTest() {
        // Have to make sure view is present
        onView(withId(R.id.accountOptionList)).check(matches(isDisplayed()));
        onData(anything())  // You can use a matcher here to match specific data
                .inAdapterView(withId(R.id.accountOptionList))
                .atPosition(0)
                .perform(click());
        // Wait for firestore to be communicated with
        IdlingRegistry.getInstance().register(idlingResource);
        intended(hasComponent(AttendeeActivity.class.getName()));

        onView(withId(R.id.bottomNavigationAttendeeView)).check(matches(isDisplayed())) ;
        onView(withId(R.id.attendeeEventMenu)).perform(click()) ;

        onView(withId(R.id.eventSearcher)).check(matches(isDisplayed())) ;

        onView(withId(R.id.eventSearcher))
                .perform(click()) // Click on the SearchView to expand it
                .perform(typeText("ABSDD"), pressKey(KeyEvent.KEYCODE_ENTER));


        onView(withId(R.id.btnViewEvent)).check(matches(isDisplayed())) ;
        onView(withId(R.id.btnViewEvent)).perform(click()) ;

        // Clicks the button and signs up for an event
        onView(withId(R.id.signUpForEventButton)).check(matches(isDisplayed())) ;
        onView(withId(R.id.signUpForEventButton)).perform(click()) ;

    }

    @Test
    public void seeSignedUpEventTest() {
        // Have to make sure view is present
        onView(withId(R.id.accountOptionList)).check(matches(isDisplayed()));
        onData(anything())  // You can use a matcher here to match specific data
                .inAdapterView(withId(R.id.accountOptionList))
                .atPosition(0)
                .perform(click());
        // Wait for firestore to be communicated with
        IdlingRegistry.getInstance().register(idlingResource);
        intended(hasComponent(AttendeeActivity.class.getName()));

        onView(withId(R.id.bottomNavigationAttendeeView)).check(matches(isDisplayed())) ;
        onView(withId(R.id.attendeeEventMenu)).perform(click()) ;

        onView(withId(R.id.eventSearcher)).check(matches(isDisplayed())) ;

        onView(withId(R.id.eventSearcher))
                .perform(click()) // Click on the SearchView to expand it
                .perform(typeText("ABSDD"), pressKey(KeyEvent.KEYCODE_ENTER));


        onView(withId(R.id.btnViewEvent)).check(matches(isDisplayed())) ;
        onView(withId(R.id.btnViewEvent)).perform(click()) ;

        // Clicks the signup button and check whether it is displayed
        onView(withId(R.id.signUpForEventButton)).check(matches(isDisplayed())) ;
        onView(withId(R.id.signUpForEventButton)).perform(click()) ;

        onView(withId(R.id.alreadySigneUpTextView)).check(matches(isDisplayed())) ;
    }

}
