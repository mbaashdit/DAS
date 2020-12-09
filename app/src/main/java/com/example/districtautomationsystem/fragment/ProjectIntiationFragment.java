package com.example.districtautomationsystem.fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.districtautomationsystem.Adapter.PhotoNotUploadAdapter;
import com.example.districtautomationsystem.Adapter.PhotoUploadAdapter;
import com.example.districtautomationsystem.InitiationPhotoUploadActivity;
import com.example.districtautomationsystem.R;
import com.example.districtautomationsystem.Response.GetProjectIntiationResponse;
import com.example.districtautomationsystem.Response.GetProjectIntiationUploadedTender;
import com.example.districtautomationsystem.Response.ProjectIntiationUploadedTenderList;
import com.example.districtautomationsystem.Response.resultResponse;
import com.example.districtautomationsystem.Util.ApiClient;
import com.example.districtautomationsystem.Util.Constants;
import com.example.districtautomationsystem.Util.SharedPrefManager;
import com.example.districtautomationsystem.Util.WebApi;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.app.Activity.RESULT_OK;
import static com.example.districtautomationsystem.Util.Constants.RESPONSE_BAD;
import static com.example.districtautomationsystem.Util.Constants.RESPONSE_ERROR;
import static com.example.districtautomationsystem.Util.Constants.RESPONSE_NOT_FOUND;
import static com.example.districtautomationsystem.Util.Constants.RESPONSE_OK;

public class ProjectIntiationFragment extends Fragment implements PhotoNotUploadAdapter.NotUploadedListener {

    Calendar dateSelected = Calendar.getInstance();
    RecyclerView recyclerView;
    TextView noRecordTextView;
    ProgressBar progressDialog;
    Retrofit retrofit;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private RelativeLayout mRlPhotoNotUploaded, mRlPhotoUploaded;
    private ImageView mIvNoUpload, mIvUpload;
    private TextView mTvNotUploaded, mTvUploaded;
    private TextView mTvFromDate, mTvToDate;

    private int mYear, mMonth, mDay, mHour, mMinute;
    private DatePickerDialog datePickerDialog;
    private SharedPrefManager sp;
    private PhotoNotUploadAdapter adapter;
    private PhotoUploadAdapter Uploadedadapter;
    private ArrayList<resultResponse> photoLists;
    private ArrayList<ProjectIntiationUploadedTenderList> uploadedPhotoLists;
    private WebApi webApi;
    private String startdate, enddate;
    private ImageView mIvFromDate, mIvToDate, mIvSearch;

    private String currentSearch;

    public ProjectIntiationFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_project_initiation_2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        retrofit = ApiClient.getRetrofit();
        webApi = retrofit.create(WebApi.class);

        photoLists = new ArrayList<>();
        uploadedPhotoLists = new ArrayList<>();
        sp = SharedPrefManager.getInstance(getActivity());
        recyclerView = view.findViewById(R.id.rv_project_initiation);
//        recyclerView = (RecyclerView) view.findViewById(R.id.mRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        recyclerView.addItemDecoration(new
//                DividerItemDecoration(getActivity(),
//                DividerItemDecoration.VERTICAL));
        noRecordTextView = view.findViewById(R.id.tv_no_initiation_record);
        progressDialog = view.findViewById(R.id.progress_bar);
        progressDialog.setVisibility(View.GONE);
        mRlPhotoNotUploaded = view.findViewById(R.id.rl_photo_not_uploaded);
        mRlPhotoUploaded = view.findViewById(R.id.rl_photo_uploaded);
        mIvNoUpload = view.findViewById(R.id.iv_no_upload);
        mIvUpload = view.findViewById(R.id.iv_upload);
        mIvSearch = view.findViewById(R.id.iv_search);
        mTvNotUploaded = view.findViewById(R.id.tv_not_upload);
        mTvUploaded = view.findViewById(R.id.tv_uploaded);

        mTvFromDate = view.findViewById(R.id.tv_from_date);
        mTvToDate = view.findViewById(R.id.tv_to_date);

        mIvFromDate = view.findViewById(R.id.iv_from_calender);
        mIvToDate = view.findViewById(R.id.iv_to_calender);

        mRlPhotoNotUploaded.setBackgroundResource(R.drawable.selected_btn_bg);
        mRlPhotoUploaded.setBackgroundResource(R.drawable.unselected_btn_bg);

        mIvNoUpload.setColorFilter(ContextCompat.getColor(getActivity(), R.color.mdtp_white), android.graphics.PorterDuff.Mode.SRC_IN);
        mIvUpload.setColorFilter(ContextCompat.getColor(getActivity(), R.color.mdtp_transparent_black), android.graphics.PorterDuff.Mode.SRC_IN);

        mTvNotUploaded.setTextColor(getResources().getColor(R.color.mdtp_white));

        currentSearch = "NOT_UPLOADED";

        mRlPhotoNotUploaded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRlPhotoNotUploaded.setBackgroundResource(R.drawable.selected_btn_bg);
                mRlPhotoUploaded.setBackgroundResource(R.drawable.unselected_btn_bg);
                mIvNoUpload.setColorFilter(ContextCompat.getColor(getActivity(), R.color.mdtp_white), android.graphics.PorterDuff.Mode.SRC_IN);
                mIvUpload.setColorFilter(ContextCompat.getColor(getActivity(), R.color.mdtp_transparent_black), android.graphics.PorterDuff.Mode.SRC_IN);
                mTvNotUploaded.setTextColor(getResources().getColor(R.color.mdtp_white));
                mTvUploaded.setTextColor(getResources().getColor(R.color.mdtp_transparent_black));

                currentSearch = "NOT_UPLOADED";

                if (isNetworkAvailable()) {
                    getIntiationTenderRecord();
                } else {
                    Toast.makeText(getActivity(), "Check Internet Connection", Toast.LENGTH_SHORT).show();
                }

            }
        });
        mRlPhotoUploaded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRlPhotoUploaded.setBackgroundResource(R.drawable.selected_btn_bg);
                mRlPhotoNotUploaded.setBackgroundResource(R.drawable.unselected_btn_bg);
                mTvNotUploaded.setTextColor(getResources().getColor(R.color.mdtp_transparent_black));
                mIvNoUpload.setColorFilter(ContextCompat.getColor(getActivity(), R.color.mdtp_transparent_black), android.graphics.PorterDuff.Mode.SRC_IN);

                mIvUpload.setColorFilter(ContextCompat.getColor(getActivity(), R.color.mdtp_white), android.graphics.PorterDuff.Mode.SRC_IN);
                mTvUploaded.setTextColor(getResources().getColor(R.color.mdtp_white));

                currentSearch = "UPLOADED";

                if (isNetworkAvailable()) {
                    getIntiationTenderUploadedRecord();
                } else {
                    Toast.makeText(getActivity(), "Check Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });


        mIvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkForApiCall();
            }
        });
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c);
        mTvToDate.setText(formattedDate);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -2);  // two month back
        SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate1 = df1.format(cal.getTime());

        mTvFromDate.setText(formattedDate1);
        startdate = mTvFromDate.getText().toString().trim();


        Calendar currentcal = Calendar.getInstance();
        SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate2 = df2.format(currentcal.getTime());
        mTvToDate.setText(formattedDate2);
        enddate = mTvToDate.getText().toString();

        mIvFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDateTimeField();
            }
        });

        mIvToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTodayDateTimeField();
            }
        });
    }


    private void setDateTimeField() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        mTvFromDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                        checkForApiCall();
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void setTodayDateTimeField() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        mTvToDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                        checkForApiCall();
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void checkForApiCall() {

        startdate = mTvFromDate.getText().toString().trim();
        enddate = mTvToDate.getText().toString().trim();
        if (currentSearch.equals("NOT_UPLOADED")) {
            //NOT_UPLOADED
            if (isNetworkAvailable()) {
                getIntiationTenderRecord();
            } else {
                Toast.makeText(getActivity(), "Check Internet Connection", Toast.LENGTH_SHORT).show();
            }
        } else {
            //UPLOADED
            if (isNetworkAvailable()) {
                getIntiationTenderUploadedRecord();
            } else {
                Toast.makeText(getActivity(), "Check Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void getIntiationTenderUploadedRecord() {

        progressDialog.setVisibility(View.VISIBLE);
        Call<GetProjectIntiationUploadedTender> call = webApi.getProjectIntiationUploadedTenderResponse(sp.getStringData(Constants.USER_ID), startdate, enddate);
        call.enqueue(new Callback<GetProjectIntiationUploadedTender>() {
            @Override
            public void onResponse(Call<GetProjectIntiationUploadedTender> call, Response<GetProjectIntiationUploadedTender> response) {

                progressDialog.setVisibility(View.GONE);
                int code = response.code();
                switch (code) {
                    case RESPONSE_NOT_FOUND:

                        Toast.makeText(getActivity(), "NO REORDS FOUND.", Toast.LENGTH_SHORT).show();
                        noRecordTextView.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        break;
                    case RESPONSE_ERROR:
                        Toast.makeText(getActivity(), "NO REORDS FOUND.", Toast.LENGTH_LONG).show();
                        noRecordTextView.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        break;
                    case RESPONSE_BAD:
                        Toast.makeText(getActivity(), "NO REORDS FOUND.", Toast.LENGTH_LONG).show();
                        noRecordTextView.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        break;
                    case RESPONSE_OK:
                        String status = response.body().getStatus();
                        if (status.equals("SUCCESS")) {
                            recyclerView.setVisibility(View.VISIBLE);
                            noRecordTextView.setVisibility(View.GONE);
                            uploadedPhotoLists = response.body().getResult();
                            if (uploadedPhotoLists.size() != 0) {

                                Uploadedadapter = new PhotoUploadAdapter(getActivity(), uploadedPhotoLists);

                                //setting adapter to recyclerview
                                recyclerView.setAdapter(Uploadedadapter);
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
                progressDialog.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getIntiationTenderRecord() {
        progressDialog.setVisibility(View.VISIBLE);
//        progressDialog.show();
//        progressDialog.setMessage("Loading...");
//        progressDialog.setCancelable(false);

        Call<GetProjectIntiationResponse> call = webApi.getProjectIntiationResponse(sp.getStringData(Constants.USER_ID), startdate, enddate);
        call.enqueue(new Callback<GetProjectIntiationResponse>() {
            @Override
            public void onResponse(Call<GetProjectIntiationResponse> call, Response<GetProjectIntiationResponse> response) {
                progressDialog.setVisibility(View.GONE);
                int code = response.code();
                switch (code) {
                    case RESPONSE_NOT_FOUND:

                        Toast.makeText(getActivity(), "NO REORDS FOUND.", Toast.LENGTH_SHORT).show();
                        noRecordTextView.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        break;
                    case RESPONSE_ERROR:
                        Toast.makeText(getActivity(), "NO REORDS FOUND.", Toast.LENGTH_LONG).show();
                        noRecordTextView.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        break;
                    case RESPONSE_BAD:
                        Toast.makeText(getActivity(), "NO REORDS FOUND.", Toast.LENGTH_LONG).show();
                        noRecordTextView.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        break;
                    case RESPONSE_OK:
                        String status = response.body().getStatus();
                        if (status.equals("SUCCESS")) {
                            recyclerView.setVisibility(View.VISIBLE);
                            noRecordTextView.setVisibility(View.GONE);
                            photoLists.clear();
                            photoLists = response.body().getResult();
                            if (photoLists.size() != 0) {

                                adapter = new PhotoNotUploadAdapter(getActivity(), photoLists);
                                adapter.setNotUploadedListener(ProjectIntiationFragment.this);

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
            public void onFailure(Call<GetProjectIntiationResponse> call, Throwable t) {
                progressDialog.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void toastMessage(String data) {
        Toast.makeText(getActivity(), data, Toast.LENGTH_LONG).show();
    }


    /**
     * version 1 code
     */
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View v= inflater.inflate(R.layout.fragment_project_intiation, container, false);
//        tabLayout = v.findViewById(R.id.tabs);
//        viewPager = v.findViewById(R.id.view_pager);
//        //tabs.addTab(tabs.newTab().setText("Photo Not Uploaded"));
//        //tabs.addTab(tabs.newTab().setText("Photo Uploaded"));
//        TabsPagerAdapter tabsPagerAdapter = new TabsPagerAdapter(getChildFragmentManager());
//
//        viewPager.setAdapter(tabsPagerAdapter);
//      //  viewPager.setCurrentItem(0);
//        tabLayout.setupWithViewPager(viewPager);
//        //tabs.setTabMode(TabLayout.MODE_FIXED);
//
//        return v;
//    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void notUploaded(resultResponse photoDTO1) {
        String tenderid=photoDTO1.getTenderId()+"";
        Intent intent =new Intent(getActivity(), InitiationPhotoUploadActivity.class);
        intent.putExtra("TENDER_ID",tenderid);
        startActivityForResult(intent,1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            boolean isUploaded = data.getBooleanExtra("data",false);
            if (isUploaded){
                getIntiationTenderRecord();
            }else{
                Toast.makeText(getActivity(), isUploaded+"", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
