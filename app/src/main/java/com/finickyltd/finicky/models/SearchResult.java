package com.finickyltd.finicky.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by common on 9/6/2017.
 */

public class SearchResult {
    @SerializedName("status")   // true is found, false is not found
    private Boolean status;

    @SerializedName("message")
    private String message;

    @SerializedName("error")
    private String error;

    @SerializedName("product_name")
    private String productName;

    public SearchResult(){

    }

    public Boolean getStatus(){return this.status;}

    public String getMessage(){return  this.message;}

    public String getError(){return this.error;}

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
