package com.wizy.wallpaper.models;

import java.io.Serializable;

public class User implements Serializable {

    private String username ;
private Profile_image profile_image;

    public Profile_image getProfile_image() {
        return profile_image;
    }

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
