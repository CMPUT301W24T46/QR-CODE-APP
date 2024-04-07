package com.example.eventapp.helpers;


import com.example.eventapp.document_reference.DocumentReferenceChecker;
import com.google.firebase.firestore.DocumentReference;

import java.util.Locale;

/**
 * CheckCustomizeProfileData provides utility methods for validating and processing user profile data.
 * It includes checks for spaces in usernames, length constraints on event descriptions,
 * presence of letters in phone numbers, and determining the profile picture based on the username.
 */

public class CheckCustomizeProfileData {

    /**
     * Constructor for CheckCustomizeProfileData.
     */

    public CheckCustomizeProfileData(){

    }

    /**
     * Checks if the provided username does not contain spaces.
     *
     * @param username The username to check.
     * @return True if the username does not contain spaces, false otherwise.
     */

    public boolean doesNotContainsSpace(String username){
        if(username.contains(" ")){
            return false ;
        }

        return true ;
    }

    /**
     * Checks if the provided event description does not exceed a specified character limit.
     *
     * @param eventDescription The event description to check.
     * @return True if the length of the event description is within the limit, false otherwise.
     */

    public boolean limitEventDescription(String eventDescription){
        if(eventDescription.length() > 100){
            return false ;
        }
        return  true ;
    }

    /**
     * Checks if the provided string (meant to represent a phone number) contains only digits.
     *
     * @param phoneNumber The string to check.
     * @return True if the string contains only digits, false if it contains any letters or special characters.
     */

    public boolean containsLetters(String phoneNumber){
        String regex = "[0-9]+";

        // Check if the string matches the regex
        return phoneNumber.matches(regex);
    }

    /**
     * Determines the profile picture to be used based on the first character of the username.
     * This is a simplified method for illustrative purposes and divides the alphabet into three groups.
     *
     * @param username The username based on which the profile picture is determined.
     * @return A string representing the profile picture category ("First", "Second", or "Third").
     */

    public static String determineProfilePic(String username){
        String lowerCaseUsername = username.toLowerCase() ;

        if(username.equals("")){
            return "" ;
        }
        char firstChar = Character.toLowerCase(username.charAt(0));

        // Check if the first character falls between 'a' and 'h'
        if (firstChar >= 'a' && firstChar <= 'h') {
            return "First" ;
        } else if (firstChar >= 'i' && firstChar <= 'j'){
            return "Second" ;
        }

        return "Third" ;
    }
}
