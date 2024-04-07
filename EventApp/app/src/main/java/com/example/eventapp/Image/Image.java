package com.example.eventapp.Image;

/**
 * The Image class represents an image entity, typically associated with an event or user profile
 * in the application. It encapsulates details such as the URL of the image, optionally its unique identifier,
 * and a description.
 */

public class Image {
    private String URL;
    private String id; // Optional, if you need a reference to the document ID
    private String description; // Optional, description of image

    /**
     * Constructs a new Image instance with a specified URL and identifier.
     * This constructor is useful when the image is associated with a database record,
     * and an identifier is available.
     *
     * @param URL The URL where the image is located.
     * @param id  The unique identifier of the image, such as a database document ID.
     */

    public Image(String URL, String id) {
        this.URL = URL;
        this.id = id;
    }

    /**
     * Constructs a new Image instance with only a specified URL.
     * This constructor can be used when the image identifier is not necessary or available.
     *
     * @param URL The URL where the image is located.
     */

    public Image(String URL) {
        this.URL = URL;
    }

    /**
     * Retrieves the URL of the image.
     *
     * @return A string representing the URL of the image.
     */

    public String getURL() {
        return URL;
    }


    /**
     * Sets the URL of the image.
     *
     * @param URL The new URL of the image.
     */

    public void setURL(String URL) {
        this.URL = URL;
    }

    /**
     * Retrieves the unique identifier of the image.
     *
     * @return A string representing the unique identifier of the image.
     */

    public String getId() {
        return id;
    }

    // Constructor, getters, and setters
}