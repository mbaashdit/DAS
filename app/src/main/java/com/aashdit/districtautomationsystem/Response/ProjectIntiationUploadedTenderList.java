package com.aashdit.districtautomationsystem.Response;

import com.google.gson.annotations.SerializedName;

public class ProjectIntiationUploadedTenderList {
    @SerializedName("tenderId")
    private int tenderId;
    @SerializedName("tenderCode")
    private String tenderCode;
    @SerializedName("agencyName")
    private String agencyName;
    @SerializedName("aggrementDate")
    private String aggrementDate;
    @SerializedName("aggrementValue")
    private String aggrementValue;
    @SerializedName("timeLine")
    private String timeLine;
    @SerializedName("projectId")
    private String projectId;
    @SerializedName("projectName")
    private String projectName;
    @SerializedName("projectCode")
    private String projectCode;
    @SerializedName("photoURL")
    private String photoURL;
    @SerializedName("latitude")
    private String latitude;
    @SerializedName("longitude")
    private String longitude;
    @SerializedName("remark")
    private String remark;

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public int getTenderId() {
        return tenderId;
    }

    public void setTenderId(int tenderId) {
        this.tenderId = tenderId;
    }

    public String getTenderCode() {
        return tenderCode;
    }

    public void setTenderCode(String tenderCode) {
        this.tenderCode = tenderCode;
    }

    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    public String getAggrementDate() {
        return aggrementDate;
    }

    public void setAggrementDate(String aggrementDate) {
        this.aggrementDate = aggrementDate;
    }

    public String getAggrementValue() {
        return aggrementValue;
    }

    public void setAggrementValue(String aggrementValue) {
        this.aggrementValue = aggrementValue;
    }

    public String getTimeLine() {
        return timeLine;
    }

    public void setTimeLine(String timeLine) {
        this.timeLine = timeLine;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }
}
