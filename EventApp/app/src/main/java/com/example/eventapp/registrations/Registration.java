package com.example.eventapp.registrations;

import com.google.firebase.Timestamp;

public class Registration {
    private String attendeeId;
    private Timestamp registrationDate;
    private String attendeeImageURL;

    public Registration() {

    }

    public String getAttendeeImageURL() {
        return attendeeImageURL;
    }

    public void setAttendeeImageURL(String attendeeImageURL) {
        this.attendeeImageURL = attendeeImageURL;
    }

    public Registration(String attendeeId, Timestamp registrationDate, String attendeeImageURL) {
        this.attendeeId = attendeeId;
        this.registrationDate = registrationDate;
        this.attendeeImageURL = attendeeImageURL;
    }

    public String getAttendeeId() {
        return attendeeId;
    }

    public void setAttendeeId(String attendeeId) {
        this.attendeeId = attendeeId;
    }

    public Timestamp getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Timestamp registrationDate) {
        this.registrationDate = registrationDate;
    }
}
