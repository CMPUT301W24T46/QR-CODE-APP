package com.example.eventapp.firestoreservice;

public interface UserNameCallback {
    void onCallback(String userName);
    void onError(Exception e);

    void onSuccessfulUpdate(boolean success) ;
}
