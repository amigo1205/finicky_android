package com.finickyltd.finicky.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by common on 9/7/2017.
 */

public class FilterItem {
    @SerializedName("status")
    public String status;

    @SerializedName("title")
    public String title;

    @SerializedName("desc")
    private String desc;

    public String getStatus(){return status;}

    public String getDesc(){return desc;}

    public String getTitle(){return title;}
}
