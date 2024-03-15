package com.example.eventapp;
import org.junit.Test;
import static org.junit.Assert.*;

import com.example.eventapp.users.User;

public class UserTest {


    @Test
    public void testSetName() {
        User user = new User("1", "John Doe", "john@example.com", "http://www.example.com", "http://www.example.com/image.jpg", "Regular", "1234567890");
        user.setName("John Doe");

        assertEquals("John Doe", user.getName());
    }

    @Test
    public void testSetHomepage() {
        User user = new User("1", "John Doe", "john@example.com", "http://www.example.com", "http://www.example.com/image.jpg", "Regular", "1234567890");
        user.setHomepage("http://www.example.com");

        assertEquals("http://www.example.com", user.getHomepage());
    }

    @Test
    public void testSetContactInformation() {
        User user = new User("1", "John Doe", "john@example.com", "http://www.example.com", "http://www.example.com/image.jpg", "Regular", "1234567890");
        user.setContactInformation("1234567890");

        assertEquals("1234567890", user.getContactInformation());
    }

    // Add more test cases as needed
}