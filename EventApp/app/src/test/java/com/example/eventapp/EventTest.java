//package com.example.eventapp;
//import org.junit.Test;
//import static org.junit.Assert.*;
//
//import com.example.eventapp.event.Event;
//
//public class EventTest {
//
//    @Test
//   public void testEventConstructorWithImageDescription() {
//        String eventName = "Example Event";
//        String imageDescription = "Description of the event";
//        String imageURL = "https://example.com/image.jpg";
//
//        Event event = new Event(eventName, imageDescription, imageURL);
//
//        assertEquals(eventName, event.getEventName());
//        assertEquals(imageDescription, event.getImageDescription());
//        assertEquals(imageURL, event.getImageURL());
//    }
//
//    @Test
//    public void testEventConstructorWithoutImageDescription() {
//        String eventName = "Example Event";
//        String imageURL = "https://example.com/image.jpg";
//
//        Event event = new Event(eventName, imageURL);
//
//        assertEquals(eventName, event.getEventName());
//        assertNull(event.getImageDescription());
//        assertEquals(imageURL, event.getImageURL());
//    }
//
//    @Test
//    public void testSettersAndGetters() {
//        String eventName = "Example Event";
//        String imageDescription = "Description of the event";
//        String imageURL = "https://example.com/image.jpg";
//
//        Event event = new Event(eventName, imageURL);
//
//        // Test setters
//        event.setEventName("New Event Name");
//        event.setImageDescription("New Image Description");
//        event.setImageURL("https://example.com/new-image.jpg");
//
//        // Test getters
//        assertEquals("New Event Name", event.getEventName());
//        assertEquals("New Image Description", event.getImageDescription());
//        assertEquals("https://example.com/new-image.jpg", event.getImageURL());
//    }
//}
