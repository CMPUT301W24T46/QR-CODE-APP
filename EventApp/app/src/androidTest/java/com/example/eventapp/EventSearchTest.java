package com.example.eventapp;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.eventapp.attendee.AttendeeActivity;
import com.example.eventapp.attendee.CustomizeProfile;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

import androidx.appcompat.widget.SearchView;

public class EventSearchTest {

    @Rule
    public ActivityScenarioRule<AttendeeActivity> activityScenarioRule = new ActivityScenarioRule<>(AttendeeActivity.class);

    @Test
    public void testEditProfile() {
        onView(withId(R.id.attendeeEventMenu)).perform(click()) ;
        onData(anything()) // Use 'anything()' as the matcher to match any item in the ListView
                .inAdapterView(withId(R.id.eventListView)) // Replace 'your_list_view_id' with the actual ID of your ListView
                .atPosition(0) // Specify the position of the item you want to click
                .perform(click());
    }
}
