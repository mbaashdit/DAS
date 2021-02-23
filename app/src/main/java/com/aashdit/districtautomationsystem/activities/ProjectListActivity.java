package com.aashdit.districtautomationsystem.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.aashdit.districtautomationsystem.R;
import com.aashdit.districtautomationsystem.databinding.ActivityProjectListBinding;

public class ProjectListActivity extends AppCompatActivity {

    private static final String TAG = "ProjectListActivity";

    private ActivityProjectListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProjectListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}