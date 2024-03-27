package com.example.eventapp.IdlingResourcesTests;

import android.util.Log;

import androidx.test.espresso.IdlingResource;

import com.example.eventapp.attendee.CustomizeProfile;

public class SaveImageIdlingResource implements IdlingResource{
    private CustomizeProfile customizeProfile;
    private IdlingResource.ResourceCallback resourceCallback;

    public SaveImageIdlingResource(CustomizeProfile customizeProfile) {
        this.customizeProfile = customizeProfile ;
    }
    @Override
    public String getName() {
        return SaveImageIdlingResource.class.getName();
    }

    @Override
    public boolean isIdleNow() {
        // Important part: checks if the login operation is in progress in the fragment
        if(customizeProfile.isSaving()){
            resourceCallback.onTransitionToIdle();
            Log.d("Save Image" , "Is Idle") ;
        }else{
        }
        return customizeProfile.isSaving();
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
