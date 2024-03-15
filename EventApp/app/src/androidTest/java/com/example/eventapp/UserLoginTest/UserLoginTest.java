package com.example.eventapp.UserLoginTest;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import static org.hamcrest.CoreMatchers.anything;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.eventapp.AccountSelection;
import com.example.eventapp.IdlingResourcesTests.LoginIdlingResource;
import com.example.eventapp.MainActivity;
import com.example.eventapp.R;
import com.example.eventapp.admin.AdminActivity;
import com.example.eventapp.attendee.AttendeeActivity;
import com.example.eventapp.organizer.OrganizerActivity;

import org.junit.Test;

@RunWith(AndroidJUnit4.class)
public class UserLoginTest {

    private LoginIdlingResource idlingResource;

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
    }

    @Test
    public void testLoginProcessAttendee() {
        // Have to make sure view is present
        onView(withId(R.id.accountOptionList)).check(matches(isDisplayed()));
        onData(anything())  // You can use a matcher here to match specific data
        .inAdapterView(withId(R.id.accountOptionList))
        .atPosition(0)
        .perform(click());
        // Wait for firestore to be communicated with
        IdlingRegistry.getInstance().register(idlingResource);
        intended(hasComponent(AttendeeActivity.class.getName()));
    }

    @Test
    public void testLoginProcessOrganizer(){
        // Have to make sure view is present
        onView(withId(R.id.accountOptionList)).check(matches(isDisplayed()));
        onData(anything())  // You can use a matcher here to match specific data
                .inAdapterView(withId(R.id.accountOptionList))
                .atPosition(1)
                .perform(click());
        // Wait for firestore to be communicated with
        IdlingRegistry.getInstance().register(idlingResource);
        intended(hasComponent(OrganizerActivity.class.getName()));
    }

    @Test
    public void testLoginProcessAdmin(){
        // Have to make sure view is present
        onView(withId(R.id.accountOptionList)).check(matches(isDisplayed()));
        onData(anything())  // You can use a matcher here to match specific data
                .inAdapterView(withId(R.id.accountOptionList))
                .atPosition(2)
                .perform(click());
        // Wait for firestore to be communicated with
        IdlingRegistry.getInstance().register(idlingResource);
        intended(hasComponent(AdminActivity.class.getName()));
    }
}
