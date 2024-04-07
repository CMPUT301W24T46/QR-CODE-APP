package com.example.eventapp.servicetest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.eventapp.firestoreservice.NotificationCallback;
import com.example.eventapp.firestoreservice.NotificationService;
import com.example.eventapp.firestoreservice.UserNameCallback;
import com.example.eventapp.firestoreservice.UserProfileService;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class NotificationServiceTest {
    @Mock
    private CollectionReference notificationRef;
    @Mock
    private DocumentReference documentReference;
    @Mock
    private Task<DocumentSnapshot> task;
    @Mock
    private DocumentSnapshot documentSnapshot;

    private NotificationService notificationService;
    private final String notificationId = "mockUserId";

    private final String notificationTitle = "mockUserId";

    private final String message = "mockUserId";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        notificationService = new NotificationService(notificationRef , notificationId , notificationTitle , message) ;

        when(notificationRef.document(notificationId)).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(task);
        when(task.isSuccessful()).thenReturn(true);
        when(task.getResult()).thenReturn(documentSnapshot);
    }

    @Test
    public void testGetNotificationInfo() {
        // Setup the mock to return a specific name
        when(documentSnapshot.exists()).thenReturn(true);
        when(documentSnapshot.getString("notificationId")).thenReturn("mockUserId");

        notificationService.getUserInfo(new NotificationCallback() {
            @Override
            public void onCallback(String notificationId) {
                // Perform assertion
                assertEquals("mockUserId", notificationId);
            }

            @Override
            public void onError(Exception e) {
                throw new RuntimeException("Failed to get user name", e);
            }

            @Override
            public void onSuccessfulUpdate(boolean success){
                assertEquals(true , success);
            }
        });

        // Verify that documentReference.get() was called.
        verify(documentReference).get();
    }

    @Test
    public void testNotificationUpdate() {
        // Mock Task<Void> for the update operation to simulate success
        Task<Void> updateTaskSuccess = Mockito.mock(Task.class);
        when(documentReference.update(Mockito.anyMap())).thenReturn(updateTaskSuccess);
        when(updateTaskSuccess.isSuccessful()).thenReturn(true);

        // Mock callback
        NotificationCallback mockCallback = Mockito.mock(NotificationCallback.class);

        // Execute the operation
        notificationService.updateUserInfo(mockCallback);

        // Verify that the update methof was called
        verify(documentReference).update(Mockito.argThat(argument ->
                argument.containsKey("notificationId") &&
                        argument.get("notificationId").equals(notificationId) &&
                        argument.containsKey("notificationTitle") &&
                        argument.get("notificationTitle").equals(notificationTitle) &&
                        argument.containsKey("message") &&
                        argument.get("message").equals(message)
        ));

    }

}
