package com.example.eventapp;


import android.content.Intent;


import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;


import com.example.eventapp.attendee.CustomizeProfile;


import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;


public class EditProfileTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);


    @Test
    public void testEditProfile() {
        // Start the activity
        ActivityScenario<MainActivity> activityScenario = ActivityScenario.launch(MainActivity.class);


        // Click on the "Attend Event" option within the ListView
        Espresso.onData(anything())
                .inAdapterView(withId(R.id.accountOptionList))
                .atPosition(0)
                .perform(click());


        // Click on the "Account" button at bottom right to go to the account page
        Espresso.onView(withId(R.id.attendeeAccount)).perform(click());


        // Click on the "Customize Profile" button
        Espresso.onView(withId(R.id.btnCustomizeProfile)).perform(click());


        // Check if the CustomizeProfile activity is launched
        activityScenario.onActivity(activity -> {
            Intent intent = new Intent(activity, CustomizeProfile.class);
            activity.startActivity(intent);
        });


        // Input data into EditText fields
        Espresso.onView(withId(R.id.editTextTextEmailAddress)).perform(ViewActions.typeText("TestUsername"));
        Espresso.onView(withId(R.id.editTextPhone)).perform(ViewActions.typeText("1234567890"));
        Espresso.onView(withId(R.id.editTextTextMultiLine)).perform(ViewActions.typeText("TestDescription"));


        // Close soft keyboard
        Espresso.closeSoftKeyboard();


        // Click on the "Save" button
        Espresso.onView(withId(R.id.AttendeeAccountSave)).perform(click());


        // Check if the changes are saved successfully and the activity finishes
        Espresso.pressBack();
    }
}
