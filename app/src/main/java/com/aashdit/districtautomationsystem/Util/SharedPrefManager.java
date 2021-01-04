package com.aashdit.districtautomationsystem.Util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private static Context mContext;
    private static SharedPrefManager mInstance;

    public SharedPrefManager(Context context) {
        mContext = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null)
            mInstance = new SharedPrefManager(context);
        return mInstance;
    }

    public boolean getLogin(){
        SharedPreferences preferences = mContext.getSharedPreferences(Constants.PREF_APP, Context.MODE_PRIVATE);
        return preferences.getBoolean(Constants.APP_LOGIN,false);
    }

//    public String getStoreToken() {
//        SharedPreferences preferences = mContext.getSharedPreferences(Constants.PREF_APP, Context.MODE_PRIVATE);
//        return preferences.getString(STORE_TOKEN, null);
//    }

    public void setStringData(String KEY,String value){
        SharedPreferences preferences = mContext.getSharedPreferences(Constants.PREF_APP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY, value).apply();
    }

    public String getStringData(String KEY){
        SharedPreferences preferences = mContext.getSharedPreferences(Constants.PREF_APP, Context.MODE_PRIVATE);
        return preferences.getString(KEY,"");
    }

    public void setBoolData(String KEY,boolean value){
        SharedPreferences preferences = mContext.getSharedPreferences(Constants.PREF_APP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY, value).apply();
    }

    public boolean getBoolData(String KEY){
        SharedPreferences preferences = mContext.getSharedPreferences(Constants.PREF_APP, Context.MODE_PRIVATE);
        return preferences.getBoolean(KEY,false);
    }

    public void isLogin(boolean status) {
        if (status) {
            SharedPreferences preferences = mContext.getSharedPreferences(Constants.PREF_APP, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(Constants.APP_LOGIN, true).apply();
        } else {
            SharedPreferences preferences = mContext.getSharedPreferences(Constants.PREF_APP, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(Constants.APP_LOGIN, false).apply();
        }

    }

    public void clear(){
        SharedPreferences preferences =mContext.getSharedPreferences(Constants.PREF_APP,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    public void setClosureTenderId(String tenderid) {
        SharedPreferences preferences = mContext.getSharedPreferences(Constants.PREF_APP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("SETCLOSURETENDERID", tenderid).apply();
//        mSharedPreferences.edit().putString("SETCLOSURETENDERID", tenderid).apply();
    }
}
