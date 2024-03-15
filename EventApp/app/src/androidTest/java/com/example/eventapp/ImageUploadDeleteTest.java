package com.example.eventapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.example.eventapp.attendee.CustomizeProfile;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ImageUploadDeleteTest {

    @Rule
    public ActivityScenarioRule<CustomizeProfile> activityScenarioRule = new ActivityScenarioRule<>(CustomizeProfile.class);


    @Test
    public void imageViewIsPresent() {
//        Check to see if profile pic is valid for user to uploadImage
        onView(withId(R.id.attendeeProfilePic)).check(matches(isDisplayed()));
    }

    @Test
    public void addButtonPresent() {
//        Checks to see if user has the option to upload profile picture
        onView(withId(R.id.CustomizeImage)).check(matches(isDisplayed()));
    }

    @Test
    public void deleteButtonPresent() {
//        Check to see if user has the option to delete a profile picture
        onView(withId(R.id.DeleteImage)).check(matches(isDisplayed()));
    }



}
