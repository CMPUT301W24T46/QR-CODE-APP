package com.example.eventapp.document_reference;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

public class DocumentReferenceChecker {
    public static boolean documentChecker(){
        return true ;
    }

    public DocumentReference documentReferenceWrite(){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference referenceDocRef = firestore.collection("defaultImage").document("NoImage");
        return referenceDocRef ;
    }

    public void documentReferenceUserWrite(String uid ){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference usersRef = firestore.collection("Users");
        DocumentReference docRef = firestore.collection("profileImages").document(uid) ;

        HashMap<String, Object> data = new HashMap<>();
        data.put("imageUrl", docRef);

        usersRef.document(uid)
                .update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("User Profile", "Successfuly written to with Updated Information");
                    }});
    }

    public void emptyDocumentReferenceWrite(String uid){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference usersRef = firestore.collection("Users");
        DocumentReference docRef = firestore.collection("defaultImage").document("NoImage") ;
        HashMap<String, Object> data = new HashMap<>();
        data.put("imageUrl", docRef);

        usersRef.document(uid)
                .update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore 2", "DocumentSnapshot successfully written!");
                    }});
    }
}
