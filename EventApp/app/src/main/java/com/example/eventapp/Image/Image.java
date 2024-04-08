package com.example.eventapp.Image;

public class Image {
    private String URL;
    private String id; // Optional, if you need a reference to the document ID
    private String description; // Optional, description of image

    public Image(String URL, String id) {
        this.URL = URL;
        this.id = id;
    }

    public Image(String URL) {
        this.URL = URL;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getId() {
        return id;
    }

    // Constructor, getters, and setters
}