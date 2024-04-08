package com.example.eventapp.helpers;

import java.util.ArrayList;
import java.util.Collections;

/**
 * ReverseEventList is a utility class that provides a method to reverse the order of elements in an ArrayList.
 * This can be particularly useful for reversing the order of a list of event identifiers or notification messages
 * to display them in a different order, such as from most recent to oldest.
 */

public class ReverseEventList {

    /**
     * Constructs a ReverseEventList instance. This constructor doesn't do anything specific
     * as the class is intended to provide a utility method and does not maintain state.
     */

    public  ReverseEventList(){

    }

    /**
     * Reverses the order of elements in the given ArrayList.
     * This method directly modifies the input list by reversing the order of its elements.
     *
     * @param notifyList The ArrayList whose elements are to be reversed in order.
     */

    public void reverseArrayList(ArrayList<String> notifyList){
        Collections.reverse(notifyList);
    }
}
