package com.example.eventapp.admin;

import android.content.Context;
import android.util.Log;
import android.widget.Button;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link androidx.appcompat.app.AppCompatActivity}
 * A controller class is responsible for handling various administrative functions related to users, events, and images.
 * Utilizes Firebase Firestore for database operations.
 */
public class AdminController {
    private final Context context;
    private final Map<String, DocumentReference> userImageRefMap;
    private final CollectionReference userRef, eventRef, imageRef;
    public static CountingIdlingResource idlingResource = new CountingIdlingResource("Admin_Controller_Ops");

    public IdlingResource getIdlingResource() {
        return idlingResource;
    }

    /**
     * Constructor for AdminController.
     * Initializes user, event, and imagereferences to Firestore collections
     * and a map for user images.
     * @param context The context where the AdminController is used
     */
    public AdminController(Context context) {
        this.context = context;
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        this.userRef = db.collection("Users");
        this.eventRef = db.collection("Events");
        this.imageRef = db.collection("Image");

        this.userImageRefMap = new HashMap<>();


    }

    /* --- BROWSE/DELETE EVENTS --- */

    /**
     * Subscribes to real-time updates of the user database in Firestore.
     * Updates the provided UserAdapter with the latest data.
     * @param adapter The adapter that needs to be updated with the fetched data.
     */
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

    /**
     * Fetches and filters the list of users based on the provided search query.
     * @param searchText The query to filter the users.
     * @param queryOrDisplay Indicates whether to perform a search (true) or just display (false).
     * @param userAdapter The adapter to be updated with filtered results.
     */
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
                if (queryOrDisplay) {
                    // Safe null checks before calling toLowerCase()
                    if (user.getName() != null && searchText != null &&
                            user.getName().toLowerCase().contains(searchText.toLowerCase())) {
                        searchResults.add(user);
                    }
                } else {
                    // Add all users when not searching
                    searchResults.add(user);
                }

            }

            userAdapter.setFilter(searchResults);
            loadProfileImages(userAdapter);
            userAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> Log.e("TAG", "Error getting documents: " + e));
    }

    /**
     * Loads profile images for users from Firestore.
     * @param userAdapter Adapter containing users for which images are to be loaded.
     */
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

    /**
     * Deletes a user from the Firestore database based on the provided user ID.
     * @param userId The ID of the user to be deleted.
     * @return A Task representing the result of the delete operation.
     */
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

    /**
     * Subscribes to real-time updates from the Events collection in Firestore.
     * Updates provided EventAdapter with current data.
     * @param adapter Adapter to be updated with event data.
     */
    public void subscribeToEventDB(EventAdapter adapter) {
        eventRef.orderBy("eventDate").addSnapshotListener((querySnapshots, error) -> {
            if (error != null) {
                Log.e("Firestore", error.toString());
                return;
            }
            if (querySnapshots != null) {
                ArrayList<Event> events = new ArrayList<>();
                for (QueryDocumentSnapshot doc : querySnapshots) {
                    String eventName = doc.getString("eventName");
                    String eventDate = doc.getString("eventDate");
                    String eventDescription = doc.getString("eventDescription");
                    String imageURL = doc.getString("imageURL");

                    events.add(new Event(eventName, eventDate, imageURL, eventDescription));
                }
                adapter.setFilter(events);
                adapter.notifyDataSetChanged();
            }
        });
    }


    /**
     * Fetches and updates the event list based on provided search criteria.
     * @param searchText Text to filter events.
     * @param queryOrDisplay Indicates whether to perform a search (true) or just display (false).
     * @param adapter Adapter to be updated with filtered results.
     */
    public void getCurrentEventList(String searchText, boolean queryOrDisplay, EventAdapter adapter) {
        eventRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            ArrayList<Event> searchResults = new ArrayList<>();
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                String eventName = documentSnapshot.getString("eventName");
                String eventDate = documentSnapshot.getString("eventDate");
                String eventDescription = documentSnapshot.getString("eventDescription");
                String imageURL = documentSnapshot.getString("imageURL");

                Event event = new Event(eventName, eventDate, imageURL, eventDescription);

                if (!queryOrDisplay || (eventName != null && eventName.toLowerCase().contains(searchText.toLowerCase()))) {
                    searchResults.add(event);
                }
            }
            adapter.setFilter(searchResults);
            adapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> Log.e("TAG", "Error getting documents: " + e));
    }



    /**
     * Deletes an event from Firestore based on the event name.
     * @param eventName Name of the event to be deleted.
     * @return Task representing the result of the delete operation.
     */
    public Task<Void> deleteEvent(String eventName) {
        idlingResource.increment(); // Increment IdlingResource
        TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();

        if (eventName != null && !eventName.isEmpty()) {
            // Query to find the event with the matching name
            eventRef.whereEqualTo("eventName", eventName)
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

    /**
     * Subscribes to real-time updates from the Images collection in Firestore.
     * Updates provided ImageGridAdapter with current data.
     * @param adapter Adapter to be updated with image data.
     */
    public void subscribeToImageDB(ImageGridAdapter adapter, String selectedFilter) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        String path;

        if ("Event Images".equals(selectedFilter)) {
            path = "event_images/";
        } else if ("Profile Images".equals(selectedFilter)) {
            path = "profileImages/";
        } else if ("QR Codes".equals(selectedFilter)) {
            path = "qr_codes/";
        } else {
            // Handle default case or throw an error
            path = "event_images/";
        }

        StorageReference listRef = storage.getReference().child(path);

        listRef.listAll()
                .addOnSuccessListener(listResult -> {
                    List<Image> images = new ArrayList<>();
                    for (StorageReference item : listResult.getItems()) {

                        item.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageURL = uri.toString();
                            String imageId = item.getName(); // id is the file name
                            images.add(new Image(imageURL, imageId));
                            adapter.setFilter(images);
                            adapter.notifyDataSetChanged();
                        }).addOnFailureListener(e -> Log.e("Storage", "Error fetching URL for item " + item.getPath(), e));
                    }
                })
                .addOnFailureListener(e -> Log.e("Storage", "Error listing images", e));

    }


    /**
     * Deletes an image from Firestore based on its ID.
     * @param imageId ID of the image to be deleted.
     * @return Task representing the result of the delete operation.
     */
    public Task<Void> deleteImage(String imageId, String selectedFilter) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        String path;

        if ("Event Images".equals(selectedFilter)) {
            path = "event_images/";
        } else if ("Profile Images".equals(selectedFilter)) {
            path = "profileImages/";
        } else if ("QR Codes".equals(selectedFilter)) {
            path = "qr_codes/";
        } else {
            // Handle default case or throw an error
            path = "event_images/";
        }
        StorageReference imageRef = storage.getReference().child(path + imageId);

        return imageRef.delete().addOnSuccessListener(aVoid -> {
            Toast.makeText(context, "Image deleted successfully", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(context, "Error deleting image", Toast.LENGTH_SHORT).show();
        });
    }



    /**
     * Fetches and updates the image list based on provided search criteria.
     * @param searchText Text to filter images.
     * @param queryOrDisplay Indicates whether to perform a search (true) or just display (false).
     * @param adapter Adapter to be updated with filtered results.
     */
    public void getCurrentImageList(String searchText, boolean queryOrDisplay, ImageGridAdapter adapter, String selectedFilter) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        String path;

        if ("Event Images".equals(selectedFilter)) {
            path = "event_images/";
        } else if ("Profile Images".equals(selectedFilter)) {
            path = "profileImages/";
        } else if ("QR Codes".equals(selectedFilter)) {
            path = "qr_codes/";
        } else {
            path = "event_images/"; // Default path
        }

        StorageReference listRef = storage.getReference().child(path);


        listRef.listAll()
                .addOnSuccessListener(listResult -> {
                    List<Image> images = new ArrayList<>();
                    for (StorageReference item : listResult.getItems()) {
                        item.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageURL = uri.toString();
                            String imageId = item.getName(); // Using file name as ID

                            if (!queryOrDisplay || imageId.toLowerCase().contains(searchText.toLowerCase())) {
                                images.add(new Image(imageURL, imageId));
                            }

                        }).addOnFailureListener(e -> Log.e("Storage", "Error fetching URL for item " + item.getPath(), e))
                        .addOnCompleteListener(task -> {
                            // Updates adapter
                            if (listResult.getItems().indexOf(item) == listResult.getItems().size() - 1) {
                                adapter.setFilter(images);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                })
                .addOnFailureListener(e -> Log.e("Storage", "Error listing images", e));
    }





    /* --- HELPER FUNCTIONS FOR INTENT TESTING --- */

    /**
     * Adds mock data for testing purposes.
     * Creates mock users, events, and images with a randomly generated identifier.
     * @param randomId Identifier to be used for mock data entries.
     */
    public void addMockData(String randomId) {
        addMockUser(randomId);
        addMockEvent(randomId);
        addMockImage(randomId);
    }

    /**
     * Deletes mock data from Firestore collections based on the provided identifier.
     * @param randomId Identifier used for the mock data entries.
     */
    public void deleteMockdata(String randomId) {
        deleteUser(randomId);
        deleteEvent(randomId);
//        deleteImage(randomId);
    }

    /**
     * Adds a mock user to the Firestore 'Users' collection for testing purposes.
     * @param randomId Identifier for the mock user.
     */
    private void addMockUser(String randomId) {
        // Adding a mock user with a specific ID
        Map<String, Object> mockUser = new HashMap<>();
        mockUser.put("name",  randomId);
        mockUser.put("contactInformation", "john.doe@example.com");
        mockUser.put("homepage", "www.JohnDoe.com");
        mockUser.put("ImageUrl", "");
        mockUser.put("typeOfUser", "Attendee");
        userRef.document(randomId).set(mockUser); // Set the custom ID
    }

    /**
     * Adds a mock event to the Firestore 'Events' collection for testing purposes.
     * @param randomId Identifier for the mock event.
     */
    private void addMockEvent(String randomId) {
        // Adding a mock event with a specific ID
        Map<String, Object> mockEvent = new HashMap<>();
        mockEvent.put("Name", randomId);
        mockEvent.put("Description", "Mock Event Intended for Testing");
        mockEvent.put("URL", "https://example.com/image.jpg");
        eventRef.document(randomId).set(mockEvent); // Set the custom ID
    }

    /**
     * Adds a mock image to the Firestore 'Images' collection for testing purposes.
     * @param randomId Identifier for the mock image.
     */
    private void addMockImage(String randomId) {
        // Adding a mock image with a specific ID
        Map<String, Object> mockImage = new HashMap<>();
        mockImage.put("URL", "https://firebasestorage.googleapis.com/v0/b/qr-code-app-6fe73.appspot.com/o/DALL%C2%B7E%202024-03-06%2015.05.19%20-%20A%20dynamic%20image%20of%20a%20futuristic%20city%20at%20night%2C%20with%20neon%20lights%20and%20towering%20skyscrapers.%20Flying%20cars%20zoom%20through%20the%20air%2C%20leaving%20trails%20of%20light%20be.webp?alt=media&token=d93cd2e0-16fc-4f54-9e77-a0f3c630eecf");
        imageRef.document(randomId).set(mockImage); // Set the custom ID
    }

    // SEARCH METHODS FOR TESTING IF A DOCUMENT EXISTS

    /**
     * Searches for a profile in the Firestore 'Users' collection based on the given user ID.
     * @param userId ID of the user to be searched.
     * @return Task<Boolean> indicating whether the profile was found (true) or not (false).
     */

    public Task<Boolean> searchForProfile(String userId) {
        return userRef.document(userId).get().continueWith(task -> {
            DocumentSnapshot document = task.getResult();
            // Return true if the document exists, false otherwise
            return document.exists();
        });
    }

    /**
     * Searches for an event in the Firestore 'Events' collection based on the given event ID.
     * @param eventId ID of the event to be searched.
     * @return Task<Boolean> indicating whether the event was found (true) or not (false).
     */
    public Task<Boolean> searchForEvent(String eventId) {
        return eventRef.document(eventId).get().continueWith(task -> {
            DocumentSnapshot document = task.getResult();
            // Return true if the document exists, false otherwise
            return document.exists();
        });
    }

    /**
     * Searches for an image in the Firestore 'Images' collection based on the given image ID.
     * @param imageId ID of the image to be searched.
     * @return Task<Boolean> indicating whether the image was found (true) or not (false).
     */
    public Task<Boolean> searchForImage(String imageId) {
        return imageRef.document(imageId).get().continueWith(task -> {
            DocumentSnapshot document = task.getResult();
            // Return true if the document exists, false otherwise
            return document.exists();
        });
    }

}



