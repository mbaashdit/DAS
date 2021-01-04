package com.aashdit.districtautomationsystem.Response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetProjectIntiationResponse {
    @SerializedName("status")
    private String status;
    @SerializedName("message")
    private String message;
    @SerializedName("result")
    private ArrayList<resultResponse> result;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<resultResponse> getResult() {
        return result;
    }

    public void setResult(ArrayList<resultResponse> result) {
        this.result = result;
    }
}
