package com.example.stayfit.model;

public class BlogModel {

    String author;
    String username;
    String dateuploaded;
    String work;
    String imageurl;
  //  int totallikes;

    public BlogModel() {
    }

    public BlogModel(String author, String username, String dateuploaded, String work, String imageurl) { //int totallikes
        this.author = author;
        this.username = username;
        this.dateuploaded = dateuploaded;
        this.work = work;
        this.imageurl = imageurl;
      //  this.totallikes = totallikes;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDateUploaded() {
        return dateuploaded;
    }

    public void setDateUploaded(String dateuploaded) {
        this.dateuploaded = dateuploaded;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getImageUrl() {
        return imageurl;
    }

    public void setImageUrl(String imageurl) {
        this.imageurl = imageurl;
    }
//
//    public int getTotalLikes() {
//        return totallikes;
//    }
//
//    public void setTotalLikes(int totallikes) {
//        this.totallikes = totallikes;
//    }
}
