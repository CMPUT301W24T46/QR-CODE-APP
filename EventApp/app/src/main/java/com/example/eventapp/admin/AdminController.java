package com.example.eventapp.admin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.idling.CountingIdlingResource;

import com.example.eventapp.Image.Image;
import com.example.eventapp.Image.ImageGridAdapter;
import com.example.eventapp.event.Event;
import com.example.eventapp.event.EventAdapter;
import com.example.eventapp.users.User;
import com.example.eventapp.users.UserAdapter;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;

public class AdminController {
    private final Context context;
    private final Map<String, DocumentReference> userImageRefMap;
    private final CollectionReference userRef, eventRef, imageRef;
    public static CountingIdlingResource idlingResource = new CountingIdlingResource("Admin_Controller_Ops");

    public IdlingResource getIdlingResource() {
        return idlingResource;
    }


    public AdminController(Context context) {
        this.context = context;
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        this.userRef = db.collection("Users");
        this.eventRef = db.collection("Events");
        this.imageRef = db.collection("Image");

        this.userImageRefMap = new HashMap<>();


    }

    /* --- BROWSE/DELETE EVENTS --- */
    public void subscribeToUserDB(UserAdapter adapter) {
        userRef.addSnapshotListener((querySnapshots, error) -> {
            if (error != null) {
                Log.e("Firestore", error.toString());
                return;
            }
            if (querySnapshots != null) {
                ArrayList<User> users = new ArrayList<>();
                userImageRefMap.clear();
                for (QueryDocumentSnapshot doc : querySnapshots) {
                    String userID = doc.getId();
                    String name = doc.getString("name");
                    String contactInfo = doc.getString("contactInformation");
                    String homepage = doc.getString("homepage");
                    String typeOfUser = doc.getString("typeOfUser");

                    // Check if 'imageUrl' field is present and of type DocumentReference
                    Object imageUrlObject = doc.get("imageUrl");
                    if (imageUrlObject instanceof DocumentReference) {
                        DocumentReference imageRef = (DocumentReference) imageUrlObject;
                        userImageRefMap.put(userID, imageRef);
                    } else if (imageUrlObject != null) {
                        // Handle cases where 'imageUrl' field is present but not a DocumentReference
                        Log.e("Firestore", "'imageUrl' field is not a DocumentReference for user: " + userID);
                        // Handle this case as necessary
                    }

                    User user = new User(userID, name, contactInfo, homepage, "", typeOfUser);
                    users.add(user);

                }

                adapter.clear();
                adapter.setFilter(users);
                loadProfileImages(adapter);
                adapter.notifyDataSetChanged();
            }
        });
    }


    public void getCurrentUserList(String searchText, boolean queryOrDisplay, UserAdapter userAdapter){
        userRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            ArrayList<User> searchResults = new ArrayList<>();

            // Iterate over all documents
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                String userID = doc.getId();
                String name = doc.getString("name");
                String contactInfo = doc.getString("contactInformation");
                String homepage = doc.getString("homepage");
                String typeOfUser = doc.getString("typeOfUser");


                User user = new User(userID, name, contactInfo, homepage, "", typeOfUser);

                // Check if filter is needed
                if (queryOrDisplay && user.getName().toLowerCase().contains(searchText.toLowerCase())) {
                    searchResults.add(user);
                } else if (!queryOrDisplay) {
                    searchResults.add(user); // Add all users when not searching
                }
            }

            userAdapter.setFilter(searchResults);
            loadProfileImages(userAdapter);
            userAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> Log.e("TAG", "Error getting documents: " + e));
    }

    public void loadProfileImages(UserAdapter userAdapter) {
        if (userImageRefMap.isEmpty()) {
            return;
        }
        List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
        for (Map.Entry<String, DocumentReference> entry : userImageRefMap.entrySet()) {
            String userId = entry.getKey();
            DocumentReference imageRef = entry.getValue();
            Task<DocumentSnapshot> imageTask = imageRef.get().continueWith(task -> {
                DocumentSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    String imageUrl = snapshot.getString("URL");
                    for (User user : userAdapter.getItems()) {
                        if (user.getId().equals(userId)) {
                            user.setImageURL(imageUrl);
                            break;
                        }
                    }
                }
                return null;
            });
            tasks.add(imageTask);
        }
        Task<Void> allTasks = Tasks.whenAll(tasks);
        allTasks.addOnSuccessListener(voids -> userAdapter.notifyDataSetChanged())
                .addOnFailureListener(e -> Log.e("TAG", "Error loading images", e));

    }


    public Task<Void> deleteUser(String userId) {
        idlingResource.increment();
        TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();

        if (userId != null && !userId.isEmpty()) {
            userRef.document(userId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, "User deleted successfully", Toast.LENGTH_SHORT).show();
                        tcs.setResult(null);
                        idlingResource.decrement();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Error deleting user", Toast.LENGTH_SHORT).show();
                        tcs.setException(e);
                        idlingResource.decrement();
                    });
        } else {
            tcs.setException(new IllegalArgumentException("User ID not found."));
        }

        return tcs.getTask();
    }


    /* --- BROWSE/DELETE EVENTS --- */
    public void subscribeToEventDB(EventAdapter adapter) {
        eventRef.addSnapshotListener((querySnapshots, error) -> {
            if (error != null) {
                Log.e("Firestore", error.toString());
                return;
            }
            if (querySnapshots != null) {
                ArrayList<Event> events = new ArrayList<>();
                for (QueryDocumentSnapshot doc : querySnapshots) {
                    String eventId = doc.getId();
                    String eventName = doc.getString("Name");
                    String imageURL = doc.getString("URL");
                    Log.d("Firestore", String.format("Name(%s, %s) fetched", eventId, eventName));
                    events.add(new Event(eventName, imageURL));
                }
                adapter.setFilter(events);
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void getCurrentEventList(String searchText, boolean queryOrDisplay, EventAdapter adapter) {
        eventRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            ArrayList<Event> searchResults = new ArrayList<>();
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                // Check if the document has the fields "Name" and "URL"
                String eventName = documentSnapshot.contains("Name") ? documentSnapshot.getString("Name") : "";
                String URL = documentSnapshot.contains("URL") ? documentSnapshot.getString("URL") : "";

                if (!queryOrDisplay) {
                    searchResults.add(new Event(eventName, URL));
                    continue;
                }

                if (eventName != null && eventName.toLowerCase().contains(searchText.toLowerCase())) {
                    searchResults.add(new Event(eventName, URL));
                }
            }
            adapter.setFilter(searchResults);
            adapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> Log.e("TAG", "Error getting documents: " + e));
    }
    public Task<Void> deleteEvent(String eventName) {
        idlingResource.increment(); // Increment IdlingResource
        TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();

        if (eventName != null && !eventName.isEmpty()) {
            // Query to find the event with the matching name
            eventRef.whereEqualTo("Name", eventName)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            String documentId = queryDocumentSnapshots.getDocuments().get(0).getId();
                            eventRef.document(documentId)
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(context, "Event deleted successfully", Toast.LENGTH_SHORT).show();
                                        tcs.setResult(null); // Set the result on success
                                        idlingResource.decrement(); // Decrement IdlingResource
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(context, "Error deleting event", Toast.LENGTH_SHORT).show();
                                        tcs.setException(e); // Set exception on failure
                                        idlingResource.decrement(); // Decrement IdlingResource
                                    });
                        } else {
                            Toast.makeText(context, "No such event found", Toast.LENGTH_SHORT).show();
                            tcs.setException(new IllegalArgumentException("Event not found")); // Set exception if event not found
                            idlingResource.decrement(); // Decrement IdlingResource
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Error finding event", Toast.LENGTH_SHORT).show();
                        tcs.setException(e); // Set exception on failure to find event
                        idlingResource.decrement(); // Decrement IdlingResource
                    });
        } else {
            tcs.setException(new IllegalArgumentException("Event name not found.")); // Set exception if event name is empty
            idlingResource.decrement(); // Decrement IdlingResource
        }

        return tcs.getTask(); // Return the Task
    }



    /* --- BROWSE/DELETE Images --- */
    public void subscribeToImageDB(ImageGridAdapter adapter) {
        imageRef.addSnapshotListener((querySnapshots, error) -> {
            if (error != null) {
                Log.e("Firestore", "Error listening to Image snapshots", error);
                return;
            }
            if (querySnapshots != null) {
                List<Image> images = new ArrayList<>();
                for (QueryDocumentSnapshot doc : querySnapshots) {
                    String imageId = doc.getId();
                    String imageURL = doc.getString("URL");
                    Log.d("Firestore", String.format("Name(%s, %s) fetched", imageId, imageURL));
                    images.add(new Image(imageURL, imageId));
                }
                adapter.setFilter(images);
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void getCurrentImageList(String searchText, boolean queryOrDisplay, ImageGridAdapter adapter) {
        imageRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            ArrayList<Image> searchResults = new ArrayList<>();
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                // Check if the document has the fields "URL"
                String imageURL = documentSnapshot.contains("URL") ? documentSnapshot.getString("URL") : "";
                String imageID =  documentSnapshot.getId();

                if (!queryOrDisplay) {
                    searchResults.add(new Image(imageURL, imageID));
                    continue;
                }

                if (imageID.toLowerCase().contains(searchText.toLowerCase())) {
                    searchResults.add(new Image(imageURL, imageID));
                }
            }
            adapter.setFilter(searchResults);
            adapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> Log.e("TAG", "Error getting documents: " + e));
    }
    public Task<Void> deleteImage(String imageId) {
        idlingResource.increment(); // Increment IdlingResource for Espresso synchronization
        TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();

        if (imageId != null && !imageId.isEmpty()) {
            // Delete the image document based on ID
            imageRef.document(imageId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, "Image deleted successfully", Toast.LENGTH_SHORT).show();
                        tcs.setResult(null); // Set the result on success
                        idlingResource.decrement(); // Decrement IdlingResource
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Error deleting image", Toast.LENGTH_SHORT).show();
                        tcs.setException(e); // Set exception on failure
                        idlingResource.decrement(); // Decrement IdlingResource
                    });
        } else {
            Toast.makeText(context, "Error: Image ID not found.", Toast.LENGTH_SHORT).show();
            tcs.setException(new IllegalArgumentException("Image ID not provided.")); // Set exception if image ID is empty
            idlingResource.decrement(); // Decrement IdlingResource
        }

        return tcs.getTask(); // Return the Task
    }



    /* --- HELPER FUNCTIONS FOR INTENT TESTING --- */
    public void addMockData(String randomId) {
        addMockUsers(randomId);
        addMockEvents(randomId);
        addMockImages(randomId);
    }

    public void deleteMockdata(String randomId) {
        deleteUser(randomId);
        deleteEvent(randomId);
        deleteImage(randomId);
    }

    private void addMockUsers(String randomId) {
        // Adding a mock user with a specific ID
        Map<String, Object> mockUser = new HashMap<>();
        mockUser.put("name",  randomId);
        mockUser.put("contactInformation", "john.doe@example.com");
        mockUser.put("homepage", "www.JohnDoe.com");
        mockUser.put("ImageUrl", "");
        mockUser.put("typeOfUser", "Attendee");
        userRef.document(randomId).set(mockUser); // Set the custom ID
    }

    private void addMockEvents(String randomId) {
        // Adding a mock event with a specific ID
        Map<String, Object> mockEvent = new HashMap<>();
        mockEvent.put("Name", randomId);
        mockEvent.put("Description", "Mock Event Intended for Testing");
        mockEvent.put("URL", "https://example.com/image.jpg");
        eventRef.document(randomId).set(mockEvent); // Set the custom ID
    }

    private void addMockImages(String randomId) {
        // Adding a mock image with a specific ID
        Map<String, Object> mockImage = new HashMap<>();
        mockImage.put("URL", "https://firebasestorage.googleapis.com/v0/b/qr-code-app-6fe73.appspot.com/o/DALL%C2%B7E%202024-03-06%2015.05.19%20-%20A%20dynamic%20image%20of%20a%20futuristic%20city%20at%20night%2C%20with%20neon%20lights%20and%20towering%20skyscrapers.%20Flying%20cars%20zoom%20through%20the%20air%2C%20leaving%20trails%20of%20light%20be.webp?alt=media&token=d93cd2e0-16fc-4f54-9e77-a0f3c630eecf");
        imageRef.document(randomId).set(mockImage); // Set the custom ID
    }

    // SEARCH METHODS
    public Task<Boolean> searchForProfile(String userId) {
        return userRef.document(userId).get().continueWith(task -> {
            DocumentSnapshot document = task.getResult();
            // Return true if the document exists, false otherwise
            return document.exists();
        });
    }

    public Task<Boolean> searchForEvent(String eventId) {
        return eventRef.document(eventId).get().continueWith(task -> {
            DocumentSnapshot document = task.getResult();
            // Return true if the document exists, false otherwise
            return document.exists();
        });
    }


    public Task<Boolean> searchForImage(String imageId) {
        return imageRef.document(imageId).get().continueWith(task -> {
            DocumentSnapshot document = task.getResult();
            // Return true if the document exists, false otherwise
            return document.exists();
        });
    }

}
