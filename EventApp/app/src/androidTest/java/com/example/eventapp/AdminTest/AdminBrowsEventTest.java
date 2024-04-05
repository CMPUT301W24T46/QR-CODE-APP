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
import com.example.eventapp.admin.AdminBrowseEvent;
import com.example.eventapp.admin.AdminBrowseProfile;
import com.example.eventapp.admin.AdminDeleteEvent;
import com.example.eventapp.attendee.AttendeeActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AdminBrowsEventTest {
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
    public void testEventSearch() {

        // Browse to Admin Browse Profile section
        onData(anything()).inAdapterView(withId(R.id.adminOptions)).atPosition(1).perform(click());

        // Intent Verification
        intended(hasComponent(AdminBrowseEvent.class.getName()));


        onView(withId(R.id.eventSearcher)).check(matches(isDisplayed())) ;

        onView(withId(R.id.eventSearcher))
                .perform(click()) // Click on the SearchView to expand it
                .perform(typeText("TestEvent1"), pressKey(KeyEvent.KEYCODE_ENTER));
    }

    @Test
    public void testDeleteEvent() {

        // Browse to Admin Browse Profile section
        onData(anything()).inAdapterView(withId(R.id.adminOptions)).atPosition(1).perform(click());

        // Intent Verification
        intended(hasComponent(AdminBrowseEvent.class.getName()));


        onView(withId(R.id.eventSearcher)).check(matches(isDisplayed())) ;

        onView(withId(R.id.eventSearcher))
                .perform(click()) // Click on the SearchView to expand it
                .perform(typeText("TestEvent1"), pressKey(KeyEvent.KEYCODE_ENTER));

        onView(withId(R.id.btnViewEvent)).check(matches(isDisplayed())) ;
        onView(withId(R.id.btnViewEvent)).perform(click()) ;

        intended(hasComponent(AdminDeleteEvent.class.getName()));

        onView(withId(R.id.btnDeleteEvent)).check(matches(isDisplayed())) ;
        onView(withId(R.id.btnDeleteEvent)).perform(click()) ;


        onView(withText(android.R.string.yes)).inRoot(isDialog()).check(matches(isDisplayed())).perform(click());
    }
}
