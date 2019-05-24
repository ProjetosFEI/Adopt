package com.example.adoptapp;

public class MatchesObject {
    private String userId;
    private String name;
    private String phone;
    private String profileImageUrl;
    public MatchesObject (String userId, String name, String profileImageUrl, String phone){
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.profileImageUrl = profileImageUrl;
    }

    public String getUserId(){
        return userId;
    }

    public String getPhone(){
        return phone;
    }

    public void setUserID(String userID){
        this.userId = userId;
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    public String getProfileImageUrl(){
        return profileImageUrl;
    }
    public void setProfileImageUrl(String profileImageUrl){
        this.profileImageUrl = profileImageUrl;
    }
}
