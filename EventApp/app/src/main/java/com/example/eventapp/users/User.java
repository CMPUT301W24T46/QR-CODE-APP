package com.example.eventapp.users;

public class User {

    private String id ;
    private String name ;
    private String email;

    private String homepage ;
    private byte[] imageData ;

    private String contactInformation ;

    private String role;

    public User() {

    };

    public User(String id ,String name , String contactInformation ,String homepage , byte[] imageData , String role){
        this.id = id ;
        this.name = name ;
        this.contactInformation = contactInformation ;
        this.homepage = homepage ;
        this.imageData = imageData ;
        this.role = role;
    }


    public String getId() {
        return id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public String getContactInformation() {
        return contactInformation;
    }

    public void setContactInformation(String contactInformation) {
        this.contactInformation = contactInformation;
    }

    public String getRole() {
        return this.role;
    }

}
