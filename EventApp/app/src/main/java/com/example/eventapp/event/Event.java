package com.example.eventapp.event;

public class Event {
    String eventName ;
    String imageDescription ;

    String imageURL ;

    public Event(String eventName , String imageDescription , String imageURL){
        this.eventName = eventName ;
        this.imageDescription = imageDescription ;
        this.imageURL = imageURL ;
    }

    public Event(String eventName, String imageURL){

        this.eventName = eventName ;
        this.imageURL = imageURL ;
    }
    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getImageDescription() {
        return imageDescription;
    }

    public void setImageDescription(String imageDescription) {
        this.imageDescription = imageDescription;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
