package com.aashdit.districtautomationsystem.Response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetProjectIntiationUploadedTender {
    @SerializedName("status")
    private String status;
    @SerializedName("message")
    private String message;
    @SerializedName("result")
    private ArrayList<ProjectIntiationUploadedTenderList> result;

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

    public ArrayList<ProjectIntiationUploadedTenderList> getResult() {
        return result;
    }

    public void setResult(ArrayList<ProjectIntiationUploadedTenderList> result) {
        this.result = result;
    }
}
