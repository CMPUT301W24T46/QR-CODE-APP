package com.example.eventapp.checkIn;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

/**
 * CheckIn represents a model for storing and managing data related to an attendee's check-in at an event.
 * It includes the attendee's unique identifier, the geographic location of the check-in, and the timestamp
 * when the check-in occurred.
 *
 * <p>This class is useful for tracking attendees' check-in details, such as when and where they checked in
 * to an event. It can be used in conjunction with services like Firebase Firestore to store and retrieve
 * check-in data in a backend database.</p>
 *
 * <p>Key Attributes:</p>
 * <ul>
 *     <li>Attendee ID: A unique identifier for the attendee, typically linked to their user account.</li>
 *     <li>Check-In Location: The geographic coordinates where the check-in took place.</li>
 *     <li>Check-In Time: The exact timestamp when the check-in was recorded.</li>
 * </ul>
 */

public class CheckIn {
    private String attendeeId;
    private GeoPoint checkInLocation;
    private Timestamp checkInTime;


    public CheckIn() {

    }

    /**
     * Constructs a CheckIn object with specified attendee ID, check-in location, and check-in time.
     *
     * @param attendeeId      The unique identifier for the attendee.
     * @param checkInLocation The geographic coordinates where the check-in took place.
     * @param checkInTime     The timestamp when the check-in was recorded.
     */

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
