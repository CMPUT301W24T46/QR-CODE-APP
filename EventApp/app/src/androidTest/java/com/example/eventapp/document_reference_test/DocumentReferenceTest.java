package com.example.eventapp.document_reference_test;

import com.example.eventapp.document_reference.DocumentReferenceChecker;
import com.google.firebase.firestore.DocumentReference;

import org.junit.Test;

import static org.junit.Assert.*;

import android.util.Log;
import org.mockito.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.assertTrue;
public class DocumentReferenceTest {
    private DocumentReferenceChecker mockDocumentReferenceChecker() {
        return new DocumentReferenceChecker();
    }

    @Test
    public void testGetDocReference() throws InterruptedException{
        DocumentReferenceChecker docCheck = new DocumentReferenceChecker() ;
        DocumentReference resultRef = docCheck.documentReferenceWrite() ;

        // Assert that the returned DocumentReference is not null
        assertNotNull(resultRef) ;
        // Assert that the collection path of the returned DocumentReference is "defaultImage"
        assertEquals("defaultImage", resultRef.getParent().getId());

        // Assert that the document ID of the returned DocumentReference is "NoImage"
        assertEquals("NoImage", resultRef.getId());
    }

    @Test
    public void testDocumentReferenceUserWrite() throws InterruptedException {

    }




}
