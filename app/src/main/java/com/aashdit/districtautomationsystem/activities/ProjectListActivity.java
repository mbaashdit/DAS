package com.aashdit.districtautomationsystem.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aashdit.districtautomationsystem.Adapter.ProjectListAdapter;
import com.aashdit.districtautomationsystem.DashboardActivity;
import com.aashdit.districtautomationsystem.LoginActivity;
import com.aashdit.districtautomationsystem.R;
import com.aashdit.districtautomationsystem.Util.Constants;
import com.aashdit.districtautomationsystem.Util.RegPrefManager;
import com.aashdit.districtautomationsystem.Util.ServerApiList;
import com.aashdit.districtautomationsystem.Util.SharedPrefManager;
import com.aashdit.districtautomationsystem.Util.Utility;
import com.aashdit.districtautomationsystem.databinding.ActivityProjectListBinding;
import com.aashdit.districtautomationsystem.model.Project;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ProjectListActivity extends AppCompatActivity implements ProjectListAdapter.OnProjectClickListener {

    private static final String TAG = "ProjectListActivity";

    private ActivityProjectListBinding binding;
    private SharedPrefManager prefs;

    private ArrayList<Project> projectList = new ArrayList<>();

    private ProjectListAdapter adapter;
    Long gpId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProjectListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefs = SharedPrefManager.getInstance(this);
        gpId = prefs.getLongData(Constants.GP_ID);

        binding.rvProjectList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProjectListAdapter(this,projectList);
        adapter.setOnProjectClickListener(this);
        binding.rvProjectList.setAdapter(adapter);

        binding.progress.setVisibility(View.VISIBLE);

        binding.swiperefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.swiperefreshlayout.setRefreshing(false);
                getProjects();
            }
        });

        binding.ivLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheet();
//                showLogoutDialog();
            }
        });

        builder = new AlertDialog.Builder(this);
        getProjects();
    }

    private BottomSheetDialog dialog;
    private void showBottomSheet() {
        {
            View dialogView = getLayoutInflater().inflate(R.layout.bottom_sheet_pause_work, null);
            dialog = new BottomSheetDialog(this);
            dialog.setContentView(dialogView);


            ImageView mIvClose = dialogView.findViewById(R.id.iv_sheet_pause_work_close);
            TextView profile = dialogView.findViewById(R.id.tv_profile);
            TextView logout = dialogView.findViewById(R.id.tv_logout);

            mIvClose.setOnClickListener(view -> {
                dialog.dismiss();
            });

            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showLogoutDialog();
                }
            });

            dialog.show();
        }
    }

    AlertDialog.Builder builder;
    private void showLogoutDialog() {
        builder.setMessage("Do you want to close this application ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//                        RegPrefManager.getInstance(DashboardActivity.this).clearData();
//                        Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
//                        startActivity(intent);
//                        finish();
                        RegPrefManager.getInstance(ProjectListActivity.this).clearData();
                        prefs.clear();
                        Intent intent = new Intent(ProjectListActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Exit ?");
        alert.show();
    }
    private void getProjects() {
        AndroidNetworking.get(ServerApiList.BASE_URL.concat("api/awc/anganwadiConstruction/getProjectListByGpId?gpId="+gpId))
                .setTag("stopWorkPlan")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        if(Utility.isStringValid(response)) {
                            binding.progress.setVisibility(View.GONE);
                            try {
                                JSONObject resObj = new JSONObject(response);
                                if(resObj.optString("flag").equals("Success")){
                                    JSONArray resArray = resObj.optJSONArray("awcProjectList");
                                    if(resArray != null && resArray.length() > 0){
                                        projectList.clear();
                                        for (int i = 0; i < resArray.length(); i++) {
                                            Project project = Project.parseProject(resArray.optJSONObject(i));
                                            projectList.add(project);
                                        }
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        binding.progress.setVisibility(View.GONE);
                        Log.e(TAG, "onError: "+anError.getErrorDetail() );
                    }
                });
    }

    @Override
    public void onProjectClick(int position) {
        Intent detailsIntent = new Intent(this, ProjectDetailsActivity.class);
        detailsIntent.putExtra("PROJ_ID",projectList.get(position).projectId);
        startActivity(detailsIntent);
    }
}