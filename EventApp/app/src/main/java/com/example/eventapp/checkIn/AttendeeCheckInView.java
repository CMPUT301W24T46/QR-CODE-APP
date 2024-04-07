package com.example.eventapp.checkIn;

import com.google.firebase.Timestamp;

/**
 * AttendeeCheckInView represents a model for storing and retrieving attendee check-in information.
 * It includes the attendee's name, profile image URL, the frequency of check-ins, and the timestamp of the latest check-in.
 *
 * <p>This class is used in conjunction with {@link AttendeeCheckInAdapter} to display a list of attendees and their check-in
 * details in the user interface. It can also serve various other purposes where attendee check-in data is required.</p>
 *
 * <p>Key Attributes:</p>
 * <ul>
 *     <li>Attendee Name: The name of the attendee.</li>
 *     <li>Profile Image URL: A URL pointing to the attendee's profile image.</li>
 *     <li>Check-In Frequency: The number of times the attendee has checked in.</li>
 *     <li>Latest Check-In: The timestamp of the attendee's latest check-in.</li>
 * </ul>
 */

public class AttendeeCheckInView {
    private String attendeeName;
    private String profileImageUrl;
    private int checkInFrequency;
    private Timestamp latestCheckIn;

    /**
     * Default constructor for Firebase and general instantiation.
     */

    // Empty constructor for Firebase
    public AttendeeCheckInView() {
    }

    /**
     * Constructs an AttendeeCheckInView with the specified details.
     *
     * @param attendeeName     The name of the attendee.
     * @param profileImageUrl  The URL to the attendee's profile image.
     * @param checkInFrequency The number of times the attendee has checked in.
     * @param latestCheckIn    The timestamp of the attendee's latest check-in.
     */

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