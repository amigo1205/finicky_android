package com.finickyltd.finicky.models;

import com.google.gson.annotations.SerializedName;

public class SearchItem {
    @SerializedName("id")
    private int id;

    @SerializedName("productName")
    private String productName;

    @SerializedName("barcodeContent")
    private String barcodeContent;

    @SerializedName("suitableType")
    private int suitableType;

    @SerializedName("product_source")
    private String productSource;

    public int getId() {
        return id;
    }

    public String getProductName() {
        return productName;
    }

    public String getBarcodeContent() {
        return barcodeContent;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setBarcodeContent(String barcodeContent) {
        this.barcodeContent = barcodeContent;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getSuitableType() {
        return suitableType;
    }

    public void setSuitableType(int suitableType) {
        this.suitableType = suitableType;
    }

    public String getProductSource() {
        return productSource;
    }

}
