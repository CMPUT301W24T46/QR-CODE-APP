package com.example.eventapp.Organizer;

import static android.app.Activity.RESULT_OK;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.app.Instrumentation;
import android.content.Intent;
import android.view.KeyEvent;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.rule.GrantPermissionRule;

import com.example.eventapp.R;
import com.example.eventapp.attendee.AttendeeActivity;
import com.example.eventapp.organizer.OrganizerActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ReuseQrCodeTest {
    @Rule
    public ActivityScenarioRule<OrganizerActivity> activityRule = new ActivityScenarioRule<>(OrganizerActivity.class);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(android.Manifest.permission.CAMERA);

    @Rule
    public GrantPermissionRule permissionRuleFineLocation = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

    @Rule
    public GrantPermissionRule permissionRuleCoarseLocation = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_COARSE_LOCATION);


    @Before
    public void setUp() {
        // Initialize Espresso Intents before each test
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testReuseQrCode(){
        onView(withId(R.id.bottomNavigationOrganizerView)).check(matches(isDisplayed())) ;
        onView(withId(R.id.organizerEvent)).perform(click()) ;
        onView(withId(R.id.organizer_eventSearcher)).check(matches(isDisplayed())) ;

        String fakeQRResult = "{ \"eventId\": \"RweF1yGlUTfNsieVS14O\", \"qrCodeId\": \"9c07ec3e-23f3-4159-a20e-50b2e51a8093\", \"type\": \"EventInfo\" }";
        Intent resultData = new Intent();
        resultData.putExtra("SCAN_RESULT", fakeQRResult);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(RESULT_OK, resultData);

        intending(hasAction("com.google.zxing.client.android.SCAN")).respondWith(result);
        

        onView(withId(R.id.organizer_eventSearcher))
                .perform(click()) // Click on the SearchView to expand it
                .perform(typeText("Event 1"), pressKey(KeyEvent.KEYCODE_ENTER));

        onView(withId(R.id.btnViewEvent)).check(matches(isDisplayed())) ;
        onView(withId(R.id.btnViewEvent)).perform(click()) ;

        onView(withId(R.id.realTimeScanBtn)).check(matches(isDisplayed())) ;
        onView(withId(R.id.realTimeScanBtn)).perform(click()) ;

        // Verify the intent was sent
        intended(hasAction("com.google.zxing.client.android.SCAN"));
    }
}
