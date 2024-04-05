package com.example.eventapp.event;

public class Event {
    String eventName ;
    String imageDescription ;
    String imageURL ;
    String eventDate;
    String creatorId;
    String eventDescription;
    private String eventId;
    private String qrcodeId;
    private Integer attendeeLimit;


//    public Event(String eventName , String imageDescription , String imageURL){
//        this.eventName = eventName ;
//        this.imageDescription = imageDescription ;
//        this.imageURL = imageURL ;
//    }
    public Event(String eventName, String imageURL, Integer attendeeLimit) {
        this.eventName = eventName;
        this.imageURL = imageURL;
        this.attendeeLimit = attendeeLimit;
    }

    public Event(String eventName, String eventDate, String imageURL, Integer attendeeLimit) {
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.imageURL = imageURL;
        this.attendeeLimit = attendeeLimit;
    }
    public Event(String eventName, String imageURL){
        this.eventName = eventName ;
        this.imageURL = imageURL ;
    }

    public Event(String eventName, String imageURL , String eventId){
        this.eventName = eventName ;
        this.imageURL = imageURL ;
        this.eventId = eventId;
    }
    public Event(String eventName , String eventDate , String imageURL, String eventDescription){
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.imageURL = imageURL;
        this.eventDescription = eventDescription;
    }
    public Event(String eventName , String eventDate , String imageURL, String eventDescription, Integer attendeeLimit){
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.imageURL = imageURL;
        this.eventDescription = eventDescription;
        this.attendeeLimit = attendeeLimit;
    }

    public Event(String eventName , String eventDate , String imageURL, String eventId, String eventDescription){
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.imageURL = imageURL;
        this.eventId = eventId;
        this.eventDescription = eventDescription;
    }
    public Event(String eventName , String eventDate , String imageURL, String creatorId, String eventDescription, String eventId){
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.imageURL = imageURL;
        this.creatorId = creatorId;
        this.eventDescription = eventDescription;
        this.eventId = eventId;
    }
    public Event(String eventName){
        this.eventName = eventName;
    }
    // No-argument constructor required for Firebase
    public Event() {
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

    public String getEventDescription(){
        return eventDescription;
    }

    public void setEventDescription(String eventDescription){
        this.eventDescription = eventDescription;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getQrcodeId() {
        return qrcodeId;
    }

    public void setQrcodeId(String qrcodeId) {
        this.qrcodeId = qrcodeId;
    }
    public Integer getAttendeeLimit() {
        return attendeeLimit;
    }

    public void setAttendeeLimit(Integer attendeeLimit) {
        this.attendeeLimit = attendeeLimit;
    }
}
