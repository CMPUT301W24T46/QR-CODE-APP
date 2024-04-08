package com.example.eventapp.helpers;

import java.util.ArrayList;

public class CheckForEventHelper {
    public static boolean checkForEvent(String eventId , ArrayList<String> currentEventList){
        boolean containsItem = currentEventList.contains(eventId) ;
        if(containsItem){
            return true ;
        }
        return false ;
    }
}
