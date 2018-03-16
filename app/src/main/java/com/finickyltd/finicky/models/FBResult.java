package com.finickyltd.finicky.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by common on 9/11/2017.
 */

public class FBResult {
    @SerializedName("error")
    private Boolean error;

    @SerializedName("message")
    private String message;

    @SerializedName("user")
    private FbUser fbUser;

    @SerializedName("isFilter")
    private  Boolean isFilter;

    public Boolean getError() {
        return error;
    }

    public FbUser getFbUser() {
        return fbUser;
    }

    public String getMessage() {
        return message;
    }

    public Boolean getFilter() {
        return isFilter;
    }
}
