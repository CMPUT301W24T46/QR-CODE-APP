package com.example.eventapp.users;

public class Organizer extends User{

    private String username;
    private String email;

    public Organizer(String id , String name , String username, String email, String contactInformation , String homepage, byte[] imagePhoto , String typeOfUser){
        super(id , name , contactInformation , homepage , imagePhoto , typeOfUser);
        this.setUsername(username);
        this.setEmail(email);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
