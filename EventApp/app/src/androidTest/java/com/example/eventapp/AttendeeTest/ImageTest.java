package com.example.eventapp.AttendeeTest;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anything;
import android.app.Instrumentation.ActivityResult;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.UiObjectNotFoundException;

import com.example.eventapp.AccountSelection;
import com.example.eventapp.IdlingResourcesTests.DeleteIdlingResource;
import com.example.eventapp.IdlingResourcesTests.LoginIdlingResource;
import com.example.eventapp.IdlingResourcesTests.SaveImageIdlingResource;
import com.example.eventapp.MainActivity;
import com.example.eventapp.R;
import com.example.eventapp.attendee.AttendeeActivity;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ImageTest {
    private LoginIdlingResource idlingResource;
    private DeleteIdlingResource deleteIdlingResource ;

    private SaveImageIdlingResource saveImageIdlingResource ;

    @Rule
    public ActivityScenarioRule<AttendeeActivity> activityRule = new ActivityScenarioRule<>(AttendeeActivity.class);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(android.Manifest.permission.CAMERA);

    @Rule
    public GrantPermissionRule permissionRuleFineLocation = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

    @Rule
    public GrantPermissionRule permissionRuleCoarseLocation = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_COARSE_LOCATION);

    @Before
    public void setUp() {
        Intents.init();
        // Gets the Account Selection Fragment and registers it as an idling resource
    }

    @After
    public void tearDown() {
        Intents.release();
        IdlingRegistry.getInstance().unregister(idlingResource);
    }

    @Test
    public void testImageUpload() throws InterruptedException, UiObjectNotFoundException {

        onView(withId(R.id.bottomNavigationAttendeeView)).check(matches(isDisplayed())) ;
        onView(withId(R.id.attendeeAccount)).perform(click()) ;

        onView(withId(R.id.btnCustomizeProfile)).check(matches(isDisplayed())) ;
        onView(withId(R.id.btnCustomizeProfile)).perform(click()) ;


        // Expected event to be seen when Edit button is clicked
        Matcher<Intent> expectedIntent = allOf(
                hasAction(Intent.ACTION_PICK),
                hasData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        );

        ActivityResult activityResult = createGalleryPickActivityResultStub();
        intending(expectedIntent).respondWith(activityResult);


        onView(withId(R.id.CustomizeImage)).check(matches(isDisplayed())) ;
        onView(withId(R.id.CustomizeImage)).perform(click()) ;

        // Checks whether Photo intent was opened
        intended(expectedIntent) ;
    }

    @Test
    public void testDeleteImage(){
//        onView(withId(R.id.accountOptionList)).check(matches(isDisplayed()));
//        onData(anything())  // You can use a matcher here to match specific data
//                .inAdapterView(withId(R.id.accountOptionList))
//                .atPosition(0)
//                .perform(click());
//        // Wait for firestore to be communicated with
//        IdlingRegistry.getInstance().register(idlingResource);
//        intended(hasComponent(AttendeeActivity.class.getName()));

        onView(withId(R.id.bottomNavigationAttendeeView)).check(matches(isDisplayed())) ;
        onView(withId(R.id.attendeeAccount)).perform(click()) ;

        onView(withId(R.id.btnCustomizeProfile)).check(matches(isDisplayed())) ;
        onView(withId(R.id.btnCustomizeProfile)).perform(click()) ;

        onView(withId(R.id.DeleteImage)).check(matches(isDisplayed())) ;
        onView(withId(R.id.DeleteImage)).perform(click()) ;

    }

    @Test
    public void testSaveUserInfo(){
//        onView(withId(R.id.accountOptionList)).check(matches(isDisplayed()));
//        onData(anything())  // You can use a matcher here to match specific data
//                .inAdapterView(withId(R.id.accountOptionList))
//                .atPosition(0)
//                .perform(click());
//        // Wait for firestore to be communicated with
//        IdlingRegistry.getInstance().register(idlingResource);
//        intended(hasComponent(AttendeeActivity.class.getName()));

        onView(withId(R.id.bottomNavigationAttendeeView)).check(matches(isDisplayed())) ;
        onView(withId(R.id.attendeeAccount)).perform(click()) ;

        onView(withId(R.id.btnCustomizeProfile)).check(matches(isDisplayed())) ;
        onView(withId(R.id.btnCustomizeProfile)).perform(click()) ;

        onView(withId(R.id.editTextTextEmailAddress)).perform(ViewActions.typeText("TestUsername"));
        onView(withId(R.id.editTextPhone)).perform(ViewActions.typeText("1234567890"));
        onView(withId(R.id.editTextTextMultiLine)).perform(ViewActions.typeText("TestDescription"));

        // Close soft keyboard
        Espresso.closeSoftKeyboard();

        // Click on the "Save" button
        onView(withId(R.id.AttendeeAccountSave)).perform(click());

    }

    private ActivityResult createGalleryPickActivityResultStub() {
        Uri imageUri = Uri.parse("Test");
        Intent resultIntent = new Intent();
        resultIntent.setData(imageUri);
        return new ActivityResult(Activity.RESULT_OK, resultIntent);
    }
}
