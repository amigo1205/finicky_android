package com.finickyltd.finicky.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProductResult {
    @SerializedName("status")   // if status is true, product is found, if status is false, product is not found
    private Boolean status;

    @SerializedName("product_name")
    private String productName;

    @SerializedName("error")
    private String error;

    @SerializedName("ingredients")
    private String ingredients;

    @SerializedName("SPLASH_NOTSUITABLE")
    private Integer notSuitable;

    @SerializedName("SPLASH_MAYBESUITABLE")
    private Integer maybeSuitable;

    @SerializedName("filterResult")
    private List<FilterItem>  filterItems;

    public ProductResult(){

    }

    public Boolean getStatus(){return status;}

    public String getError(){return error;}

    public List<FilterItem> getFilterItems(){return filterItems;}

    public String getIngredients(){return ingredients;}

    public String getProductName(){return productName; }

    public Integer getMaybeSuitable() {
        return maybeSuitable;
    }

    public Integer getNotSuitable() {
        return notSuitable;
    }
}
