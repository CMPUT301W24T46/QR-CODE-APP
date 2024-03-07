package com.example.eventapp.users;

public class User {

    private String id ;
    private String name ;
    private String email;

    private String homepage ;
    private String imageURL ;

    private String typeOfUser ;

    private String contactInformation ;

    public User(String number, String johnDoe, String mail, String url, String s, String regular, String id) {

    };
  
    public User(String id ,String name , String contactInformation ,String homepage , String imageURL , String typeOfUser){
      this.id = id ;
      this.name = name ;
      this.contactInformation = contactInformation ;
      this.homepage = homepage ;
      this.imageURL= imageURL ;
      this.typeOfUser = typeOfUser ;
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

    public String getContactInformation() {
        return contactInformation;
    }

    public void setContactInformation(String contactInformation) {
        this.contactInformation = contactInformation;
    }


     
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getTypeOfUser() {
        return typeOfUser;
    }

    public void setTypeOfUser(String typeOfUser) {
        this.typeOfUser = typeOfUser;
    }


}
