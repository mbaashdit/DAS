package com.aashdit.districtautomationsystem.Response;

import com.google.gson.annotations.SerializedName;

public class closureTenderResultResponse {

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
}
