package com.finickyltd.finicky.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by common on 9/13/2017.
 */

public class FilterResult {
    @SerializedName("error")
    private Boolean error;

    @SerializedName("message")
    private String message;

    @SerializedName("filters")
    private FilterModel filterModel;

    public String getMessage() {
        return message;
    }

    public FilterModel getFilterModel() {
        return filterModel;
    }

    public Boolean getError() {
        return error;
    }
}
