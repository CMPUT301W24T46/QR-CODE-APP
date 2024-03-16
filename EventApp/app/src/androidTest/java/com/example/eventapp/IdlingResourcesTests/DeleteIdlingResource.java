package com.example.eventapp.IdlingResourcesTests;

import androidx.test.espresso.IdlingResource;

import com.example.eventapp.attendee.CustomizeProfile;

public class DeleteIdlingResource implements IdlingResource {
    private CustomizeProfile customizeProfile;
    private IdlingResource.ResourceCallback resourceCallback;

    public DeleteIdlingResource(CustomizeProfile customizeProfile) {
        this.customizeProfile = customizeProfile ;
    }
    @Override
    public String getName() {
        return LoginIdlingResource.class.getName();
    }

    @Override
    public boolean isIdleNow() {
        // Important part: checks if the login operation is in progress in the fragment
        if(customizeProfile.isDeleting()){
            resourceCallback.onTransitionToIdle();
        }else{
        }
        return customizeProfile.isDeleting();
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
