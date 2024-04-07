package com.example.eventapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * The main entry point of the application.
 * This activity serves as the initial screen that users interact with. It presents options for attending
 * or organizing an event, as well as an option for administrative tasks. The user's selection determines
 * the subsequent navigation path within the application.
 */

//Main Entry point of the application
//Attached to main activity
public class MainActivity extends AppCompatActivity {

    /**
     * An array of strings representing the different account options available to the user.
     */

    String[] accountOptionsData = {"Attend Event" , "Organize Event" , "Admin"} ;

    /**
     * The ListView widget used to display the account options.
     */
    ListView accountOptionsListView ;

    /**
     * The NavController responsible for managing app navigation within a NavHost.
     */

    NavController navController ;

    /**
     * The adapter used to bind the account options data to the ListView.
     */

    SelectOptionsAdapter accountOptionsAdapter ;

    /**
     * Called when the activity is starting. This method initializes the activity, inflates the UI,
     * and sets up navigation and the account options ListView.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     *                           down, this Bundle contains the most recent data supplied to onSaveInstanceState(Bundle).
     *                           Otherwise, it is null.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
    }
}