package com.aashdit.districtautomationsystem.model;

import org.json.JSONObject;

public class Stage {
    public String stageName;
    public String stageCode;
    public boolean imageUploaded;
    public Long stageId;

    public static Stage parseStageResponse(JSONObject object){
        Stage stage = new Stage();
        stage.stageName = object.optString("stageName");
        stage.stageCode = object.optString("stageCode");
        stage.imageUploaded = object.optBoolean("imageUploaded");
        stage.stageId = object.optLong("stageId");
        return stage;
    }
}
