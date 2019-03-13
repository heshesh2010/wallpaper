package com.wizy.wallpaper.models;

import java.io.Serializable;

public class Profile_image implements Serializable {
    private String large;

    public Profile_image() {
    }

    public String getLarge() {
        return large;
    }

    public void setLarge(String large) {
        this.large = large;
    }
}
