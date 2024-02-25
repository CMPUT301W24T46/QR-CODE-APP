package com.example.eventapp;

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

    private FirebaseAuth mAuth ;

    private FirebaseFirestore dbQRApp ;

    private CollectionReference userRef;

    public AcountSelection() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        dbQRApp = FirebaseFirestore.getInstance();
        userRef = dbQRApp.collection("Users");
//        if (getArguments() != null) {
//
//        }
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

//    This function enables navigation to the main parts of the app which is the
//    AttendeeActivity , OrganizerActivity , AdminActivity
    private void navigateToAccount(View view , AdapterView<?> parent , int position){
        String selectedAccount = (String) parent.getItemAtPosition(position) ;
        NavController selectAccountController = Navigation.findNavController(view) ;
//        {"Attend Event" , "Organize Event" , "Admin"} ;
        if(selectedAccount.equals("Attend Event")){
            setmAuth() ;
            selectAccountController.navigate(R.id.action_acountSelection_to_attendeeActivity);
        }else if(selectedAccount.equals("Organize Event")){
            selectAccountController.navigate(R.id.action_acountSelection_to_organizerActivity);
        }else if(selectedAccount.equals("Admin")){
            selectAccountController.navigate(R.id.action_acountSelection_to_adminActivity);
        }
    }

    private void setmAuth(){
        mAuth.signInAnonymously()
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInAnonymously:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            // You can update UI or perform other actions here

                            if(task.getResult().getAdditionalUserInfo().isNewUser()){
                                String uid = user.getUid() ;
                                HashMap<String, String> data = new HashMap<>();
                                data.put("id", uid);
                                data.put("name", "");
                                data.put("homepage", "");
                                data.put("contactInformation", "");
                                userRef.document(uid)
                                        .set(data)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("Firestore", "DocumentSnapshot successfully written!");
                                            }});
                            }else{
//                                Store user information and pass on later
                                String uid = user.getUid() ;
                                HashMap<String, String> data = new HashMap<>();
                                data.put("id", uid);
                                data.put("name", "information ");
                                data.put("homepage", "Good");
                                data.put("contactInformation", "");
                                userRef.document(uid)
                                        .set(data)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("Firestore", "DocumentSnapshot successfully written!");
                                            }});
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInAnonymously:failure", task.getException());
                            Toast.makeText(requireContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}