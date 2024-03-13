package com.example.eventapp.event;

public class Event {
    String eventName ;
    String imageDescription ;
    String imageURL ;
    String eventDate;
    String creatorId;
    String description;

    public Event(String eventName , String imageDescription , String imageURL){
        this.eventName = eventName ;
        this.imageDescription = imageDescription ;
        this.imageURL = imageURL ;
    }

    public Event(String eventName, String imageURL){

        this.eventName = eventName ;
        this.imageURL = imageURL ;
    }

    public Event(String eventName , String eventDate , String imageURL, String creatorId, String description){
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.imageURL = imageURL;
        this.creatorId = creatorId;
        this.description = description;
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

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }
}
