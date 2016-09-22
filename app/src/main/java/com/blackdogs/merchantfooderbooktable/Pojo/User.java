package com.blackdogs.merchantfooderbooktable.Pojo;

/**
 * Created by BlackDogs on 15-09-2016.
 */
public class User {

    String uname,email,profileImgUrl,phoneNumber;

    public User() {
    }

    public User(String uname, String email,String profileImgUrl, String phoneNumber) {
        this.uname = uname;
        this.email = email;
        this.profileImgUrl=profileImgUrl;
        this.phoneNumber = phoneNumber;

    }

    public User(String uname, String email,String profileImgUrl) {
        this.uname = uname;
        this.email = email;
        this.profileImgUrl=profileImgUrl;

    }

    public String getUname() {
        return uname;
    }

    public String getEmail() {
        return email;
    }

    public String getProfileImgUrl() {
        return profileImgUrl;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
