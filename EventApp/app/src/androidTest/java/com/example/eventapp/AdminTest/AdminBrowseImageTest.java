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
import com.example.eventapp.admin.AdminBrowseImage;
import com.example.eventapp.admin.AdminBrowseProfile;
import com.example.eventapp.admin.AdminDeleteImage;
import com.example.eventapp.admin.AdminDeleteProfile;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AdminBrowseImageTest {
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
    public void testImageSearch() {

        // Browse to Admin Browse Profile section
        onData(anything()).inAdapterView(withId(R.id.adminOptions)).atPosition(2).perform(click());

        // Intent Verification
        intended(hasComponent(AdminBrowseImage.class.getName()));


        onView(withId(R.id.imageSearcher)).check(matches(isDisplayed())) ;

        onView(withId(R.id.imageSearcher))
                .perform(click()) // Click on the SearchView to expand it
                .perform(typeText("TestImage1"), pressKey(KeyEvent.KEYCODE_ENTER));
    }

    @Test
    public void testDeleteImage() {

        // Browse to Admin Browse Image section
        onData(anything()).inAdapterView(withId(R.id.adminOptions)).atPosition(2).perform(click());

        // Intent Verification
        intended(hasComponent(AdminBrowseImage.class.getName()));


        onView(withId(R.id.imageSearcher)).check(matches(isDisplayed())) ;

        onView(withId(R.id.imageSearcher))
                .perform(click()) // Click on the SearchView to expand it
                .perform(typeText("TestImage1"), pressKey(KeyEvent.KEYCODE_ENTER));

        // Click on the view button next to the list item
        onData(anything())
                .inAdapterView(withId(R.id.imageGridView))
                .atPosition(0) // Adjust position if needed
                .onChildView(withId(R.id.gridImageItem))
                .perform(click());

        //  Assert that we have navigated to AdminDeleteImage
        intended(hasComponent(AdminDeleteImage.class.getName()));

        onView(withId(R.id.btnDeleteImage)).perform(click());
        // deletes the image (handled by the AdminController)
        onView(withText(android.R.string.yes)).inRoot(isDialog()).check(matches(isDisplayed())).perform(click());
    }
}
