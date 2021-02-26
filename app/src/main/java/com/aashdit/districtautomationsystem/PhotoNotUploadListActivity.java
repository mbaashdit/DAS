package com.aashdit.districtautomationsystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.aashdit.districtautomationsystem.Adapter.PhotoNotUploadAdapter;
import com.aashdit.districtautomationsystem.Adapter.PhotoNotUploadClosureAdapter;
import com.aashdit.districtautomationsystem.Response.GetProjectClosureTenderlist;
import com.aashdit.districtautomationsystem.Response.GetProjectIntiationResponse;
import com.aashdit.districtautomationsystem.Response.closureTenderResultResponse;
import com.aashdit.districtautomationsystem.Response.resultResponse;
import com.aashdit.districtautomationsystem.Util.ApiClient;
import com.aashdit.districtautomationsystem.Util.Constants;
import com.aashdit.districtautomationsystem.Util.RegPrefManager;
import com.aashdit.districtautomationsystem.Util.SharedPrefManager;
import com.aashdit.districtautomationsystem.Util.WebApi;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.aashdit.districtautomationsystem.Util.Constants.RESPONSE_BAD;
import static com.aashdit.districtautomationsystem.Util.Constants.RESPONSE_ERROR;
import static com.aashdit.districtautomationsystem.Util.Constants.RESPONSE_NOT_FOUND;
import static com.aashdit.districtautomationsystem.Util.Constants.RESPONSE_OK;
@SuppressWarnings("All")
public class PhotoNotUploadListActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView noRecordTextView;
    private ArrayList<resultResponse> photoLists;
    private ArrayList<closureTenderResultResponse> closurephotoLists;
    private PhotoNotUploadAdapter adapter;
    private PhotoNotUploadClosureAdapter adapterCloser;
    ProgressDialog progressDialog;
    Retrofit retrofit;
    private WebApi webApi;

    private String startdate,enddate;
    private SharedPrefManager sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_not_upload_list);
        retrofit = ApiClient.getRetrofit();
        webApi = retrofit.create(WebApi.class);

        sp  = SharedPrefManager.getInstance(this);
        progressDialog = new ProgressDialog(PhotoNotUploadListActivity.this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back_button);
        //  setTitle("My Profile");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(PhotoNotUploadListActivity.this, DashboardActivity.class));
//                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
//                finish();

                onBackPressed();
            }
        });

        String type=RegPrefManager.getInstance(PhotoNotUploadListActivity.this).getProjectIntiationOrCloser();
        if(type .equals("Initiation")) {
            startdate = RegPrefManager.getInstance(PhotoNotUploadListActivity.this).getIntiationProjectStartDate();
            enddate = RegPrefManager.getInstance(PhotoNotUploadListActivity.this).getIntiationProjectEndDate();
        }else {
            startdate = RegPrefManager.getInstance(PhotoNotUploadListActivity.this).getProjectCloserNotUploadedListStartDate();
            enddate = RegPrefManager.getInstance(PhotoNotUploadListActivity.this).getProjectCloserNotUploadedListEndDate();
        }


        //display all patient list
        noRecordTextView=findViewById(R.id.noRecordTextView);
        recyclerView = (RecyclerView) findViewById(R.id.mRecyclerView);
//        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        photoLists = new ArrayList<>();
        closurephotoLists = new ArrayList<>();

        if(isNetworkAvailable()){
            if(type .equals("Initiation")) {
                getIntiationTenderRecord();
            }else {
                getCloserTenderRecord();
            }
        }else {
            toastMessage("Please Check Internet Connection");
        }

    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void toastMessage(String  data){
        Toast.makeText(getApplicationContext(),data,Toast.LENGTH_LONG).show();
    }


    private void getIntiationTenderRecord(){
        progressDialog.show();
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        Call<GetProjectIntiationResponse> call=webApi.getProjectIntiationResponse(String.valueOf(sp.getLongData(Constants.USER_ID)),startdate,enddate);
        call.enqueue(new Callback<GetProjectIntiationResponse>() {
            @Override
            public void onResponse(Call<GetProjectIntiationResponse> call, Response<GetProjectIntiationResponse> response) {
                progressDialog.dismiss();
                int code=response.code();
                switch (code) {
                    case RESPONSE_NOT_FOUND:

                        Toast.makeText(getApplicationContext(), "NO REORDS FOUND.", Toast.LENGTH_SHORT).show();
                        noRecordTextView.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        break;
                    case RESPONSE_ERROR:
                        Toast.makeText(getApplicationContext(), "NO REORDS FOUND.", Toast.LENGTH_LONG).show();
                        noRecordTextView.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        break;
                    case RESPONSE_BAD:
                        Toast.makeText(getApplicationContext(), "NO REORDS FOUND.", Toast.LENGTH_LONG).show();
                        noRecordTextView.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        break;
                    case RESPONSE_OK:
                        String status=response.body().getStatus();
                        if(status.equals("SUCCESS")){
                            recyclerView.setVisibility(View.VISIBLE);
                            noRecordTextView.setVisibility(View.GONE);
                            photoLists=response.body().getResult();
                            if(photoLists.size()!=0){

                                adapter = new PhotoNotUploadAdapter(PhotoNotUploadListActivity.this, photoLists);

                                //setting adapter to recyclerview
                                recyclerView.setAdapter(adapter);
                            }else {
                                noRecordTextView.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            }
                        }else {
                            toastMessage("No Records Found.");
                            noRecordTextView.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }

                        break;
                }
            }

            @Override
            public void onFailure(Call<GetProjectIntiationResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(PhotoNotUploadListActivity.this,"Failed",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getCloserTenderRecord(){
        progressDialog.show();
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        Call<GetProjectClosureTenderlist> call = webApi.getProjectClosureTenderlistResponse(String.valueOf(sp.getLongData(Constants.USER_ID)),startdate,enddate);
        call.enqueue(new Callback<GetProjectClosureTenderlist>() {
            @Override
            public void onResponse(Call<GetProjectClosureTenderlist> call, Response<GetProjectClosureTenderlist> response) {
                progressDialog.dismiss();
                int code=response.code();
                switch (code) {
                    case RESPONSE_NOT_FOUND:

                        Toast.makeText(getApplicationContext(), "NO REORDS FOUND.", Toast.LENGTH_SHORT).show();
                        noRecordTextView.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        break;
                    case RESPONSE_ERROR:
                        Toast.makeText(getApplicationContext(), "NO REORDS FOUND.", Toast.LENGTH_LONG).show();
                        noRecordTextView.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        break;
                    case RESPONSE_BAD:
                        Toast.makeText(getApplicationContext(), "NO REORDS FOUND.", Toast.LENGTH_LONG).show();
                        noRecordTextView.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        break;
                    case RESPONSE_OK:
                        String status=response.body().getStatus();
                        if(status.equals("SUCCESS")){
                            recyclerView.setVisibility(View.VISIBLE);
                            noRecordTextView.setVisibility(View.GONE);
                            closurephotoLists=response.body().getResult();
                            if(closurephotoLists.size()!=0){

                                adapterCloser = new PhotoNotUploadClosureAdapter(PhotoNotUploadListActivity.this, closurephotoLists);

                                //setting adapter to recyclerview
                                recyclerView.setAdapter(adapterCloser);
                                noRecordTextView.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            }else {
                                noRecordTextView.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            }

                        }else {
                            toastMessage("No Records Found.");
                            noRecordTextView.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }

                        break;
                }
            }

            @Override
            public void onFailure(Call<GetProjectClosureTenderlist> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(PhotoNotUploadListActivity.this,"Failed",Toast.LENGTH_SHORT).show();
            }
        });
    }


}
