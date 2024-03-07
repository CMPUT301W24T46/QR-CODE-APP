package com.example.eventapp.document_reference_test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import com.example.eventapp.document_reference.DocumentReferenceChecker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Test;

public class DocumentReferenceCheckerTest {
    @Test
    public void testDocumentReferenceWrite() {
        // Mock FirebaseFirestore instance
        FirebaseFirestore firestore = mock(FirebaseFirestore.class);
        // Mock CollectionReference
        CollectionReference collectionReference = mock(CollectionReference.class);
        when(firestore.collection("defaultImage")).thenReturn(collectionReference);
        // Mock DocumentReference
        DocumentReference documentReference = mock(DocumentReference.class);
        when(collectionReference.document("NoImage")).thenReturn(documentReference);

        // Create an instance of DocumentReferenceChecker
        DocumentReferenceChecker checker = new DocumentReferenceChecker();
        checker.documentReferenceWrite();

        // Verify that the document reference was retrieved from Firestore
        verify(firestore).collection("defaultImage");
        verify(collectionReference).document("NoImage");
    }

}
