package com.example.districtautomationsystem.Util;


import android.content.Context;
import android.content.SharedPreferences;


/**
 * Created by Raju Satheesh on 10/18/2016.
 */
public class RegPrefManager {

    private static RegPrefManager mPrefManager;
    public Context mContext;
    private SharedPreferences mSharedPreferences;


    private RegPrefManager(Context context) {
        this.mContext = context;
        mSharedPreferences = context.
                getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
    }

    public static RegPrefManager getInstance(Context context) {
        if (mPrefManager == null) {
            mPrefManager = new RegPrefManager(context);
        }
        return mPrefManager;
    }


    public void setLoginResponse(String Username,String name, String userid, String emailid, String phoneNo, String password, String roleName) {
        mSharedPreferences.edit().putString("SETLOGINNAME", name).apply();
        mSharedPreferences.edit().putString("UserName", Username).apply();
        mSharedPreferences.edit().putString("SETLOGINUSERID", userid).apply();
        mSharedPreferences.edit().putString("SETLOGINEMAILID", emailid).apply();
        mSharedPreferences.edit().putString("SETLOGINPHONENO", phoneNo).apply();
        mSharedPreferences.edit().putString("SETLOGINPASSWORD", password).apply();
        mSharedPreferences.edit().putString("SETROLENAME", roleName).apply();

    }

    public String getLoginName() {
        return mSharedPreferences.getString("UserName", null);
    }

    public String getLoginUserid() {
        return mSharedPreferences.getString("SETLOGINUSERID", null);
    }

    public String getLoginPassword() {
        return mSharedPreferences.getString("SETLOGINPASSWORD", null);
    }

    public String getLoginId() {
        return mSharedPreferences.getString("SETLOGINID", null);
    }

    public String getRoleName() {
        return mSharedPreferences.getString("SETROLENAME", null);
    }

    public String getLoginEmailId() {
        return mSharedPreferences.getString("SETLOGINEMAILID", null);
    }

    public String getLoginPhoneNo() {
        return mSharedPreferences.getString("SETLOGINPHONENO", null);
    }

    public void ClearCartData() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.remove("CartID");
        editor.commit();
    }

    public void setIntiationProjectList(String statdate, String enddate) {
        mSharedPreferences.edit().putString("SETINTIATIONPHOTONOTUPLOAD_STARTDATE", statdate).apply();
        mSharedPreferences.edit().putString("SETINTIATIONPHOTONOTUPLOAD_ENDDATE", enddate).apply();

    }

    public String getIntiationProjectStartDate() {
        return mSharedPreferences.getString("SETINTIATIONPHOTONOTUPLOAD_STARTDATE", null);
    }

    public String getIntiationProjectEndDate() {
        return mSharedPreferences.getString("SETINTIATIONPHOTONOTUPLOAD_ENDDATE", null);
    }

    public void setIntiationProjectUploadedList(String statdate, String enddate) {
        mSharedPreferences.edit().putString("SETINTIATIONPHOTOUPLOAD_STARTDATE", statdate).apply();
        mSharedPreferences.edit().putString("SETINTIATIONPHOTOUPLOAD_ENDDATE", enddate).apply();

    }

    public String getIntiationProjectPhotoUploadedStartDate() {
        return mSharedPreferences.getString("SETINTIATIONPHOTOUPLOAD_STARTDATE", null);
    }

    public String getIntiationProjectPhotoUploadedEndDate() {
        return mSharedPreferences.getString("SETINTIATIONPHOTOUPLOAD_ENDDATE", null);
    }

    public void setProjectCloserNotUploadedList(String statdate, String enddate) {
        mSharedPreferences.edit().putString("SETPROJECTCLOSURENOTUPLOADED_STARTDATE", statdate).apply();
        mSharedPreferences.edit().putString("SETPROJECTCLOSURENOTUPLOADED_ENDDATE", enddate).apply();

    }

    public String getProjectCloserNotUploadedListStartDate() {
        return mSharedPreferences.getString("SETPROJECTCLOSURENOTUPLOADED_STARTDATE", null);
    }

    public String getProjectCloserNotUploadedListEndDate() {
        return mSharedPreferences.getString("SETPROJECTCLOSURENOTUPLOADED_ENDDATE", null);
    }

    public void setProjectCloserUploadedList(String statdate, String enddate) {
        mSharedPreferences.edit().putString("SETPROJECTCLOSUREUPLOADED_STARTDATE", statdate).apply();
        mSharedPreferences.edit().putString("SETPROJECTCLOSUREUPLOADED_ENDDATE", enddate).apply();

    }

    public String getProjectCloserUploadedListStartDate() {
        return mSharedPreferences.getString("SETPROJECTCLOSUREUPLOADED_STARTDATE", null);
    }

    public String getProjectCloserUploadedListEndDate() {
        return mSharedPreferences.getString("SETPROJECTCLOSUREUPLOADED_ENDDATE", null);
    }

    public String getProjectIntiationOrCloser() {
        return mSharedPreferences.getString("SETPROJECTINTIATIONORCLOSER", null);
    }

    public void setProjectIntiationOrCloser(String TYPE) {
        mSharedPreferences.edit().putString("SETPROJECTINTIATIONORCLOSER", TYPE).apply();


    }

    public String getInitialTenderId() {
        return mSharedPreferences.getString("SETINTIATIONTENDERID", null);
    }

    public void setInitialTenderId(String tenderid) {
        mSharedPreferences.edit().putString("SETINTIATIONTENDERID", tenderid).apply();
    }

    public String getClosureTenderId() {
        return mSharedPreferences.getString("SETCLOSURETENDERID", null);
    }

    public void setClosureTenderId(String tenderid) {
        mSharedPreferences.edit().putString("SETCLOSURETENDERID", tenderid).apply();
    }

    public void clearData() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.remove("SETLOGINUSERID");
        editor.clear().commit();
        editor.apply();

    }

}

