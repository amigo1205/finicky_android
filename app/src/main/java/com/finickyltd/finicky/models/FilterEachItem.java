package com.finickyltd.finicky.models;

/**
 * Created by common on 9/13/2017.
 */

public class FilterEachItem {
    public String key;
    public boolean value;

    public FilterEachItem(String key, boolean value){
        this.key = key;
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    public String getKey() {
        return key;
    }
}
