package com.example.myapplication;

public class ModelUserModel {
    
    private String name ;
    private String email ;
    private String phone ;
    private String id ;
    private String imageURL  ;

    public ModelUserModel() {}

    public ModelUserModel(String id ,String name, String email, String phone , String imageURL) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.id = id;
        this.imageURL =imageURL;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }
}
