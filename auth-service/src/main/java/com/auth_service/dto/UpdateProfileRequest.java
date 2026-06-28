package com.auth_service.dto;

public class UpdateProfileRequest {
    private String phoneNumber;
    private String address;
    private String bio;

    public UpdateProfileRequest() {}

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
}
