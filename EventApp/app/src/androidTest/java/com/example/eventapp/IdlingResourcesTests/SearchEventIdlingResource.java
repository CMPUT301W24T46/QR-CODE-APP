package com.example.eventapp.IdlingResourcesTests;

import androidx.test.espresso.IdlingResource;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.eventapp.attendee.AttendeeActivity;
import com.example.eventapp.attendee.AttendeeEvent;
import com.example.eventapp.attendee.CustomizeProfile;

public class SearchEventIdlingResource implements IdlingResource{
    private AttendeeEvent attendeeEvent;
    private IdlingResource.ResourceCallback resourceCallback;

    public SearchEventIdlingResource(AttendeeEvent attendeeEvent) {
        this.attendeeEvent = attendeeEvent ;
    }
    @Override
    public String getName() {
        return SearchEventIdlingResource.class.getName();
    }

    @Override
    public boolean isIdleNow() {
        // Important part: checks if the login operation is in progress in the fragment
        if(attendeeEvent.isDoneSearching()){
            resourceCallback.onTransitionToIdle();
        }else{
        }
        return attendeeEvent.isDoneSearching();
    }

    @Override
    public void registerIdleTransitionCallback(IdlingResource.ResourceCallback callback) {
        this.resourceCallback = callback;
    }

    // You may need to notify the resource callback when the state transitions to idle
    public void onLoginFinished() {
        if (isIdleNow() && resourceCallback != null) {
            resourceCallback.onTransitionToIdle();
        }
    }
}
