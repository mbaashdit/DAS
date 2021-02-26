package com.aashdit.districtautomationsystem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.aashdit.districtautomationsystem.Util.ApiClient;
import com.aashdit.districtautomationsystem.Util.Constants;
import com.aashdit.districtautomationsystem.Util.SharedPrefManager;
import com.aashdit.districtautomationsystem.Util.WebApi;
import com.aashdit.districtautomationsystem.activities.ProjectListActivity;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SplashActivity extends AppCompatActivity {
    private int SPLASH_TIME = 1000;
    private WebApi webApi;
    Retrofit retrofit;
    private SharedPrefManager sp;
    private boolean isLogin ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sp = SharedPrefManager.getInstance(this);

        isLogin = sp.getBoolData(Constants.APP_LOGIN);
        retrofit = ApiClient.getRetrofit();
        webApi = retrofit.create(WebApi.class);
        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(SPLASH_TIME);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                    if (isLogin){
                        String password = sp.getStringData(Constants.USER_PASSWORD);
                        String userName = sp.getStringData(Constants.USER_NAME);
                        getLoginResponse(userName,password);
                    }
                    else {
                        startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        finish();
                    }

//                 String  userid = sp.getStringData(Constants.USER_ID);
//                    Log.d("DAS",userid+"");
//                    if(userid!=null){
//                        String password = sp.getStringData(Constants.USER_PASSWORD);
//                        String userName = sp.getStringData(Constants.USER_NAME);
//                        getLoginResponse(userName,password);
//                    }

                }
            }
        };
        timer.start();
    }




    private void getLoginResponse(String userName,String password) {


        Call<String > call = webApi.getLoginResponse(userName, password);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONObject loginObj = new JSONObject(response.body());
                        String status = loginObj.optString("status");
                        if (status.equals("SUCCESS")) {
                            JSONObject userResult = loginObj.optJSONObject("userResult");
                            Long userId = userResult.optLong("userId");
                            String userName = userResult.optString("userName");
                            String name = userResult.optString("name");
                            String emailId = userResult.optString("emailId");
                            String mobileNumber = userResult.optString("mobileNumber");
                            String image = userResult.optString("image");
                            Long panchayatId = userResult.optLong("panchayatId");

                            JSONObject roleResult = loginObj.optJSONObject("roleResult");
                            String roleName = roleResult.optString("roleName");

//                            RegPrefManager.getInstance(SplashActivity.this).setLoginResponse(name, userId, emailId, mobileNumber, password, roleName);
                            sp.setLongData(Constants.USER_ID, userId);
                            sp.setStringData(Constants.USER_NAME, userName);
                            sp.setStringData(Constants.USER_PASSWORD, password);
                            sp.setStringData(Constants.NAME, name);
                            sp.setStringData(Constants.EMAIL, emailId);
                            sp.setStringData(Constants.MOBILE, mobileNumber);
                            sp.setStringData(Constants.IMAGE, image);
                            sp.setStringData(Constants.ROLE_NAME, roleName);
                            sp.setLongData(Constants.GP_ID, panchayatId);

                            if (userName.startsWith("io_")) {
                                Intent intent = new Intent(SplashActivity.this, DashboardActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

                                finish();
                            } else {
                                Intent intent = new Intent(SplashActivity.this, ProjectListActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

                                finish();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("TAG", "onFailure: " + t.getLocalizedMessage());
            }
        });
    }
}
