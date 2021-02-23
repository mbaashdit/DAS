package com.aashdit.districtautomationsystem.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aashdit.districtautomationsystem.R;
import com.aashdit.districtautomationsystem.model.Project;

import java.util.ArrayList;

public class ProjectListAdapter extends RecyclerView.Adapter<ProjectListAdapter.ProjectListHolder> {

    private static final String TAG = "ProjectListAdapter";

    private Context mContext;
    private ArrayList<Project> projects;

    public ProjectListAdapter(Context mContext, ArrayList<Project> projects) {
        this.mContext = mContext;
        this.projects = projects;
    }

    @NonNull
    @Override
    public ProjectListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.cell_project_list, parent, false);
        return new ProjectListHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectListHolder holder, int position) {

        Project p = projects.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onProjectClickListener.onProjectClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public static class ProjectListHolder extends RecyclerView.ViewHolder {

        public ProjectListHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
    OnProjectClickListener onProjectClickListener;

    public void setOnProjectClickListener(OnProjectClickListener onProjectClickListener) {
        this.onProjectClickListener = onProjectClickListener;
    }

    public interface OnProjectClickListener{
        void onProjectClick(int position);
    }
}
