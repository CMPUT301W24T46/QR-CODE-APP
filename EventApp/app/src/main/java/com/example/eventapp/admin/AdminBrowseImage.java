package com.example.eventapp.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eventapp.Image.Image;
import com.example.eventapp.Image.ImageGridAdapter;
import com.example.eventapp.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link androidx.appcompat.app.AppCompatActivity}
 * An activity that browses through images in the database
 */
public class AdminBrowseImage extends AppCompatActivity {
    private AdminController adminController;

    private GridView imageGridView;
    private List<Image> imageItems;
    private ImageGridAdapter imageGridAdapter;
    private SearchView searchView;
    private Spinner imageSpinner;
    private String uid;


    /**
     * Called when the activity is starting. Initializes the views
     *
     * @param savedInstanceState a previously saved state
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_browse_image);

        // TODO: Add search functionality (optional)

        adminController = new AdminController(this);


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int columnWidth = screenWidth / 2;

        imageGridView = findViewById(R.id.imageGridView);
        imageGridView.setColumnWidth(columnWidth);
        searchView = findViewById(R.id.imageSearcher);

        // set up search view
        imageItems = new ArrayList<>();
        imageGridAdapter = new ImageGridAdapter(this, imageItems, columnWidth);
        imageGridView.setAdapter(imageGridAdapter);



        // set up spinner
        imageSpinner = findViewById(R.id.imageSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.image_action_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        imageSpinner.setAdapter(adapter);




        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Browse Images");
            actionBar.setDisplayHomeAsUpEnabled(true); // Enable the back button
        }

        imageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Update gridview based on selected filter
                String selectedFilter = parent.getItemAtPosition(position).toString();
                if(uid == null){
                    testBrowseImage(); // Only use static data when UID is null
                }else{
                    adminController.getCurrentImageList("", false, imageGridAdapter, selectedFilter);
                }
                searchView.setQuery("", false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Default to event images when no filter is selected
                if(uid != null){
                    adminController.getCurrentImageList("", false, imageGridAdapter, "Event Images");
                }else{
                    testBrowseImage();
                }
            }
        });


        imageGridView.setOnItemClickListener((parent, view, position, id) -> {
            Image selectedItem = imageItems.get(position);

            // Navigate to delete image page
            Intent intent = new Intent(AdminBrowseImage.this, AdminDeleteImage.class);
            intent.putExtra("ImageID", selectedItem.getId());
            intent.putExtra("ImageURL", selectedItem.getURL());
            intent.putExtra("selectedFilter", imageSpinner.getSelectedItem().toString());
            startActivity(intent);

        });

        setUpSearchView();
        String selectedFilter = imageSpinner.getSelectedItem().toString();


        uid = FirebaseAuth.getInstance().getUid();


        if(uid != null){
            adminController.subscribeToImageDB(imageGridAdapter, selectedFilter);
        }else{
            testBrowseImage();
        }

    }

    /**
     * Sets up the search view with query listeners.
     */
    private void setUpSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(uid == null){
                    filterStaticImageList(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(uid == null){
                    filterStaticImageList(newText); // Use static data filtering when UID is null
                }else{
                    String selectedFilter = imageSpinner.getSelectedItem().toString();
                    adminController.getCurrentImageList(newText, !TextUtils.isEmpty(newText), imageGridAdapter, selectedFilter);
                }
                return true;
            }
        });
    }



    /**
     * This method is called when the up button is pressed.
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    /**
     * Called whenever an item in your options menu is selected.
     *
     * @param item The menu item that was selected.
     * @return  Return false to allow normal menu processing, true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Check if the correct item was clicked
        if (item.getItemId() == android.R.id.home) {
            // Handle the action when the up button is pressed
            return onSupportNavigateUp();
        }
        return super.onOptionsItemSelected(item);
    }

    // Re-fetch image data on back navigations
    @Override
    protected void onResume() {
        super.onResume();
        if(uid == null){
            testBrowseImage(); // Ensure static data is used on resume when UID is null
        }else{
            String currentFilter = imageSpinner.getSelectedItem().toString();
            adminController.subscribeToImageDB(imageGridAdapter, currentFilter);
        };
    }

    private void testBrowseImage(){
        ArrayList<Image> staticImages = new ArrayList<>() ;
        staticImages.add(new Image("https://firebasestorage.googleapis.com/v0/b/qr-code-app-6fe73.appspot.com/o/DALL%C2%B7E%202024-03-06%2015.04.40%20-%20An%20underwater%20scene%20with%20a%20coral%20reef%2C%20teeming%20with%20colorful%20tropical%20fish.%20Sunlight%20is%20filtering%20through%20the%20water%2C%20creating%20a%20dappled%20effect%20on%20the%20.webp?alt=media&token=d392fb5e-58b8-4a52-8273-74b0677e92a9", "TestImage1"));
        staticImages.add(new Image("https://firebasestorage.googleapis.com/v0/b/qr-code-app-6fe73.appspot.com/o/defaultImages%2FScreen%20Shot%202024-03-31%20at%2011.48.45%20PM.png?alt=media&token=33e5f2f2-0b9a-4372-9c30-1a212c833a34", "TestImage2"));
        imageGridAdapter.setFilter(staticImages);
        imageGridAdapter.notifyDataSetChanged();
    }

    public void filterStaticImageList(String searchText){
        String eventStaticName ;
        Image staticImage ;
        ArrayList<Image> searchResults = new ArrayList<>();
        for(int i = 0 ; i < imageItems.size() ; i++){
            staticImage = imageItems.get(i) ;
            eventStaticName = imageItems.get(i).getId() ;
            if (eventStaticName.toLowerCase().contains(searchText.toLowerCase())) {
                searchResults.add(new Image(staticImage.getURL(),  staticImage.getId()));
            }
        }
        imageGridAdapter.setFilter(searchResults);
        imageGridAdapter.notifyDataSetChanged();
    }

}
