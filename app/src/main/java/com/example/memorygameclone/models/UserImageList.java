package com.example.memorygameclone.models;

import com.google.firebase.firestore.PropertyName;

import java.util.ArrayList;

public class UserImageList {

    @PropertyName("image") private ArrayList<String> images;

    public UserImageList() {
        this.images = null;
    }

    @Override
    public String toString() {
        return "UserImageList{" +
                "images=" + images +
                '}';
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }
}
