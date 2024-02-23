package com.example.eventapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

//Main Entry point of the application
//Attached to main activity
public class MainActivity extends AppCompatActivity {

    String[] accountOptionsData = {"Attend Event" , "Organize Event" , "Admin"} ;
    ListView accountOptionsListView ;

    NavController navController ;

    SelectOptionsAdapter accountOptionsAdapter ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
    }
}