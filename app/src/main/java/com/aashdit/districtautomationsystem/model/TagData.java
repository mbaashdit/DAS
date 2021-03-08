package com.aashdit.districtautomationsystem.model;

import org.json.JSONObject;

public class TagData {
    public String address;
    public String latitude;
    public String longitude;
    public String createdDateWithTime;
    public Long projectGeoTaggingId;

    public static TagData parseTagData(JSONObject object){
        TagData tagData = new TagData();
        tagData.address = object.optString("address");
        tagData.latitude = object.optString("latitude");
        tagData.longitude = object.optString("longitude");
        tagData.createdDateWithTime = object.optString("createdDateWithTime");
        tagData.projectGeoTaggingId = object.optLong("projectGeoTaggingId");
        return tagData;
    }
}
