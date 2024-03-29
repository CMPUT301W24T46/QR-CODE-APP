package com.example.eventapp;

import static org.junit.Assert.assertEquals;

import com.example.eventapp.helpers.ReverseEventList;

import org.junit.Test;

import java.util.ArrayList;

public class ReverseListTest {

    @Test
    public void testReverseArrayList() {
        // Create an instance of ReverseEventList
        ReverseEventList reverseEventList = new ReverseEventList();

        // Create an ArrayList
        ArrayList<String> list = new ArrayList<>();
        list.add("Yeno");
        list.add("Ron");
        list.add("David");

        // Reverse the ArrayList using the method to be tested
        reverseEventList.reverseArrayList(list);

        // Create an ArrayList with expected reversed order
        ArrayList<String> expected = new ArrayList<>();
        expected.add("David");
        expected.add("Ron");
        expected.add("Yeno");

        // Check if the reversed ArrayList matches the expected one
        assertEquals(expected, list);
    }
}
