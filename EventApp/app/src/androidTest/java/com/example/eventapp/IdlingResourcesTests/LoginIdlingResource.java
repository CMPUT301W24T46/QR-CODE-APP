package com.example.eventapp.IdlingResourcesTests;

import android.util.Log;

import androidx.test.espresso.IdlingResource;

import com.example.eventapp.AccountSelection;

public class LoginIdlingResource implements IdlingResource {
    private AccountSelection accountSelectionFragment;
    private IdlingResource.ResourceCallback resourceCallback;

    public LoginIdlingResource(AccountSelection accountSelectionFragment) {
        this.accountSelectionFragment = accountSelectionFragment ;
    }
    @Override
    public String getName() {
        return LoginIdlingResource.class.getName();
    }

    @Override
    public boolean isIdleNow() {
        // Important part: checks if the login operation is in progress in the fragment
        if(!accountSelectionFragment.isLoginInProgress()){
            resourceCallback.onTransitionToIdle();
        }else{
        }
        return !accountSelectionFragment.isLoginInProgress();
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
