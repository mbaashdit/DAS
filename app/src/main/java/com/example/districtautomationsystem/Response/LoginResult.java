package com.example.districtautomationsystem.Response;

import com.google.gson.annotations.SerializedName;

public class LoginResult {
    @SerializedName("name")
    private String name;
    @SerializedName("userId")
    private String userId;
    @SerializedName("emailid")
    private String emailid;
    @SerializedName("phoneNo")
    private String phoneNo;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmailid() {
        return emailid;
    }

    public void setEmailid(String emailid) {
        this.emailid = emailid;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }


}
