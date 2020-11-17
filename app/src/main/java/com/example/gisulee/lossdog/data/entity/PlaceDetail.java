package com.example.gisulee.lossdog.data.entity;

public class PlaceDetail {

    private String phoneNumber;
    private String photoRef;

    public PlaceDetail(String phoneNumber, String photoRef) {
        this.phoneNumber = phoneNumber;
        this.photoRef = photoRef;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhotoRef() {
        return photoRef;
    }

    public void setPhotoRef(String photoRef) {
        this.photoRef = photoRef;
    }
}
