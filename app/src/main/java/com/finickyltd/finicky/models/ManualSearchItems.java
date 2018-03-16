package com.finickyltd.finicky.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by common on 9/7/2017.
 */

public class ManualSearchItems {
    @SerializedName("error")
    private String error;

    @SerializedName("status")
    private Boolean staus;

    @SerializedName("products")
    private List<SearchItem> items;


    public List<SearchItem> getItems() {
        return items;
    }

    public String getError() {
        return error;
    }

    public Boolean getStaus() {
        return staus;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setItems(List<SearchItem> items) {
        this.items = items;
    }

    public void setStaus(Boolean staus) {
        this.staus = staus;
    }
}
