package com.example.eventapp.Organizer;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.eventapp.attendee.AttendeeActivity;
import com.example.eventapp.organizer.OrganizerActivity;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anything;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.KeyEvent;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.eventapp.R;
import com.example.eventapp.attendee.AttendeeActivity;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CreateEventTest {
    @Rule
    public ActivityScenarioRule<OrganizerActivity> activityRule = new ActivityScenarioRule<>(OrganizerActivity.class);

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
    public void testCreateEvent(){
//        button_createEvent
        onView(withId(R.id.button_createEvent)).perform(click()) ;
        onView(withText("Write the Event Name to Create an event upon submission a unique QR Code ")).check(matches(isDisplayed()));
        onView(withId(R.id.EditEventName))
                .perform(click())
                .perform(replaceText("Test Event Name"), ViewActions.closeSoftKeyboard());

        // Skip setting the date as it requires a custom solution to interact with the DatePickerDialog

        // Input for the event description
        onView(withId(R.id.EditEventDescription))
                .perform(click())
                .perform(replaceText("Test Event Description"), ViewActions.closeSoftKeyboard());

        // Input for attendee limit
        onView(withId(R.id.LimitAttendeesView))
                .perform(click())
                .perform(replaceText("100"), ViewActions.closeSoftKeyboard());

        // Click the confirm button
        onView(withText("CONFIRM")).perform(click());
    }

    @Test
    public void testEventLimit(){
        //        button_createEvent
        onView(withId(R.id.button_createEvent)).perform(click()) ;
        onView(withText("Write the Event Name to Create an event upon submission a unique QR Code ")).check(matches(isDisplayed()));
        onView(withId(R.id.LimitAttendeesView)).check(matches(isDisplayed())) ;

        // Expected event to be seen when Edit button is clicked
        Matcher<Intent> expectedIntent = allOf(
                hasAction(Intent.ACTION_PICK),
                hasData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        );

    }
//
    @Test
    public void testUploadEventPoster(){
        onView(withId(R.id.button_createEvent)).perform(click()) ;
        onView(withText("Write the Event Name to Create an event upon submission a unique QR Code ")).check(matches(isDisplayed()));
        onView(withId(R.id.EventImageView)).check(matches(isDisplayed()));
    }

    @Test
    public void testGenerateQrCode(){
        onView(withId(R.id.bottomNavigationOrganizerView)).check(matches(isDisplayed())) ;
        onView(withId(R.id.organizerEvent)).perform(click()) ;
        onView(withId(R.id.organizer_eventSearcher)).check(matches(isDisplayed())) ;

        onView(withId(R.id.organizer_eventSearcher))
                .perform(click()) // Click on the SearchView to expand it
                .perform(typeText("Event 1"), pressKey(KeyEvent.KEYCODE_ENTER));

        onView(withId(R.id.btnViewEvent)).check(matches(isDisplayed())) ;
        onView(withId(R.id.btnViewEvent)).perform(click()) ;

        onView(withId(R.id.button_editEvent_info)).check(matches(isDisplayed())) ;
        onView(withId(R.id.button_editEvent_info)).perform(click()) ;

        onView(withId(R.id.btn_QRCode)).check(matches(isDisplayed())) ;
        onView(withId(R.id.btn_QRCode)).perform(click()) ;
    }



    @Test
    public void testGeneratePromotionCode(){
        onView(withId(R.id.bottomNavigationOrganizerView)).check(matches(isDisplayed())) ;
        onView(withId(R.id.organizerEvent)).perform(click()) ;
        onView(withId(R.id.organizer_eventSearcher)).check(matches(isDisplayed())) ;

        onView(withId(R.id.organizer_eventSearcher))
                .perform(click()) // Click on the SearchView to expand it
                .perform(typeText("Event 1"), pressKey(KeyEvent.KEYCODE_ENTER));

        onView(withId(R.id.btnViewEvent)).check(matches(isDisplayed())) ;
        onView(withId(R.id.btnViewEvent)).perform(click()) ;

        onView(withId(R.id.button_editEvent_info)).check(matches(isDisplayed())) ;
        onView(withId(R.id.button_editEvent_info)).perform(click()) ;

        onView(withId(R.id.btn_qrcode_eventinfo)).check(matches(isDisplayed())) ;
        onView(withId(R.id.btn_qrcode_eventinfo)).perform(click()) ;
    }

    @Test
    public void testSeeListOfAttendees(){
        onView(withId(R.id.bottomNavigationOrganizerView)).check(matches(isDisplayed())) ;
        onView(withId(R.id.organizerEvent)).perform(click()) ;
        onView(withId(R.id.organizer_eventSearcher)).check(matches(isDisplayed())) ;

        onView(withId(R.id.organizer_eventSearcher))
                .perform(click()) // Click on the SearchView to expand it
                .perform(typeText("Event 1"), pressKey(KeyEvent.KEYCODE_ENTER));

        onView(withId(R.id.btnViewEvent)).check(matches(isDisplayed())) ;
        onView(withId(R.id.btnViewEvent)).perform(click()) ;

        onView(withId(R.id.button_editEvent_info)).check(matches(isDisplayed())) ;
        onView(withId(R.id.button_editEvent_info)).perform(click()) ;

        onView(withId(R.id.btn_listAttendee)).check(matches(isDisplayed())) ;
        onView(withId(R.id.btn_listAttendee)).perform(click()) ;

        onView(withId(R.id.spinner_eventActions)).check(matches(isDisplayed())) ;
        onView(withId(R.id.spinner_eventActions)).perform(click());
        onView(withText("Sign-ups")).perform(click());
    }

    @Test
    public void testNotifyAttendees(){
        onView(withId(R.id.bottomNavigationOrganizerView)).check(matches(isDisplayed())) ;
        onView(withId(R.id.organizerEvent)).perform(click()) ;
        onView(withId(R.id.organizer_eventSearcher)).check(matches(isDisplayed())) ;

        onView(withId(R.id.organizer_eventSearcher))
                .perform(click()) // Click on the SearchView to expand it
                .perform(typeText("Event 1"), pressKey(KeyEvent.KEYCODE_ENTER));

        onView(withId(R.id.btnViewEvent)).check(matches(isDisplayed())) ;
        onView(withId(R.id.btnViewEvent)).perform(click()) ;

        onView(withId(R.id.button_editEvent_info)).check(matches(isDisplayed())) ;
        onView(withId(R.id.button_editEvent_info)).perform(click()) ;

        onView(withId(R.id.btn_listAttendee)).check(matches(isDisplayed())) ;
        onView(withId(R.id.btn_listAttendee)).perform(click()) ;

        onView(withId(R.id.spinner_eventActions)).check(matches(isDisplayed())) ;
        onView(withId(R.id.spinner_eventActions)).perform(click());
        onView(withText("Sign-ups")).perform(click());

        onView(withId(R.id.button_notifyAttendees)).check(matches(isDisplayed())) ;
        onView(withId(R.id.button_notifyAttendees)).perform(click());

        onView(withText("Maximum 1000 characters including spaces")).check(matches(isDisplayed()));
        onView(withId(R.id.EditEventDescription))
                .perform(click())
                .perform(replaceText("Test Event Name"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.buttonConfirm)).perform(click());
    }

    @Test
    public void testViewNumberOfCheckIns(){
        onView(withId(R.id.bottomNavigationOrganizerView)).check(matches(isDisplayed())) ;
        onView(withId(R.id.organizerEvent)).perform(click()) ;
        onView(withId(R.id.organizer_eventSearcher)).check(matches(isDisplayed())) ;

        onView(withId(R.id.organizer_eventSearcher))
                .perform(click()) // Click on the SearchView to expand it
                .perform(typeText("Event 1"), pressKey(KeyEvent.KEYCODE_ENTER));

        onView(withId(R.id.btnViewEvent)).check(matches(isDisplayed())) ;
        onView(withId(R.id.btnViewEvent)).perform(click()) ;

        onView(withId(R.id.button_editEvent_info)).check(matches(isDisplayed())) ;
        onView(withId(R.id.button_editEvent_info)).perform(click()) ;

        onView(withId(R.id.btn_listAttendee)).check(matches(isDisplayed())) ;
        onView(withId(R.id.btn_listAttendee)).perform(click()) ;

        onView(withId(R.id.spinner_eventActions)).check(matches(isDisplayed())) ;
        onView(withId(R.id.spinner_eventActions)).perform(click());
        onView(withText("Check-ins")).perform(click());
    }

    private Instrumentation.ActivityResult createGalleryPickActivityResultStub() {
        Uri imageUri = Uri.parse("Test");
        Intent resultIntent = new Intent();
        resultIntent.setData(imageUri);
        return new Instrumentation.ActivityResult(Activity.RESULT_OK, resultIntent);
    }

}
