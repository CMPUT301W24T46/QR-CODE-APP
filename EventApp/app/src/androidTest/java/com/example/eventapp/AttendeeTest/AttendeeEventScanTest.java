package com.example.eventapp.AttendeeTest;

import static android.app.Activity.RESULT_OK;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import static org.hamcrest.core.AllOf.allOf;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import com.example.eventapp.R;
import com.example.eventapp.admin.AdminActivity;
import com.example.eventapp.attendee.AttendeeActivity;
import com.example.eventapp.attendee.QRCodeScanFragment;
import com.example.eventapp.attendee.QRCodeScannerActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.journeyapps.barcodescanner.CaptureActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AttendeeEventScanTest {

    @Rule
    public ActivityScenarioRule<AttendeeActivity> activityRule = new ActivityScenarioRule<>(AttendeeActivity.class);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(android.Manifest.permission.CAMERA);


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
    public void testScanEvent() {
        onView(withId(R.id.join_event)).check(matches(isDisplayed())) ;
        onView(withId(R.id.join_event)).perform(click()) ;

        // Stub the intent that ZXing library would use to start the CaptureActivity
        String fakeQRResult = "{ \"eventId\": \"RweF1yGlUTfNsieVS14O\", \"qrCodeId\": \"9c07ec3e-23f3-4159-a20e-50b2e51a8093\", \"type\": \"EventInfo\" }";
        Intent resultData = new Intent();
        resultData.putExtra("SCAN_RESULT", fakeQRResult);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(RESULT_OK, resultData);

        intending(hasAction("com.google.zxing.client.android.SCAN")).respondWith(result);

        // Trigger the scan button click
        onView(withId(R.id.testButton)).perform(click());

        // Verify the intent was sent
        intended(hasAction("com.google.zxing.client.android.SCAN"));

    }

}
