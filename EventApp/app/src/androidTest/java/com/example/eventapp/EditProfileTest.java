package com.example.eventapp;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.eventapp.attendee.CustomizeProfile;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class EditProfileTest {

    @Rule
    public ActivityScenarioRule<CustomizeProfile> activityScenarioRule = new ActivityScenarioRule<>(CustomizeProfile.class);

    @Test
    public void testEditProfile() {
        // Input data into EditText fields
        onView(withId(R.id.editTextTextEmailAddress)).perform(ViewActions.typeText("TestUsername"));
        onView(withId(R.id.editTextPhone)).perform(ViewActions.typeText("1234567890"));
        onView(withId(R.id.editTextTextMultiLine)).perform(ViewActions.typeText("TestDescription"));

        // Close soft keyboard
        Espresso.closeSoftKeyboard();

        // Click on the "Save" button
        onView(withId(R.id.AttendeeAccountSave)).perform(click());

        // Check if the changes are saved successfully and the activity finishes
//        Espresso.pressBack();
    }
}
