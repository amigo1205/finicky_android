package com.finickyltd.finicky.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by common on 9/11/2017.
 */

public class FbUser {
    @SerializedName("id")
    private int id;

    @SerializedName("fb_id")
    private String fb_id;

    @SerializedName("fb_name")
    private String fb_name;

    @SerializedName("fb_email")
    private String fb_email;

    @SerializedName("password")
    private String password;

    @SerializedName("membership_type")
    private String membership_type;

    public int getId() {
        return id;
    }

    public String getFb_id() {
        return fb_id;
    }

    public String getFb_name() {
        return fb_name;
    }

    public String getPassword() {
        return password;
    }

    public String getFb_email() {
        return fb_email;
    }

    public String getMembership_type() {
        return membership_type;
    }

    public FbUser(int id, String fb_id, String fb_name){
        this.id = id;
        this.fb_id = fb_id;
        this.fb_name = fb_name;
    }

    public FbUser(int id, String fb_id, String fb_name, String membership_type){
        this.id = id;
        this.fb_id = fb_id;
        this.fb_name = fb_name;
        this.membership_type = membership_type;
    }
}
