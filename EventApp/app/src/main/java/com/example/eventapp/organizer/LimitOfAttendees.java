package com.example.eventapp.organizer;

import androidx.annotation.Nullable;

public class LimitOfAttendees {

    private String eventId;
    private Integer attendeeLimit;

    public LimitOfAttendees(String eventId) {
        this.eventId = eventId;
    }

    public void setAttendeeLimit(@Nullable Integer limit) {
        // If the limit is null or -1, we consider the attendee limit as disabled.
        this.attendeeLimit = (limit == null || limit == -1) ? null : limit;
        // Persist the change to the database.
        saveLimitToDatabase();
    }

    @Nullable
    public Integer getAttendeeLimit() {
        return attendeeLimit;
    }

    private void saveLimitToDatabase() {
        // Your logic to save the attendee limit to the database.
        // If attendeeLimit is null, it means there's no limit.
        // Update the database accordingly.
    }
}

