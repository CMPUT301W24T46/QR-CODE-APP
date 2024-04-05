package com.example.eventapp.servicetest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.eventapp.firestoreservice.EventCallback;
import com.example.eventapp.firestoreservice.EventService;
import com.example.eventapp.firestoreservice.UserNameCallback;
import com.example.eventapp.firestoreservice.UserProfileService;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class EventServiceTest{
    @Mock
    private CollectionReference eventRef;
    @Mock
    private DocumentReference documentReference;
    @Mock
    private Task<DocumentSnapshot> task;
    @Mock
    private DocumentSnapshot documentSnapshot;

    private EventService eventService;
    private final String userId = "mockUserId";

    private final String eventName = "mockUserId";

    private final String eventId = "mockUserId";

    private final String eventDescription = "mockUserId";

    private final String imageUrl = "https://snisdnsdnfsdsd";

    private final String date = "12/10/2002" ;
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        eventService = new EventService(eventRef, date , eventName,eventDescription, eventId ,imageUrl);

        when(eventRef.document(eventId)).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(task);
        when(task.isSuccessful()).thenReturn(true);
        when(task.getResult()).thenReturn(documentSnapshot);
    }

    @Test
    public void testGetUserInfo() {
        // Setup the mock to return a specific name
        when(documentSnapshot.exists()).thenReturn(true);
        when(documentSnapshot.getString("name")).thenReturn("John Doe");

        eventService.getEventInformation(new EventCallback() {
            @Override
            public void eventCreated() {

            }

            @Override
            public void eventRetrieved(String eventName) {

            }

            @Override
            public void onError(Exception e) {
                throw new RuntimeException("Failed to get user name", e);
            }
        });

        // Verify that documentReference.get() was called.
        verify(documentReference).get();
    }

    @Test
    public void updateUserInfo_Success() {
        // Mock Task<Void> for the update operation to simulate success
        Task<Void> updateTaskSuccess = Mockito.mock(Task.class);
        when(documentReference.update(Mockito.anyMap())).thenReturn(updateTaskSuccess);
        when(updateTaskSuccess.isSuccessful()).thenReturn(true);

        // Mock callback
        UserNameCallback mockCallback = Mockito.mock(UserNameCallback.class);

    }

}
