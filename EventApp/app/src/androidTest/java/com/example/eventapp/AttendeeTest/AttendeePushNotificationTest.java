package com.example.eventapp.AttendeeTest;

import static org.hamcrest.Matchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.app.NotificationManager;
import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import com.example.eventapp.attendee.AttendeeActivity;
import com.example.eventapp.firestoreservice.NotificationSend;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AttendeePushNotificationTest {

    @Rule
    public ActivityScenarioRule<AttendeeActivity> activityRule = new ActivityScenarioRule<>(AttendeeActivity.class);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(android.Manifest.permission.CAMERA);

    @Rule
    public GrantPermissionRule permissionRuleFineLocation = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

    @Rule
    public GrantPermissionRule permissionRuleCoarseLocation = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_COARSE_LOCATION);
    @Test
    public void sendNotification_createsCorrectNotification() {
        // Context of the app under test.
        Context appContext = ApplicationProvider.getApplicationContext();

        // Define your test parameters
        String channelId = "test_channel_id";
        String title = "Event App";
        String content = "This is a test notification content.";

        // Call your method to send a test notification
        NotificationSend.testNotification(appContext, channelId, title, content);

    }
}
