package com.aashdit.districtautomationsystem.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.aashdit.districtautomationsystem.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class ProjectDetailsActivity extends AppCompatActivity {

    private static final String TAG = "ProjectDetailsActivity";

    private BottomSheetDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_details);
    }

    private void showApproveBottomSheet() {

        View dialogView = getLayoutInflater().inflate(R.layout.bottom_sheet_phase_list, null);
        dialog = new BottomSheetDialog(this);
        dialog.setContentView(dialogView);

        ImageView mIvClose = dialogView.findViewById(R.id.iv_approve_close);
        mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}