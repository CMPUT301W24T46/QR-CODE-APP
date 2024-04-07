package com.example.eventapp.firestoreservice;

/**
 * UserNameCallback is an interface defining methods to handle responses from Firestore operations related to user names.
 * Implementations of this interface can manage responses from retrieving a user's name, updating a user's information,
 * and handling any errors that may occur during these operations.
 */

public interface UserNameCallback {

    /**
     * Called when a user's name has been successfully retrieved from Firestore. This method should contain
     * the logic that needs to be executed after the successful retrieval of a user's name.
     *
     * @param userName The name of the user that was retrieved.
     */

    void onCallback(String userName);

    /**
     * Called when an error occurs during a Firestore operation related to user names. Implementations
     * of this method should handle the error, such as by logging it or displaying a user-friendly message.
     *
     * @param e The exception representing the error that occurred.
     */

    void onError(Exception e);

    /**
     * Called when a user's information has been successfully updated in Firestore. This method should contain
     * the logic that needs to be executed after the successful update of a user's information.
     *
     * @param success A boolean indicating the success of the update operation.
     */

    void onSuccessfulUpdate(boolean success) ;
}
