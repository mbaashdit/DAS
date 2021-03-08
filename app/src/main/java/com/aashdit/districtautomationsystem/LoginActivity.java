package com.aashdit.districtautomationsystem;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.aashdit.districtautomationsystem.Util.ApiClient;
import com.aashdit.districtautomationsystem.Util.Constants;
import com.aashdit.districtautomationsystem.Util.SharedPrefManager;
import com.aashdit.districtautomationsystem.Util.WebApi;
import com.aashdit.districtautomationsystem.activities.ProjectListActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

@SuppressWarnings("All")
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    ProgressDialog progressDialog;
    Retrofit retrofit;
    private Button loginbtn;
    private TextView usernameTv, passwordTv;
    private WebApi webApi;
    private boolean flag = false;
    private SharedPrefManager sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        retrofit = ApiClient.getRetrofit();
        webApi = retrofit.create(WebApi.class);

        sp = SharedPrefManager.getInstance(this);
        progressDialog = new ProgressDialog(LoginActivity.this);
        loginbtn = findViewById(R.id.loginbtn);
        loginbtn.setOnClickListener(this);

        usernameTv = findViewById(R.id.usernameTv);
        passwordTv = findViewById(R.id.passwordTv);

    }
    public static Bitmap drawableToBitmap (Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginbtn:


                if (usernameTv.getText().toString().equals("")) {
                    Toast.makeText(LoginActivity.this, "Enter user name", Toast.LENGTH_SHORT).show();
                } else if (passwordTv.getText().toString().equals("")) {
                    Toast.makeText(LoginActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
                } else {
                    if (flag == false) {
                        showalert("Please Allow Location");
                        //   checkPermission();
                    } else {
                        if (isNetworkAvailable()) {
                            getLoginResponse();
                        } else {
                            toastMessage("Please Check Internet Connection");
                        }

                    }
                }

                break;
        }
    }


    @Override
    public void onBackPressed() {
        //this is only needed if you have specific things
        //that you want to do when the user presses the back button.
        /* your specific things...*/
        super.onBackPressed();
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);

    }

    private void getLoginResponse() {
        progressDialog.show();
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        String userName = usernameTv.getText().toString().trim();
        String pas = passwordTv.getText().toString().trim();

        Call<String> call = webApi.getLoginResponse(userName, pas);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    try {

                        Log.i("TAG", "onResponse:::::::::::::::::::::::::::::::::::::::::::::::: "+response.body());

                        JSONObject loginObj = new JSONObject(response.body());
                        String status = loginObj.optString("status");
                        if (status.equals("SUCCESS")) {
                            Toast.makeText(LoginActivity.this, loginObj.optString("message"), Toast.LENGTH_SHORT).show();
                            JSONObject userResult = loginObj.optJSONObject("userResult");
                            Long userId = userResult.optLong("userId");
                            String userName = userResult.optString("userName");
                            String name = userResult.optString("name");
                            String emailId = userResult.optString("emailId");
                            String mobileNumber = userResult.optString("mobileNumber");
                            String image = userResult.optString("image");


                            JSONObject roleResult = loginObj.optJSONObject("roleResult");
                            String roleName = roleResult.optString("roleName");

                            Long panchayatId = userResult.optLong("panchayatId");

//                            RegPrefManager.getInstance(LoginActivity.this).setLoginResponse(name, userId, emailId, mobileNumber, pas, roleName);

                            sp.setLongData(Constants.USER_ID,userId);
                            sp.setStringData(Constants.USER_NAME,userName);
                            sp.setStringData(Constants.USER_PASSWORD,pas);
                            sp.setStringData(Constants.NAME,name);
                            sp.setStringData(Constants.EMAIL,emailId);
                            sp.setStringData(Constants.MOBILE,mobileNumber);
                            sp.setStringData(Constants.IMAGE,image);
                            sp.setStringData(Constants.ROLE_NAME,roleName);
                            sp.setLongData(Constants.GP_ID,panchayatId);
                            sp.setBoolData(Constants.APP_LOGIN,true);


                            if (userName.startsWith("io_")) {
                                Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

                                finish();
                            }else{
                                Intent intent = new Intent(LoginActivity.this, ProjectListActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

                                finish();
                            }
                        }else{
//                            Intent intent = new Intent(LoginActivity.this, ProjectListActivity.class);
//                            startActivity(intent);
//                            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
//
//                            finish();
                            Toast.makeText(LoginActivity.this, loginObj.optString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
//        call.enqueue(new Callback<LoginResponse>() {
//            @Override
//            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
//                progressDialog.dismiss();
//
//                if (response.isSuccessful() && response.body() != null) {
//                    if (response.body().getStatus().equals("SUCCESS")) {
//
//                        String name = response.body().getResult().getName();
//
//
//                        RegPrefManager.getInstance(LoginActivity.this).setLoginResponse(response.body().getResult().getName(),
//                                response.body().getResult().getUserId(), response.body().getResult().getEmailid(), response.body().getResult().getPhoneNo(),pas,userName);
//
//                        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
//                        startActivity(intent);
//                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
//
//                        finish();
//
//                    } else {
//                        Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
//                    }
//                }else {
//                    Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
//                }
//
////                int code=response.code();
////                switch (code) {
////                    case RESPONSE_NOT_FOUND:
////                        Toast.makeText(getApplicationContext(), "NO REORDS FOUND.", Toast.LENGTH_SHORT).show();
////
////                        break;
////                    case RESPONSE_ERROR:
////                        Toast.makeText(getApplicationContext(), "NO REORDS FOUND.", Toast.LENGTH_LONG).show();
////
////                        break;
////                    case RESPONSE_BAD:
////                        Toast.makeText(getApplicationContext(), "NO REORDS FOUND.", Toast.LENGTH_LONG).show();
////
////                        break;
////                    case RESPONSE_OK:
////                        String status=response.body().getStatus();
////                        if(status.equals("SUCCESS")){
////                        String name=response.body().getResult().getName();
////
////
////                            RegPrefManager.getInstance(LoginActivity.this).setLoginResponse(response.body().getResult().getName(),
////                                    response.body().getResult().getUserId(),response.body().getResult().getEmailid(),response.body().getResult().getPhoneNo());
////
////                             Intent intent = new Intent(LoginActivity.this,DashboardActivity.class);
////                              startActivity(intent);
////                            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
////
////                        }else {
////                            Toast.makeText(LoginActivity.this,"Network is Slow. Please Check After Sometimes.",Toast.LENGTH_SHORT).show();
////                        }
////
////                        break;
////                }
//            }
//
//            @Override
//            public void onFailure(Call<LoginResponse> call, Throwable t) {
//                progressDialog.dismiss();
//                Toast.makeText(LoginActivity.this, "Failed", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    //check permissions

    private boolean checkAndRequestPermissions() {

        try {

            int permissionLOCATION = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int storagePermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            int permissionSTORAGEWRITE = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int permissionCAMERA = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA);

            List<String> listPermissionsNeeded = new ArrayList<>();
            if (storagePermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            } else {
                flag = true;
            }
            if (permissionLOCATION != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
            } else {
                flag = true;
            }
            if (permissionSTORAGEWRITE != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            } else {
                flag = true;
            }
            if (permissionCAMERA != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.CAMERA);
            } else {
                flag = true;
            }
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1);
                return false;
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return true;

    }

    private void showalert(String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
        dialog.setCancelable(false);
        dialog.setMessage(message);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (Build.VERSION.SDK_INT < 23) {
                    //Do not need to check the permission
                    flag = true;
                } else {
                    try {


                        if (checkAndRequestPermissions()) {
                            //If you have already permitted the permission
                        }
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Error:" + e, Toast.LENGTH_LONG).show();
                    }
                }

                dialog.dismiss();
            }
        });

        final AlertDialog alert = dialog.create();
        alert.show();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onResume() {
        super.onResume();
        // put your code here...

        if (Build.VERSION.SDK_INT < 23) {
            //Do not need to check the permission
            flag = true;
        } else {
            try {
                if (checkAndRequestPermissions()) {
                    //If you have already permitted the permission
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Error:" + e, Toast.LENGTH_LONG).show();
            }
        }

        // }
        /*else {

        }*/
    }

    private void toastMessage(String data) {
        Toast.makeText(getApplicationContext(), data, Toast.LENGTH_LONG).show();
    }


}
