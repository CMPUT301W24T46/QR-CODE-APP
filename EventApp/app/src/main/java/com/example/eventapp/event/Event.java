package com.example.eventapp.event;

public class Event {
    String eventName ;
    String imageDescription ;

    byte[] imagePhoto ;

    public Event(String eventName , String imageDescription , byte[] imagePhoto){
        this.eventName = eventName ;
        this.imageDescription = imageDescription ;
        this.imagePhoto = imagePhoto ;
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

    public byte[] getImagePhoto() {
        return imagePhoto;
    }

    public void setImagePhoto(byte[] imagePhoto) {
        this.imagePhoto = imagePhoto;
    }
}
