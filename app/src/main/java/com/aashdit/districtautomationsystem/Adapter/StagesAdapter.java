package com.aashdit.districtautomationsystem.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aashdit.districtautomationsystem.R;
import com.aashdit.districtautomationsystem.model.Stage;

import java.util.ArrayList;

public class StagesAdapter extends RecyclerView.Adapter<StagesAdapter.ProjectListHolder> {

    private static final String TAG = "ProjectListAdapter";

    private Context mContext;
    private ArrayList<Stage> projects;

    public StagesAdapter(Context mContext, ArrayList<Stage> projects) {
        this.mContext = mContext;
        this.projects = projects;
    }

    @NonNull
    @Override
    public ProjectListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.cell_phase_list, parent, false);
        return new ProjectListHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectListHolder holder, int position) {

        Stage p = projects.get(position);
        if (p.imageUploaded) {
            holder.mIvOk.setVisibility(View.VISIBLE);
        } else {
            holder.mIvOk.setVisibility(View.GONE);
        }
        holder.mTvPhasePos.setText(String.valueOf(position + 1));
        holder.mTvPhaseName.setText(p.stageName);
//        if (p.stageId == 1) {
//            holder.mRlPhaseCount.setBackground(mContext.getDrawable(R.drawable.ic_foundation));
//        }
//        if (p.stageId == 2) {
//            holder.mRlPhaseCount.setBackground(mContext.getDrawable(R.drawable.ic_plinth));
//        }
//        if (p.stageId == 3) {
//            holder.mRlPhaseCount.setBackground(mContext.getDrawable(R.drawable.ic_lintel));
//        }
//        if (p.stageId == 4) {
//            holder.mRlPhaseCount.setBackground(mContext.getDrawable(R.drawable.ic_));
//        }
//        if (p.stageId == 5) {
//            holder.mRlPhaseCount.setBackground(mContext.getDrawable(R.drawable.ic_finishing));
//        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStageClickListener.onProjectClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

    public static class ProjectListHolder extends RecyclerView.ViewHolder {

        TextView mTvPhaseName, mTvPhasePos;
        ImageView mIvOk;
        RelativeLayout mRlPhaseCount;

        public ProjectListHolder(@NonNull View itemView) {
            super(itemView);
            mTvPhaseName = itemView.findViewById(R.id.cell_tv_phase_name);
            mTvPhasePos = itemView.findViewById(R.id.cell_tv_pos);
            mRlPhaseCount = itemView.findViewById(R.id.rl_phase_count);
            mIvOk = itemView.findViewById(R.id.cell_iv_ok);
        }
    }

    OnStageClickListener onStageClickListener;

    public void setOnStageClickListener(OnStageClickListener onStageClickListener) {
        this.onStageClickListener = onStageClickListener;
    }

    public interface OnStageClickListener {
        void onProjectClick(int position);
    }
}
