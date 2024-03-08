package com.example.eventapp.admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.eventapp.R;
import com.example.eventapp.SelectOptionsAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminHome} factory method to
 * create an instance of this fragment.
 */
public class AdminHome extends Fragment {

    private ListView adminListView;
    private AdminOptionsAdapter adminOptionsAdapter;
    private String[] adminOptions = new String[]{"Browse Profiles", "Browse Events", "Browse Images"};



    public AdminHome() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_admin_home, container, false);

        // Initialize the ListView and the adapter
        adminListView = rootView.findViewById(R.id.adminOptions);
        adminOptionsAdapter = new AdminOptionsAdapter(getContext(), adminOptions);

        // Set the adapter to the ListView
        adminListView.setAdapter(adminOptionsAdapter);

        // Set the item click listener if necessary
        adminListView.setOnItemClickListener((parent, view, position, id) -> {
            navigateToPage(view, parent, position);
        });

        return rootView;
    }


    private void navigateToPage(View view , AdapterView<?> parent , int position){
        String selectedPage = (String) parent.getItemAtPosition(position) ;
        NavController adminNavController = Navigation.findNavController(view) ;
        if(selectedPage.equals("Browse Profiles")){
            adminNavController.navigate(R.id.action_adminHome_to_adminProfiles);
        }else if(selectedPage.equals("Browse Events")){
            adminNavController.navigate(R.id.action_adminHome_to_adminEvents);
        }else if(selectedPage.equals("Browse Images")){
            adminNavController.navigate(R.id.action_adminHome_to_adminImages);
        }
    }

}