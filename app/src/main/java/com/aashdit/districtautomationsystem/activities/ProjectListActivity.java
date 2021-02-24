package com.aashdit.districtautomationsystem.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.aashdit.districtautomationsystem.Adapter.ProjectListAdapter;
import com.aashdit.districtautomationsystem.R;
import com.aashdit.districtautomationsystem.Util.Constants;
import com.aashdit.districtautomationsystem.Util.ServerApiList;
import com.aashdit.districtautomationsystem.Util.SharedPrefManager;
import com.aashdit.districtautomationsystem.Util.Utility;
import com.aashdit.districtautomationsystem.databinding.ActivityProjectListBinding;
import com.aashdit.districtautomationsystem.model.Project;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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

        getProjects();
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