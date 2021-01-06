package com.aashdit.districtautomationsystem.fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aashdit.districtautomationsystem.Adapter.PhotoNotUploadClosureAdapter;
import com.aashdit.districtautomationsystem.Adapter.PhotoUploadClosureAdapter;
import com.aashdit.districtautomationsystem.ClosuretakePhotoUploadActivity;
import com.aashdit.districtautomationsystem.R;
import com.aashdit.districtautomationsystem.Response.GetProjectClosureTenderlist;
import com.aashdit.districtautomationsystem.Response.GetProjectClosureUploadedTender;
import com.aashdit.districtautomationsystem.Response.ProjectClosureUploadedTenderlist;
import com.aashdit.districtautomationsystem.Response.closureTenderResultResponse;
import com.aashdit.districtautomationsystem.Util.ApiClient;
import com.aashdit.districtautomationsystem.Util.Constants;
import com.aashdit.districtautomationsystem.Util.SharedPrefManager;
import com.aashdit.districtautomationsystem.Util.WebApi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.app.Activity.RESULT_OK;
import static com.aashdit.districtautomationsystem.Util.Constants.RESPONSE_BAD;
import static com.aashdit.districtautomationsystem.Util.Constants.RESPONSE_ERROR;
import static com.aashdit.districtautomationsystem.Util.Constants.RESPONSE_NOT_FOUND;
import static com.aashdit.districtautomationsystem.Util.Constants.RESPONSE_OK;


public class ProjectCloserFragment extends Fragment implements PhotoNotUploadClosureAdapter.ClosureUploadListener {
//    private TabLayout tabLayout;
//    private ViewPager viewPager;


    Calendar dateSelected = Calendar.getInstance();
    RecyclerView recyclerView;
    TextView noRecordTextView;
    ProgressBar progressDialog;
    Retrofit retrofit;
    private WebApi webApi;
    private RelativeLayout mRlPhotoNotUploaded, mRlPhotoUploaded;
    private ImageView mIvNoUpload, mIvUpload;
    private TextView mTvNotUploaded, mTvUploaded;
    private TextView mTvFromDate, mTvToDate;


    private PhotoUploadClosureAdapter photoUploadedAdapterCloser;
    private PhotoNotUploadClosureAdapter adapterCloser;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private DatePickerDialog datePickerDialog;
    private SharedPrefManager sp;
    private String startdate="", enddate="";
    private ImageView mIvFromDate, mIvToDate, mIvSearch;

    private String currentSearch;

    private ArrayList<closureTenderResultResponse> closurephotoLists;
    private ArrayList<ProjectClosureUploadedTenderlist> photoListsUploaded;

    public ProjectCloserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //        tabLayout = v.findViewById(R.id.tabs);
//        viewPager = v.findViewById(R.id.view_pager);
//        //tabs.addTab(tabs.newTab().setText("Photo Not Uploaded"));
//        //tabs.addTab(tabs.newTab().setText("Photo Uploaded"));
//        TabsPagerCloserAdapter tabsPagerAdapter = new TabsPagerCloserAdapter(getChildFragmentManager());
//
//        viewPager.setAdapter(tabsPagerAdapter);
//        //  viewPager.setCurrentItem(0);
//        tabLayout.setupWithViewPager(viewPager);

        return inflater.inflate(R.layout.fragment_project_closer_2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        retrofit = ApiClient.getRetrofit();
        webApi = retrofit.create(WebApi.class);
        sp = SharedPrefManager.getInstance(getActivity());
        closurephotoLists = new ArrayList<>();
        progressDialog = view.findViewById(R.id.progress_bar);
        progressDialog.setVisibility(View.GONE);
        mRlPhotoNotUploaded = view.findViewById(R.id.rl_photo_not_uploaded);
        mRlPhotoUploaded = view.findViewById(R.id.rl_photo_uploaded);
        mIvNoUpload = view.findViewById(R.id.iv_no_upload);
        mIvUpload = view.findViewById(R.id.iv_upload);
        mTvNotUploaded = view.findViewById(R.id.tv_not_upload);
        mTvUploaded = view.findViewById(R.id.tv_uploaded);
        recyclerView = view.findViewById(R.id.rv_project_closure);
//        recyclerView = (RecyclerView) view.findViewById(R.id.mRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        recyclerView.addItemDecoration(new
//                DividerItemDecoration(getActivity(),
//                DividerItemDecoration.VERTICAL));
        noRecordTextView = view.findViewById(R.id.tv_no_closure_record);
        mTvFromDate = view.findViewById(R.id.tv_from_date);
        mTvToDate = view.findViewById(R.id.tv_to_date);

        mIvFromDate = view.findViewById(R.id.iv_from_calender);
        mIvToDate = view.findViewById(R.id.iv_to_calender);
        mIvSearch = view.findViewById(R.id.iv_search);

        mRlPhotoNotUploaded.setBackgroundResource(R.drawable.selected_btn_bg);
        mRlPhotoUploaded.setBackgroundResource(R.drawable.unselected_btn_bg);

        mIvNoUpload.setColorFilter(ContextCompat.getColor(getActivity(), R.color.mdtp_white), android.graphics.PorterDuff.Mode.SRC_IN);
        mIvUpload.setColorFilter(ContextCompat.getColor(getActivity(), R.color.mdtp_transparent_black), android.graphics.PorterDuff.Mode.SRC_IN);

        mTvNotUploaded.setTextColor(getResources().getColor(R.color.mdtp_white));


        currentSearch = "NOT_UPLOADED";
        isAlreadyChanged = sp.getBoolData("CLS_DATE_CHANGES");
        mRlPhotoNotUploaded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAlreadyChanged = true;
                sp.setStringData("CLS_START_DATE",startdate);
                sp.setStringData("CLS_END_DATE",enddate);
                mRlPhotoNotUploaded.setBackgroundResource(R.drawable.selected_btn_bg);
                mRlPhotoUploaded.setBackgroundResource(R.drawable.unselected_btn_bg);
                mIvNoUpload.setColorFilter(ContextCompat.getColor(Objects.requireNonNull(getActivity()), R.color.mdtp_white), android.graphics.PorterDuff.Mode.SRC_IN);
                mIvUpload.setColorFilter(ContextCompat.getColor(getActivity(), R.color.mdtp_transparent_black), android.graphics.PorterDuff.Mode.SRC_IN);
                mTvNotUploaded.setTextColor(getResources().getColor(R.color.mdtp_white));
                mTvUploaded.setTextColor(getResources().getColor(R.color.mdtp_transparent_black));

                currentSearch = "NOT_UPLOADED";
                setDafaultDateFormat();
                if (isNetworkAvailable()) {
                    getCloserTenderRecord();
                } else {
                    Toast.makeText(getActivity(), "Check Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mRlPhotoUploaded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAlreadyChanged = true;
                sp.setStringData("CLS_START_DATE",startdate);
                sp.setStringData("CLS_END_DATE",enddate);
                mRlPhotoUploaded.setBackgroundResource(R.drawable.selected_btn_bg);
                mRlPhotoNotUploaded.setBackgroundResource(R.drawable.unselected_btn_bg);
                mTvNotUploaded.setTextColor(getResources().getColor(R.color.mdtp_transparent_black));
                mIvNoUpload.setColorFilter(ContextCompat.getColor(Objects.requireNonNull(getActivity()), R.color.mdtp_transparent_black), android.graphics.PorterDuff.Mode.SRC_IN);

                mIvUpload.setColorFilter(ContextCompat.getColor(getActivity(), R.color.mdtp_white), android.graphics.PorterDuff.Mode.SRC_IN);
                mTvUploaded.setTextColor(getResources().getColor(R.color.mdtp_white));

                currentSearch = "UPLOADED";
                setDafaultDateFormat();
                if (isNetworkAvailable()) {
                    getCloserTenderUploadedRecord();
                } else {
                    Toast.makeText(getActivity(), "Check Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mIvSearch.setOnClickListener(view1 ->{
            isAlreadyChanged = true;
            checkForApiCall();
            sp.setStringData("CLS_START_DATE",startdate);
            sp.setStringData("CLS_END_DATE",enddate);
        });
        setDafaultDateFormat();

        mIvFromDate.setOnClickListener(view12 -> setDateTimeField());

        mIvToDate.setOnClickListener(view13 -> setTodayDateTimeField());
    }
    private boolean isAlreadyChanged = false;
    private void setDafaultDateFormat() {
        if (!isAlreadyChanged) {
            Date c = Calendar.getInstance().getTime();
            System.out.println("Current time => " + c);

            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String formattedDate = df.format(c);
            mTvToDate.setText(formattedDate);

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -2);  // two month back
            SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String formattedDate1 = df1.format(cal.getTime());

            mTvFromDate.setText(formattedDate1);
            startdate = mTvFromDate.getText().toString().trim();


            Calendar currentcal = Calendar.getInstance();
            SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String formattedDate2 = df2.format(currentcal.getTime());
            mTvToDate.setText(formattedDate2);
            enddate = mTvToDate.getText().toString();
        }else {
            startdate = sp.getStringData("CLS_START_DATE");
            enddate = sp.getStringData("CLS_END_DATE");
            mTvFromDate.setText(startdate);
            mTvToDate.setText(enddate);
        }
    }

    private void setDateTimeField() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(Objects.requireNonNull(getContext()),
                (view, year, monthOfYear, dayOfMonth) -> {

                    mTvFromDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    checkForApiCall();
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void setTodayDateTimeField() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(Objects.requireNonNull(getContext()),
                (view, year, monthOfYear, dayOfMonth) -> {

                    mTvToDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    checkForApiCall();
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
                getCloserTenderRecord();
            } else {
                Toast.makeText(getActivity(), "Check Internet Connection", Toast.LENGTH_SHORT).show();
            }
        } else {
            //UPLOADED
            if (isNetworkAvailable()) {
                getCloserTenderUploadedRecord();
            } else {
                Toast.makeText(getActivity(), "Check Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }

    }


    private void getCloserTenderUploadedRecord() {

        progressDialog.setVisibility(View.VISIBLE);
        Call<GetProjectClosureUploadedTender> call = webApi.getProjectClosureUploadedTenderlistResponse(sp.getStringData(Constants.USER_ID), startdate, enddate);
        call.enqueue(new Callback<GetProjectClosureUploadedTender>() {
            @Override
            public void onResponse(@NonNull Call<GetProjectClosureUploadedTender> call, @NonNull Response<GetProjectClosureUploadedTender> response) {

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
                        assert response.body() != null;
                        String status = response.body().getStatus();
                        if (status.equals("SUCCESS")) {
                            recyclerView.setVisibility(View.VISIBLE);
                            noRecordTextView.setVisibility(View.GONE);
                            photoListsUploaded = response.body().getResult();
                            if (photoListsUploaded.size() != 0) {

                                photoUploadedAdapterCloser = new PhotoUploadClosureAdapter(getActivity(), photoListsUploaded);

                                //setting adapter to recyclerview
                                recyclerView.setAdapter(photoUploadedAdapterCloser);
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
            public void onFailure(@NonNull Call<GetProjectClosureUploadedTender> call, @NonNull Throwable t) {

                progressDialog.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getCloserTenderRecord() {
        progressDialog.setVisibility(View.VISIBLE);
        Call<GetProjectClosureTenderlist> call = webApi.getProjectClosureTenderlistResponse(sp.getStringData(Constants.USER_ID), startdate, enddate);
        call.enqueue(new Callback<GetProjectClosureTenderlist>() {
            @Override
            public void onResponse(@NonNull Call<GetProjectClosureTenderlist> call, @NonNull Response<GetProjectClosureTenderlist> response) {
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
                        assert response.body() != null;
                        String status = response.body().getStatus();
                        if (status.equals("SUCCESS")) {
                            recyclerView.setVisibility(View.VISIBLE);
                            noRecordTextView.setVisibility(View.GONE);
                            closurephotoLists = response.body().getResult();
                            if (closurephotoLists.size() != 0) {

                                adapterCloser = new PhotoNotUploadClosureAdapter(getActivity(), closurephotoLists);
                                adapterCloser.setClosureUploadListener(ProjectCloserFragment.this);

                                //setting adapter to recyclerview
                                recyclerView.setAdapter(adapterCloser);
                                noRecordTextView.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
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
            public void onFailure(@NonNull Call<GetProjectClosureTenderlist> call, @NonNull Throwable t) {
                progressDialog.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toastMessage(String data) {
        Toast.makeText(getActivity(), data, Toast.LENGTH_LONG).show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) Objects.requireNonNull(getActivity()).getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    @Override
    public void onDetach() {
        super.onDetach();

        startdate = mTvFromDate.getText().toString().trim();
        enddate = mTvToDate.getText().toString().trim();

        startdate = sp.getStringData("CLS_START_DATE");
        enddate = sp.getStringData("CLS_END_DATE");
        isAlreadyChanged = true;
        sp.setBoolData("CLS_DATE_CHANGES",true);
    }
    @Override
    public void onClosureUpload(int position) {
        Intent i = new Intent(getActivity(), ClosuretakePhotoUploadActivity.class);
        startActivityForResult(i,1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            boolean isUploaded = data.getBooleanExtra("data",false);
            if (isUploaded){
                getCloserTenderRecord();
            }else{
                Toast.makeText(getActivity(), isUploaded+"", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
