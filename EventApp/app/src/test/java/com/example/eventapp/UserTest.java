package com.example.eventapp;
import org.junit.Test;
import static org.junit.Assert.*;

import com.example.eventapp.users.User;

public class UserTest {

    @Test
    public void testGettersAndSetters() {
        User user = new User("1", "John Doe", "john@example.com", "http://www.example.com", "http://www.example.com/image.jpg", "Regular", "1234567890");
        user.getId("1");
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setHomepage("http://www.example.com");
        user.setImageURL("http://www.example.com/image.jpg");
        user.setTypeOfUser("Regular");
        user.setContactInformation("1234567890");

        assertEquals("1", user.getId("1"));
        assertEquals("John Doe", user.getName());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("http://www.example.com", user.getHomepage());
        assertEquals("http://www.example.com/image.jpg", user.getImageURL());
        assertEquals("Regular", user.getTypeOfUser());
        assertEquals("1234567890", user.getContactInformation());
    }

    @Test
    public void testConstructor() {
        User user = new User("1", "John Doe", "john@example.com", "http://www.example.com", "http://www.example.com/image.jpg", "Regular", "1234567890");

        assertEquals("1", user.getId("1"));
        assertEquals("John Doe", user.getName());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("http://www.example.com", user.getHomepage());
        assertEquals("http://www.example.com/image.jpg", user.getImageURL());
        assertEquals("Regular", user.getTypeOfUser());
        assertEquals("1234567890", user.getContactInformation());
    }

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