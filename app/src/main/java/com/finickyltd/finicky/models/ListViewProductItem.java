package com.finickyltd.finicky.models;

public class  ListViewProductItem {
    private String productName;
    private String barcodeContent;
    private String suitableType;
    private int suitableColor;
    private String imageUrl;
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getBarcodeContent() {
        return barcodeContent;
    }

    public void setBarcodeContent(String barcodeContent) {
        this.barcodeContent = barcodeContent;
    }

    public String getSuitableType() {
        return suitableType;
    }

    public void setSuitableType(String suitableType) {
        this.suitableType = suitableType;
    }

    public int getSuitableColor() {
        return suitableColor;
    }

    public void setSuitableColor(int suitableColor) {
        this.suitableColor = suitableColor;
    }

    public String getProductImageUrl() {
        return imageUrl;
    }

    public void setProductImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
