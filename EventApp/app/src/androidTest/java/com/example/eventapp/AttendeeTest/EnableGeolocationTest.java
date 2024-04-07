package com.example.eventapp.AttendeeTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import com.example.eventapp.R;
import com.example.eventapp.attendee.AttendeeActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class EnableGeolocationTest {

    @Rule
    public ActivityScenarioRule<AttendeeActivity> activityRule = new ActivityScenarioRule<>(AttendeeActivity.class);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(android.Manifest.permission.CAMERA);

    @Rule
    public GrantPermissionRule permissionRuleFineLocation = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

    @Rule
    public GrantPermissionRule permissionRuleCoarseLocation = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_COARSE_LOCATION);

    @Test
    public void testGeolocation(){
        onView(withId(R.id.bottomNavigationAttendeeView)).check(matches(isDisplayed())) ;
        onView(withId(R.id.attendeeAccount)).perform(click()) ;

        onView(withId(R.id.btnCustomizeProfile)).check(matches(isDisplayed())) ;
        onView(withId(R.id.btnCustomizeProfile)).perform(click()) ;


        onView(withId(R.id.switch_enable_geolocation)).check(matches(isDisplayed())) ;
        // Assume the switch starts in the OFF position.
        onView(withId(R.id.switch_enable_geolocation)).check(matches(isNotChecked()));

        // Click the switch to toggle it ON.
        onView(withId(R.id.switch_enable_geolocation)).perform(click());

        // Verify the switch is working
        onView(withId(R.id.switch_enable_geolocation))
                .check(matches(isChecked()));

    }
}
