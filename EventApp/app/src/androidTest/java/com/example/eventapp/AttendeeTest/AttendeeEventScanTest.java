package com.example.eventapp.AttendeeTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.core.AllOf.allOf;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.eventapp.R;
import com.example.eventapp.admin.AdminActivity;
import com.example.eventapp.attendee.AttendeeActivity;
import com.example.eventapp.attendee.QRCodeScannerActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AttendeeEventScanTest {

    @Rule
    public ActivityScenarioRule<QRCodeScannerActivity> activityRule = new ActivityScenarioRule<>(QRCodeScannerActivity.class);

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
    public void testSelectImageFromGallery() {
        // Create a result to return when an image is picked
        Intent resultData = new Intent();
        Uri imageUri = Uri.parse("android.resource://com.example.eventapp/drawable/example_image");
        resultData.setData(imageUri);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);

        // Stub out the gallery intent
        intending(hasAction(Intent.ACTION_PICK)).respondWith(result);

        // Trigger the gallery button click
        // You would use Espresso to click on the gallery button here, e.g.,
        onView(withId(R.id.galleryButton)).perform(click());

        // Verify the intent was sent out to pick an image
        intended(allOf(hasAction(Intent.ACTION_PICK), hasAction(Intent.ACTION_GET_CONTENT), hasAction(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString())));
    }
}
