package com.finickyltd.finicky.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchItems {
    @SerializedName("searchItems")
    private List<SearchItem> searchItems;

    public List<SearchItem> getSearchItems() {
        return searchItems;
    }

    public void setSearchItems(List<SearchItem> searchItems) {
        this.searchItems = searchItems;
    }
}
