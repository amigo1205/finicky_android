package com.finickyltd.finicky.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by common on 10/20/2017.
 */

public class QuestionData {
    @SerializedName("id")
    private int q_id;

    @SerializedName("category")
    private String category;

    @SerializedName("question")
    private String questionData;

    @SerializedName("answer")
    private String answerData;

    public int getQ_id() {
        return q_id;
    }

    public String getCategory() {
        return category;
    }

    public String getQuestionData() {
        return questionData;
    }

    public String getAnswerData() {
        return answerData;
    }
}
