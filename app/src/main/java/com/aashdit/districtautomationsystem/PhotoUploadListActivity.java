package com.aashdit.districtautomationsystem;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aashdit.districtautomationsystem.Adapter.PhotoUploadAdapter;
import com.aashdit.districtautomationsystem.Adapter.PhotoUploadClosureAdapter;
import com.aashdit.districtautomationsystem.Response.GetProjectClosureUploadedTender;
import com.aashdit.districtautomationsystem.Response.GetProjectIntiationUploadedTender;
import com.aashdit.districtautomationsystem.Response.ProjectClosureUploadedTenderlist;
import com.aashdit.districtautomationsystem.Response.ProjectIntiationUploadedTenderList;
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
public class PhotoUploadListActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView noRecordTextView;
    ProgressDialog progressDialog;
    Retrofit retrofit;
    private ArrayList<ProjectIntiationUploadedTenderList> photoLists;
    private ArrayList<ProjectClosureUploadedTenderlist> photoListsUploaded;
    private PhotoUploadAdapter adapter;
    private PhotoUploadClosureAdapter adapterCloser;
    private WebApi webApi;
    private SharedPrefManager sp;
    private String startdate, enddate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_upload_list);
        retrofit = ApiClient.getRetrofit();
        webApi = retrofit.create(WebApi.class);
        sp = SharedPrefManager.getInstance(this);
        progressDialog = new ProgressDialog(PhotoUploadListActivity.this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back_button);
        //  setTitle("My Profile");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(PhotoUploadListActivity.this, DashboardActivity.class));
//                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
//                finish();

                onBackPressed();
            }
        });

        String type = RegPrefManager.getInstance(PhotoUploadListActivity.this).getProjectIntiationOrCloser();
        if (type.equals("Initiation")) {
            startdate = RegPrefManager.getInstance(PhotoUploadListActivity.this).getIntiationProjectPhotoUploadedStartDate();
            enddate = RegPrefManager.getInstance(PhotoUploadListActivity.this).getIntiationProjectPhotoUploadedEndDate();
        } else {
            startdate = RegPrefManager.getInstance(PhotoUploadListActivity.this).getProjectCloserUploadedListStartDate();
            enddate = RegPrefManager.getInstance(PhotoUploadListActivity.this).getProjectCloserUploadedListEndDate();
        }


        //display all patient list
        noRecordTextView = findViewById(R.id.noRecordTextView);
        recyclerView = (RecyclerView) findViewById(R.id.mRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        photoLists = new ArrayList<>();

        if (isNetworkAvailable()) {
            if (type.equals("Initiation")) {
                getIntiationTenderUploadedRecord();
            } else {
                getCloserTenderUploadedRecord();

            }
        } else {
            toastMessage("Please Check Internet Connection");
        }


        recyclerView.setVisibility(View.VISIBLE);
        adapter = new PhotoUploadAdapter(PhotoUploadListActivity.this, photoLists);

        //setting adapter to recyclerview
        recyclerView.setAdapter(adapter);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void toastMessage(String data) {
        Toast.makeText(getApplicationContext(), data, Toast.LENGTH_LONG).show();
    }

    private void getIntiationTenderUploadedRecord() {
        progressDialog.show();
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        Call<GetProjectIntiationUploadedTender> call = webApi.getProjectIntiationUploadedTenderResponse(sp.getStringData(Constants.USER_ID), startdate, enddate);
        call.enqueue(new Callback<GetProjectIntiationUploadedTender>() {
            @Override
            public void onResponse(Call<GetProjectIntiationUploadedTender> call, Response<GetProjectIntiationUploadedTender> response) {
                progressDialog.dismiss();
                int code = response.code();
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
                        String status = response.body().getStatus();
                        if (status.equals("SUCCESS")) {
                            recyclerView.setVisibility(View.VISIBLE);
                            noRecordTextView.setVisibility(View.GONE);
                            photoLists = response.body().getResult();
                            if (photoLists.size() != 0) {

                                adapter = new PhotoUploadAdapter(PhotoUploadListActivity.this, photoLists);

                                //setting adapter to recyclerview
                                recyclerView.setAdapter(adapter);
                            } else {
                                noRecordTextView.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            }


                        } else {
                            toastMessage("No Records Found.");
                            noRecordTextView.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }

                        break;
                }
            }

            @Override
            public void onFailure(Call<GetProjectIntiationUploadedTender> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(PhotoUploadListActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getCloserTenderUploadedRecord() {
        progressDialog.show();
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        Call<GetProjectClosureUploadedTender> call = webApi.getProjectClosureUploadedTenderlistResponse(sp.getStringData(Constants.USER_ID), startdate, enddate);
        call.enqueue(new Callback<GetProjectClosureUploadedTender>() {
            @Override
            public void onResponse(Call<GetProjectClosureUploadedTender> call, Response<GetProjectClosureUploadedTender> response) {
                progressDialog.dismiss();
                int code = response.code();
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
                        String status = response.body().getStatus();
                        if (status.equals("SUCCESS")) {
                            recyclerView.setVisibility(View.VISIBLE);
                            noRecordTextView.setVisibility(View.GONE);
                            photoListsUploaded = response.body().getResult();
                            if (photoListsUploaded.size() != 0) {

                                adapterCloser = new PhotoUploadClosureAdapter(PhotoUploadListActivity.this, photoListsUploaded);

                                //setting adapter to recyclerview
                                recyclerView.setAdapter(adapterCloser);
                            } else {
                                noRecordTextView.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            }


                        } else {
                            toastMessage("No Records Found.");
                            noRecordTextView.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }

                        break;
                }
            }

            @Override
            public void onFailure(Call<GetProjectClosureUploadedTender> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(PhotoUploadListActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
