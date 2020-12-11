package com.example.districtautomationsystem;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.districtautomationsystem.Response.ChangePasswordResponse;
import com.example.districtautomationsystem.Util.ApiClient;
import com.example.districtautomationsystem.Util.Constants;
import com.example.districtautomationsystem.Util.SharedPrefManager;
import com.example.districtautomationsystem.Util.WebApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.example.districtautomationsystem.Util.Constants.RESPONSE_BAD;
import static com.example.districtautomationsystem.Util.Constants.RESPONSE_ERROR;
import static com.example.districtautomationsystem.Util.Constants.RESPONSE_NOT_FOUND;
import static com.example.districtautomationsystem.Util.Constants.RESPONSE_OK;

@SuppressWarnings("All")
public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {
    ProgressDialog progressDialog;
    Retrofit retrofit;
    SharedPrefManager sp;
    private TextView oldpasswordTv, newpasswordTv;
    private Button submitbtn;
    private WebApi webApi;
    private String userid;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        retrofit = ApiClient.getRetrofit();
        webApi = retrofit.create(WebApi.class);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        sp = SharedPrefManager.getInstance(this);
        progressDialog = new ProgressDialog(ChangePasswordActivity.this);

        userid = sp.getStringData(Constants.USER_ID);//RegPrefManager.getInstance(ChangePasswordActivity.this).getLoginUserid();

//        findViewById(R.id.image_back).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onBackPressed();
//            }
//        });
        intialize();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private void intialize() {
        newpasswordTv = findViewById(R.id.newpasswordTv);
        oldpasswordTv = findViewById(R.id.oldpasswordTv);
        submitbtn = findViewById(R.id.submitbtn);

        submitbtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submitbtn:
                if (oldpasswordTv.getText().toString().equals("")) {
                    toastMessage("Please Enter Old Password");
                } else if (newpasswordTv.getText().toString().equals("")) {
                    toastMessage("Please Enter New Password");
                } else {
                    if (isNetworkAvailable()) {
                        getChangePassword();
                    } else {
                        toastMessage("Please Check Internet Connection");
                    }
                }
                break;
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void getChangePassword() {
        progressDialog.show();
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        Call<ChangePasswordResponse> call = webApi.getChangePasswordResponse(userid, oldpasswordTv.getText().toString(), newpasswordTv.getText().toString());
        call.enqueue(new Callback<ChangePasswordResponse>() {
            @Override
            public void onResponse(Call<ChangePasswordResponse> call, Response<ChangePasswordResponse> response) {
                progressDialog.dismiss();
                int code = response.code();
                switch (code) {
                    case RESPONSE_NOT_FOUND:
                        toastMessage("NO REORDS FOUND.");

                        break;
                    case RESPONSE_ERROR:
                        toastMessage("NO REORDS FOUND.");

                        break;
                    case RESPONSE_BAD:
                        toastMessage("NO REORDS FOUND.");

                        break;
                    case RESPONSE_OK:
                        String status = response.body().getStatus();
                        String message = "";
                        if (status.equals("SUCCESS")) {
                             message = response.body().getMessage();

                            toastMessage(message);

                            Intent intent = new Intent(ChangePasswordActivity.this, DashboardActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

                        } else {
                            message = response.body().getMessage();
                            toastMessage(message);
                        }

                        break;
                }
            }

            @Override
            public void onFailure(Call<ChangePasswordResponse> call, Throwable t) {
                progressDialog.dismiss();
                toastMessage("Failed");
            }
        });
    }

    private void toastMessage(String data) {
        Toast.makeText(getApplicationContext(), data, Toast.LENGTH_LONG).show();
    }

}
