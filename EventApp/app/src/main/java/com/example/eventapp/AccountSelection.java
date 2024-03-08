package com.example.eventapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.eventapp.users.UserDB;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountSelection extends Fragment {

    String[] accountOptionsData = {"Attend Event" , "Organize Event" , "Admin"} ;
    ListView accountOptionsListView ;

    NavController navController ;

    SelectOptionsAdapter accountOptionsAdapter ;

    Activity activity ;

    Context context ;

    private FirebaseAuth mAuth ;

    private FirebaseFirestore dbQRApp ;

    private CollectionReference userRef;

    private NavController testAccountNavController ;
    private UserDB userDB ;


    public AccountSelection() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = requireActivity() ;
        context = requireContext() ;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account_selection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        accountOptionsListView = view.findViewById(R.id.accountOptionList) ;
        accountOptionsAdapter = new SelectOptionsAdapter(requireContext(), accountOptionsData) ;
        accountOptionsListView.setAdapter(accountOptionsAdapter);
//        testAccountNavController = Navigation.findNavController(view) ;
        accountOptionsListView.setOnItemClickListener((parent , v , position , id)->{
            navigateToAccount(v , parent , position);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        dbQRApp = FirebaseFirestore.getInstance();
        userRef = dbQRApp.collection("Users");
        userDB = new UserDB(context ,activity , dbQRApp) ;
    }

    //    This function enables navigation to the main parts of the app which is the
//    AttendeeActivity , OrganizerActivity , AdminActivity
    private void navigateToAccount(View view , AdapterView<?> parent , int position){
        String selectedAccount = (String) parent.getItemAtPosition(position) ;
        NavController selectAccountController = Navigation.findNavController(view) ;
        if(mAuth == null){
            testAccountNavController = selectAccountController ;
//            testNavigateToAccount(view ,  parent , position);
            return;
        }

        if(selectedAccount.equals("Attend Event")){
            userDB.setNavController(selectAccountController);
            userDB.setNavigationPageId(R.id.action_accountSelection_to_attendeeActivity) ;
            userDB.getUserInfoAttendee();
//            selectAccountController.navigate(R.id.action_accountSelection_to_attendeeActivity);

        }else if(selectedAccount.equals("Organize Event")){
            Log.d("Organizer" , "Page Clicked") ;
            userDB.setNavController(selectAccountController);
            userDB.setNavigationPageId(R.id.action_accountSelection_to_organizerActivity) ;
            userDB.getUserInfoAttendee();
//            selectAccountController.navigate(R.id.action_accountSelection_to_organizerActivity);
        }else if(selectedAccount.equals("Admin")){
            selectAccountController.navigate(R.id.action_accountSelection_to_adminActivity);
        }
    }

    public void testNavigateToAccount(String page , NavController appNav){
        testAccountNavController = appNav ;
        if(page.equals("Attend Event")){
            testAccountNavController.navigate(R.id.action_accountSelection_to_attendeeActivity);
        }else if(page.equals("Organize")){
            testAccountNavController.navigate(R.id.action_accountSelection_to_organizerActivity);
        }else if(page.equals("Admin")){
            testAccountNavController.navigate(R.id.action_accountSelection_to_adminActivity);
        }
    }
}