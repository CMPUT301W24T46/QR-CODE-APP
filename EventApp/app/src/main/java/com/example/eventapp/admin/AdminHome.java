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
 * Use the {@link AdminHome#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminHome extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ListView adminListView;
    private AdminOptionsAdapter adminOptionsAdapter;
    private String[] adminOptions = new String[]{"Browse Profiles", "Browse Events", "Browse Images"};



    public AdminHome() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminHome.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminHome newInstance(String param1, String param2) {
        AdminHome fragment = new AdminHome();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
//            adminNavController.navigate(R.id.action_accountSelection_to_adminActivity);
        }
    }

}