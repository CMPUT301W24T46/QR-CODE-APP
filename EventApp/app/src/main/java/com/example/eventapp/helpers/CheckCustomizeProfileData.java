package com.example.eventapp.helpers;


import com.example.eventapp.document_reference.DocumentReferenceChecker;
import com.google.firebase.firestore.DocumentReference;

import java.util.Locale;

public class CheckCustomizeProfileData {

    public CheckCustomizeProfileData(){

    }
    public boolean doesNotContainsSpace(String username){
        if(username.contains(" ")){
            return false ;
        }

        return true ;
    }

    public boolean limitEventDescription(String eventDescription){
        if(eventDescription.length() > 100){
            return false ;
        }
        return  true ;
    }

    public boolean containsLetters(String phoneNumber){
        String regex = "[0-9]+";

        // Check if the string matches the regex
        return phoneNumber.matches(regex);
    }

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
