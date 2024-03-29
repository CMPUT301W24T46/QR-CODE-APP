package com.example.eventapp.checkIn;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

public class CheckIn {
    private String attendeeId;
    private GeoPoint checkInLocation;
    private Timestamp checkInTime;


    public CheckIn() {

    }

    public CheckIn(String attendeeId, GeoPoint checkInLocation, Timestamp checkInTime) {
        this.attendeeId = attendeeId;
        this.checkInLocation = checkInLocation;
        this.checkInTime = checkInTime;
    }

    // Getter methods
    public String getAttendeeId() {
        return attendeeId;
    }

    public GeoPoint getCheckInLocation() {
        return checkInLocation;
    }

    public Timestamp getCheckInTime() {
        return checkInTime;
    }

    // Setter methods
    public void setAttendeeId(String attendeeId) {
        this.attendeeId = attendeeId;
    }

    public void setCheckInLocation(GeoPoint checkInLocation) {
        this.checkInLocation = checkInLocation;
    }

    public void setCheckInTime(Timestamp checkInTime) {
        this.checkInTime = checkInTime;
    }
}
