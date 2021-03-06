package com.aashdit.districtautomationsystem.model;

import org.json.JSONObject;

public class Project {
    public String stageName;
    public String financialYearName;
    public String projectCode;
    public String schemeName;
    public String projectName;
    public String phaseName;
    public String actualProjectStatus;
    public Long projectId;

    public static Project parseProject(JSONObject object){
        Project project = new Project();
        project.stageName = object.optString("stageName");
        project.financialYearName = object.optString("financialYearName");
        project.projectCode = object.optString("projectCode");
        project.schemeName = object.optString("schemeName");
        project.projectName = object.optString("projectName");
        project.phaseName = object.optString("phaseName");
        project.actualProjectStatus = object.optString("actualProjectStatus");
        project.projectId = object.optLong("projectId");

        return project;
    }
}
