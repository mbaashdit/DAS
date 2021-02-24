package com.aashdit.districtautomationsystem.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.aashdit.districtautomationsystem.Adapter.StagesAdapter;
import com.aashdit.districtautomationsystem.R;
import com.aashdit.districtautomationsystem.Util.ServerApiList;
import com.aashdit.districtautomationsystem.Util.Utility;
import com.aashdit.districtautomationsystem.databinding.ActivityProjectDetailsBinding;
import com.aashdit.districtautomationsystem.model.Stage;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProjectDetailsActivity extends AppCompatActivity implements StagesAdapter.OnStageClickListener {

    private static final String TAG = "ProjectDetailsActivity";

    private BottomSheetDialog dialog;

    private ActivityProjectDetailsBinding binding;
    private Long proj_id;

    private ArrayList<Stage> stages = new ArrayList<>();

    private StagesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProjectDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        proj_id = getIntent().getLongExtra("PROJ_ID",0L);

        adapter = new StagesAdapter(this, stages);
        adapter.setOnStageClickListener(this);
        getProjectDetails();
        binding.rlViewPhase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showApproveBottomSheet();
            }
        });
    }

    private void getProjectDetails() {
        AndroidNetworking.get(ServerApiList.BASE_URL.concat("api/awc/anganwadiConstruction/getProjectDetailsByProjectId?projectId="+proj_id))
                .setTag("projectDetails")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        if(Utility.isStringValid(response)) {
                            try {
                                JSONObject resObj = new JSONObject(response);
                                if(resObj.optString("flag").equals("Success")){

                                    JSONArray stageArr = resObj.optJSONArray("stageList");
                                    if (stageArr != null && stageArr.length() > 0){
                                        for (int i = 0; i < stageArr.length(); i++) {
                                            Stage stage = Stage.parseStageResponse(stageArr.optJSONObject(i));
                                            stages.add(stage);
                                        }
                                        adapter.notifyDataSetChanged();

                                    }

                                    String districtName = resObj.optString("districtName");
                                    binding.tvDistName.setText(districtName);
                                    String blockName = resObj.optString("blockName");
                                    binding.tvBlockName.setText(blockName);
                                    String gpName = resObj.optString("gpName");
                                    binding.tvGpName.setText(gpName);
                                    String projectName = resObj.optString("projectName");
                                    binding.tvProjectTitle.setText(projectName);
                                    String financialYearName = resObj.optString("financialYearName");
                                    binding.tvProjectFy.setText(financialYearName);
                                    String projectCode = resObj.optString("projectCode");
                                    binding.tvProjCode.setText(projectCode);
                                    String approvalLetterNumber = resObj.optString("approvalLetterNumber");
                                    binding.tvLetterNo.setText(approvalLetterNumber);
                                    String schemeName = resObj.optString("schemeName");
                                    binding.tvSchName.setText(schemeName);
                                    String estimatedStartDate = resObj.optString("estimatedStartDate");
                                    binding.tvEsDate.setText(estimatedStartDate);
                                    String creationDate = resObj.optString("creationDate");
                                    binding.tvCrDate.setText(creationDate);
                                    String estimatedEndDate = resObj.optString("estimatedEndDate");
                                    binding.tvEeDate.setText(estimatedEndDate);
                                    String currentStageName = resObj.optString("currentStageName");
                                    String estimatedProjectCost = resObj.optString("estimatedProjectCost");
                                    binding.tvEstProjCost.setText(estimatedProjectCost);
                                    String anganwadiCenterName = resObj.optString("anganwadiCenterName");
                                    binding.tvAwcName.setText(anganwadiCenterName);
                                    String description = resObj.optString("description");
                                    binding.tvProjDesc.setText(description);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: "+anError.getErrorDetail() );
                    }
                });
    }

    private void showApproveBottomSheet() {

        View dialogView = getLayoutInflater().inflate(R.layout.bottom_sheet_phase_list, null);
        dialog = new BottomSheetDialog(this);
        dialog.setContentView(dialogView);

        RecyclerView mRvPhase = dialogView.findViewById(R.id.rv_phase_list);
        mRvPhase.setLayoutManager(new LinearLayoutManager(this));
        mRvPhase.setAdapter(adapter);

        ImageView mIvClose = dialogView.findViewById(R.id.iv_approve_close);
        mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void onProjectClick(int position) {
        Intent intent = new Intent(this,GeoTaggingActivity.class);
        intent.putExtra("STAGE_ID",stages.get(position).stageId);
        intent.putExtra("PROJ_ID",proj_id);
        startActivity(intent);
        dialog.dismiss();
    }
}