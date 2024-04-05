package com.example.eventapp.servicetest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

public class UserProfileServiceTest {
    @Mock
    private CollectionReference userRef;
    @Mock
    private DocumentReference documentReference;
    @Mock
    private Task<DocumentSnapshot> task;
    @Mock
    private DocumentSnapshot documentSnapshot;

    private UserProfileService userProfileService;
    private final String userId = "mockUserId";

    private final String name = "mockUserId";

    private final String contactInformation = "mockUserId";

    private final String description = "mockUserId";
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        userProfileService = new UserProfileService(userRef, name ,userId , contactInformation , description);

        when(userRef.document(userId)).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(task);
        when(task.isSuccessful()).thenReturn(true);
        when(task.getResult()).thenReturn(documentSnapshot);
    }

    @Test
    public void testGetUserInfo() {
        // Setup the mock to return a specific name
        when(documentSnapshot.exists()).thenReturn(true);
        when(documentSnapshot.getString("name")).thenReturn("John Doe");

        userProfileService.getUserInfo(new UserNameCallback() {
            @Override
            public void onCallback(String userName) {
                // Perform assertion
                assertEquals("John Doe", userName);
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
    public void updateUserInfo_Success() {
        // Mock Task<Void> for the update operation to simulate success
        Task<Void> updateTaskSuccess = Mockito.mock(Task.class);
        when(documentReference.update(Mockito.anyMap())).thenReturn(updateTaskSuccess);
        when(updateTaskSuccess.isSuccessful()).thenReturn(true);

        // Mock callback
        UserNameCallback mockCallback = Mockito.mock(UserNameCallback.class);

        // Execute the operation
        userProfileService.updateUserInfo(mockCallback);

        // Verify that the update methof was called
        verify(documentReference).update(Mockito.argThat(argument ->
                argument.containsKey("name") &&
                        argument.get("name").equals(name) &&
                        argument.containsKey("homepage") &&
                        argument.get("homepage").equals(description) &&
                        argument.containsKey("contactInformation") &&
                        argument.get("contactInformation").equals(contactInformation)
        ));

    }

}
