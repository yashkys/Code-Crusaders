package com.example.stayfit.model;

public class Doctor {

    String email;
    String username;
    String name;
    String imageurl;
    String education;
    String speciality;
    String experience;
    int rating;
    String availableDays;
    String availableTime;
    String languagesSpoken;

    public Doctor() {
    }

    public Doctor(String email, String username, String name, String imageUrl, String education, String speciality,
                  String experience, int rating, String availableDays, String availableTime, String languagesSpoken) {
        this.email = email;
        this.username = username;
        this.name = name;
        this.imageurl = imageUrl;
        this.education = education;
        this.speciality = speciality;
        this.experience = experience;
        this.rating = rating;
        this.availableDays = availableDays;
        this.availableTime = availableTime;
        this.languagesSpoken = languagesSpoken;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getAvailableDays() { return availableDays; }

    public void setAvailableDays(String available_days) {
        this.availableDays = available_days;
    }

    public String getAvailableTime() {
        return availableTime;
    }

    public void setAvailableTime(String available_time) {
        this.availableTime = available_time;
    }

    public String getLanguagesSpoken() {
        return languagesSpoken;
    }

    public void setLanguagesSpoken(String languages_spoken) { this.languagesSpoken = languages_spoken; }
}
