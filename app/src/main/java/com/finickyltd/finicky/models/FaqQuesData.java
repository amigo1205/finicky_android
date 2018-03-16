package com.finickyltd.finicky.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by common on 10/20/2017.
 */

public class FaqQuesData {
    @SerializedName("error")
    private Boolean error;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<QuestionData> quesData;

    public Boolean getError() {
        return error;
    }

    public List<QuestionData> getQuesData() {
        return quesData;
    }

    public String getMessage() {
        return message;
    }
}
