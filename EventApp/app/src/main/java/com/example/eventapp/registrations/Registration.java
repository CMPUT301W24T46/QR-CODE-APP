package com.example.eventapp.registrations;

import com.google.firebase.Timestamp;

/**
 * Represents a registration entry for an attendee at an event. This class encapsulates information about the attendee's
 * registration, including their unique identifier, the date of registration, and a URL to an image associated with the attendee.
 */

public class Registration {
    private String attendeeId;
    private Timestamp registrationDate;
    private String attendeeImageURL;

    /**
     * Default constructor required for Firebase's automatic data mapping.
     */

    public Registration() {

    }

    /**
     * @return The URL to an image associated with the attendee.
     */

    public String getAttendeeImageURL() {
        return attendeeImageURL;
    }

    /**
     * Sets the URL to an image associated with the attendee.
     *
     * @param attendeeImageURL The URL to an image associated with the attendee.
     */

    public void setAttendeeImageURL(String attendeeImageURL) {
        this.attendeeImageURL = attendeeImageURL;
    }

    /**
     * Constructs a new Registration instance with specified attendee ID, registration date, and attendee image URL.
     *
     * @param attendeeId The unique identifier for the attendee.
     * @param registrationDate The date and time of registration.
     * @param attendeeImageURL The URL to an image associated with the attendee.
     */

    public Registration(String attendeeId, Timestamp registrationDate, String attendeeImageURL) {
        this.attendeeId = attendeeId;
        this.registrationDate = registrationDate;
        this.attendeeImageURL = attendeeImageURL;
    }

    /**
     * @return The unique identifier for the attendee.
     */

    public String getAttendeeId() {
        return attendeeId;
    }

    /**
     * Sets the unique identifier for the attendee.
     *
     * @param attendeeId The unique identifier for the attendee.
     */

    public void setAttendeeId(String attendeeId) {
        this.attendeeId = attendeeId;
    }

    /**
     * @return The timestamp representing the date and time of registration.
     */

    public Timestamp getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Timestamp registrationDate) {
        this.registrationDate = registrationDate;
    }
}
