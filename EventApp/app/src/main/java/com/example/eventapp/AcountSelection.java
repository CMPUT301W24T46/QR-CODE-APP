package com.example.eventapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.eventapp.users.UserDB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class AcountSelection extends Fragment {

    String[] accountOptionsData = {"Attend Event" , "Organize Event" , "Admin"} ;
    ListView accountOptionsListView ;

    NavController navController ;

    SelectOptionsAdapter accountOptionsAdapter ;

    Activity activity ;

    Context context ;

    private FirebaseAuth mAuth ;

    private FirebaseFirestore dbQRApp ;

    private CollectionReference userRef;

    private UserDB userDB ;

    public AcountSelection() {
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
        return inflater.inflate(R.layout.fragment_acount_selection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        accountOptionsListView = view.findViewById(R.id.accountOptionList) ;
        accountOptionsAdapter = new SelectOptionsAdapter(requireContext(), accountOptionsData) ;
        accountOptionsListView.setAdapter(accountOptionsAdapter);

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
        if(selectedAccount.equals("Attend Event")){
            userDB.setNavController(selectAccountController);
            userDB.getUserInfoAttendee();
        }else if(selectedAccount.equals("Organize Event")){
            selectAccountController.navigate(R.id.action_acountSelection_to_organizerActivity);
        }else if(selectedAccount.equals("Admin")){
            selectAccountController.navigate(R.id.action_acountSelection_to_adminActivity);
        }
    }

}