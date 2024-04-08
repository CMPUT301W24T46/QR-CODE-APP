package com.example.eventapp.organizer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EventView extends ViewModel {
    private final MutableLiveData<String> eventId = new MutableLiveData<>();

    public void setEventId(String eventId) {
        this.eventId.setValue(eventId);
    }

    public LiveData<String> getEventId() {
        return eventId;
    }
}

