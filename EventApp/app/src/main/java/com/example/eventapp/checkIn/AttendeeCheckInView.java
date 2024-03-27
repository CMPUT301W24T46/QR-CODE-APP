package com.example.eventapp.checkIn;

import com.google.firebase.Timestamp;

public class AttendeeCheckInView {
    private String attendeeName;
    private String profileImageUrl;
    private int checkInFrequency;
    private Timestamp latestCheckIn;

    // Empty constructor for Firebase
    public AttendeeCheckInView() {
    }

    // Constructor
    public AttendeeCheckInView(String attendeeName, String profileImageUrl, int checkInFrequency, Timestamp latestCheckIn) {
        this.attendeeName = attendeeName;
        this.profileImageUrl = profileImageUrl;
        this.checkInFrequency = checkInFrequency;
        this.latestCheckIn = latestCheckIn;
    }


    public String getAttendeeName() {
        return attendeeName;
    }

    public void setAttendeeName(String attendeeName) {
        this.attendeeName = attendeeName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public int getCheckInFrequency() {
        return checkInFrequency;
    }

    public void setCheckInFrequency(int checkInFrequency) {
        this.checkInFrequency = checkInFrequency;
    }

    public Timestamp getLatestCheckIn() {
        return latestCheckIn;
    }

    public void setLatestCheckIn(Timestamp latestCheckIn) {
        this.latestCheckIn = latestCheckIn;
    }
}