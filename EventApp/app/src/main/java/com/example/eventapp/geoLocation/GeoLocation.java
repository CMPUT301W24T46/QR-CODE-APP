package com.example.eventapp.geoLocation;

public class GeoLocation {
    double latitude;
    double longitude;

    // Default constructor for Firebase
    public GeoLocation() {}

    public GeoLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
