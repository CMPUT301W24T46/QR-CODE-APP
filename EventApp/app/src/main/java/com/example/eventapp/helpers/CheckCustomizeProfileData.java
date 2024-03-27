package com.example.eventapp.helpers;


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
}
