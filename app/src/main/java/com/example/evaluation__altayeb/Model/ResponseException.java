package com.example.evaluation__altayeb.Model;

import com.google.gson.annotations.SerializedName;

public class ResponseException {
    @SerializedName("field")
    private String field;
    @SerializedName("message")
    private String message;

    public ResponseException(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public String getMessage() {
        return message;
    }
}
