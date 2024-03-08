package com.example.eventapp.admin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.eventapp.Image.Image;
import com.example.eventapp.Image.ImageGridAdapter;
import com.example.eventapp.event.Event;
import com.example.eventapp.event.EventAdapter;
import com.example.eventapp.users.User;
import com.example.eventapp.users.UserAdapter;
import com.google.android.gms.tasks.Task;
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

public class AdminController {
    private final Context context;
    private final Map<String, DocumentReference> userImageRefMap;
    private final CollectionReference userRef, eventRef, imageRef;

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

    public void deleteUser(String userId) {
        if (userId != null && !userId.isEmpty()) {
            new AlertDialog.Builder(this.context)
                    .setTitle("Delete User")
                    .setMessage("Are you sure you want to delete this user?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        // Continue with delete operation
                        userRef.document(userId)
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this.context, "User deleted successfully", Toast.LENGTH_SHORT).show();
                                    if (this.context instanceof Activity) {
                                        ((Activity) this.context).finish();
                                    }
                                })
                                .addOnFailureListener(e -> Toast.makeText(this.context , "Error deleting user", Toast.LENGTH_SHORT).show());
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            Toast.makeText(this.context, "Error: User ID not found.", Toast.LENGTH_SHORT).show();
        }
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

    public void deleteEvent(String eventName) {
        // TODO: Delete based on id instead of name
        if (eventName != null && !eventName.isEmpty()) {
            new AlertDialog.Builder(this.context)
                    .setTitle("Delete event")
                    .setMessage("Are you sure you want to delete this event?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        // Query to find the event with the matching name
                        eventRef.whereEqualTo("Name", eventName)
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    if (!queryDocumentSnapshots.isEmpty()) {
                                        String documentId = queryDocumentSnapshots.getDocuments().get(0).getId();
                                        eventRef.document(documentId)
                                                .delete()
                                                .addOnSuccessListener(aVoid -> {
                                                    Toast.makeText(this.context, "Event deleted successfully", Toast.LENGTH_SHORT).show();

                                                    if (this.context instanceof Activity) {
                                                        ((Activity) this.context).finish();
                                                    }
                                                })
                                                .addOnFailureListener(e -> Toast.makeText(this.context, "Error deleting event", Toast.LENGTH_SHORT).show());
                                    } else {
                                        Toast.makeText(this.context, "No such event found", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(e -> Toast.makeText(this.context, "Error finding event", Toast.LENGTH_SHORT).show());
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            Toast.makeText(this.context, "Error: Event name not found.", Toast.LENGTH_SHORT).show();
        }
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

    public void deleteImage(String imageURL) {
        if (imageURL != null && !imageURL.isEmpty()) {
            new androidx.appcompat.app.AlertDialog.Builder(this.context)
                    .setTitle("Delete Image")
                    .setMessage("Are you sure you want to delete this image?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        // Query for the image by URL and delete it
                        imageRef
                                .whereEqualTo("URL", imageURL)
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                        imageRef.document(document.getId())
                                                .delete()
                                                .addOnSuccessListener(aVoid -> {
                                                    Toast.makeText(this.context, "Image deleted successfully", Toast.LENGTH_SHORT).show();
                                                    if (this.context instanceof Activity) {
                                                        ((Activity) this.context).finish();
                                                    }
                                                })
                                                .addOnFailureListener(e -> Toast.makeText(this.context, "Error deleting image", Toast.LENGTH_SHORT).show());
                                    }
                                })
                                .addOnFailureListener(e -> Toast.makeText(this.context, "Error finding image", Toast.LENGTH_SHORT).show());
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            Toast.makeText(this.context, "Error: Image URL not found.", Toast.LENGTH_SHORT).show();
        }
    }

    /* --- HELPER FUNCTIONS FOR INTENT TESTING --- */
    public void addMockData(String randomId) {
        addMockUsers(randomId);
        addMockEvents(randomId);
        addMockImages(randomId);
    }

    private void addMockUsers(String randomId) {
        // Adding a mock user
        Map<String, Object> mockUser = new HashMap<>();
        mockUser.put("id", randomId);
        mockUser.put("name",  randomId);
        mockUser.put("contactInformation", "john.doe@example.com");
        mockUser.put("homepage", "www.JohnDoe.com");
        mockUser.put("ImageUrl", "");
        mockUser.put("typeOfUser", "Attendee");
        userRef.add(mockUser);

    }

    private void addMockEvents(String randomId) {
        // Adding a mock event
        Map<String, Object> mockEvent = new HashMap<>();
        mockEvent.put("Name", randomId);
        mockEvent.put("Description", "Mock Event Intended for Testing");
        mockEvent.put("URL", "https://firebasestorage.googleapis.com/v0/b/qr-code-app-6fe73.appspot.com/o/DALL%C2%B7E%202024-03-06%2015.03.30%20-%20A%20modern%2C%20sleek%2C%20technology-themed%20office%20space%20with%20large%20windows%2C%20offering%20a%20view%20of%20a%20futuristic%20cityscape.%20Inside%2C%20there's%20a%20variety%20of%20high-tech%20.webp?alt=media&token=eee291b2-44aa-47fe-918d-b07a2e2bc996");
        eventRef.add(mockEvent);

    }

    private void addMockImages(String randomId) {
        // Adding a mock image
        Map<String, Object> mockImage = new HashMap<>();
        mockImage.put("URL", "https://firebasestorage.googleapis.com/v0/b/qr-code-app-6fe73.appspot.com/o/DALL%C2%B7E%202024-03-06%2015.03.30%20-%20A%20modern%2C%20sleek%2C%20technology-themed%20office%20space%20with%20large%20windows%2C%20offering%20a%20view%20of%20a%20futuristic%20cityscape.%20Inside%2C%20there's%20a%20variety%20of%20high-tech%20.webp?alt=media&token=eee291b2-44aa-47fe-918d-b07a2e2bc996");
        imageRef.add(mockImage);
    }


}
