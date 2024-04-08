package com.example.eventapp.event;

/**
 * Event represents an event in the application, encapsulating details such as the event's name, date,
 * image URL, description, creator ID, attendee limit, and the number of sign-ups. This class provides
 * constructors for creating event instances with various combinations of these details, catering to different
 * use cases throughout the application.
 *
 * <p>This class is designed to be flexible, allowing for events to be created with minimal information
 * (e.g., just an event name) or more detailed information (e.g., event name, date, image, description, and attendee limits).</p>
 *
 * <p>Key Attributes:</p>
 * <ul>
 *     <li>Event Name: The name of the event.</li>
 *     <li>Event Date: The date on which the event is scheduled.</li>
 *     <li>Image URL: A URL pointing to an image associated with the event.</li>
 *     <li>Event Description: A detailed description of the event.</li>
 *     <li>Creator ID: The unique identifier of the user who created the event.</li>
 *     <li>Attendee Limit: The maximum number of attendees allowed for the event.</li>
 *     <li>Number of Sign-Ups: The current number of sign-ups for the event.</li>
 * </ul>
 *
 * <p>Additionally, the class includes getter and setter methods for each attribute, allowing for easy access
 * and modification of event details.</p>
 */

public class Event {
    private Long eventLim;
    String eventName ;
    String imageDescription ;
    String imageURL ;
    String eventDate;
    String creatorId;
    String eventDescription;
    private String eventId;
    private String qrcodeId;
    private Integer attendeeLimit;

    private Long numberOfSignUps ;

    public Long getNumberOfSignUps() {
        return numberOfSignUps;
    }

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

    public Event(String eventName , String eventDate , String imageURL, String eventId, String eventDescription, Long eventLim , Long number0fSignUps){
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.imageURL = imageURL;
        this.eventId = eventId;
        this.eventDescription = eventDescription;
        this.eventLim = eventLim ;
        this.numberOfSignUps = number0fSignUps ;
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

    public Long getEventLim() {
        return eventLim;
    }

    public void setAttendeeLimit(Integer attendeeLimit) {
        this.attendeeLimit = attendeeLimit;
    }
}
