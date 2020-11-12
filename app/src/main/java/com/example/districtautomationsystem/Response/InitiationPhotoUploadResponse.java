package com.example.districtautomationsystem.Response;

import com.google.gson.annotations.SerializedName;

public class InitiationPhotoUploadResponse {

    @SerializedName("outcome")
    private String outcome;

    public String  isOutcome() {
        return outcome;
    }

}
