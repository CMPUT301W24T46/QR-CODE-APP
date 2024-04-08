package com.example.eventapp.organizer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * ViewModel class to hold and manage event ID data in a lifecycle-conscious way. This class allows data to survive
 * configuration changes such as screen rotations.
 */

public class EventView extends ViewModel {
    private final MutableLiveData<String> eventId = new MutableLiveData<>();

    /**
     * Sets the event ID in the MutableLiveData. This method allows the event ID to be updated.
     *
     * @param eventId The new event ID to be set.
     */

    public void setEventId(String eventId) {
        this.eventId.setValue(eventId);
    }

    /**
     * Returns the LiveData object containing the event ID. This allows the UI to observe changes to the event ID.
     *
     * @return A LiveData object containing the current event ID.
     */

    public LiveData<String> getEventId() {
        return eventId;
    }
}

