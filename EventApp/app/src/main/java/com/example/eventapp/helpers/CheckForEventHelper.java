package com.example.eventapp.helpers;

import java.util.ArrayList;

/**
 * CheckForEventHelper provides utility methods to check for the presence of a specific event ID
 * within a list of event IDs. This can be useful in scenarios such as determining if a user is
 * already signed up for or interested in an event.
 */

public class CheckForEventHelper {

    /**
     * Checks if the provided event ID is present in the given list of event IDs.
     *
     * @param eventId The ID of the event to check for.
     * @param currentEventList A list of event IDs to search through.
     * @return True if the event ID is found in the list, false otherwise.
     */
    
    public static boolean checkForEvent(String eventId , ArrayList<String> currentEventList){
        boolean containsItem = currentEventList.contains(eventId) ;
        if(containsItem){
            return true ;
        }
        return false ;
    }
}
