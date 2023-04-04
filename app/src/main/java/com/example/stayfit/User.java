package com.example.stayfit;

public class User {

    String email;
    String uid;
    String name;
    String imageUrl;
    int weight;
    int height;

    public User() {
    }

    public User(String email, String uid, String name, String imageUrl, int weight, int height, String gender, int dob) {
        this.email = email;
        this.uid = uid;
        this.name = name;
        this.imageUrl = imageUrl;
        this.weight = weight;
        this.height = height;
        this.gender = gender;
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getDob() {
        return dob;
    }

    public void setDob(int dob) {
        this.dob = dob;
    }

    String gender;
    int dob;
}
