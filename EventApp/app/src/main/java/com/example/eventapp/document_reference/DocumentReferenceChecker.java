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

public class DocumentReferenceChecker {
    public static boolean documentChecker(){
        return true ;
    }

    public static DocumentReference documentReferenceWrite(){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        // Create a map to store the reference to another document
        // Assuming you have another document ID you want to reference, replace "referenceDocumentId" with the actual ID
        DocumentReference referenceDocRef = firestore.collection("defaultImage").document("NoImage");
        return referenceDocRef ;
    }

    public static void documentReferenceUserWrite(String uid ){
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
                        Log.d("Firestore 2", "DocumentSnapshot successfully written!");
                    }});
    }

    public static void emptyDocumentReferenceWrite(String uid){
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
