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
import com.aashdit.districtautomationsystem.model.TagData;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ProjectListHolder> {

    private static final String TAG = "ProjectListAdapter";

    private Context mContext;
    private ArrayList<TagData> projects;
    private String imageUrl;

    public ImagesAdapter(Context mContext, ArrayList<TagData> projects,String imageUrl) {
        this.mContext = mContext;
        this.projects = projects;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public ProjectListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.cell_image_list, parent, false);
        return new ProjectListHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectListHolder holder, int position) {

        TagData p = projects.get(position);
        holder.mTvLat.setText(p.latitude);
        holder.mTvLong.setText(p.longitude);
        holder.mTvAddress.setText(p.address);

        Glide.with(mContext).load(/*imageUrl*/"http://localhost:3030/DAS_Jajpur/awc/anganwadiConstruction/viewAwcProjectGeoTagImage?projectGeoTaggingId="+p.projectGeoTaggingId)
                .thumbnail(0.5f)
                .placeholder(R.drawable.avatardefault)
                .error(R.drawable.avatardefault)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.mIvImage);


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

        TextView mTvLat, mTvLong,mTvAddress;
        ImageView mIvImage;

        public ProjectListHolder(@NonNull View itemView) {
            super(itemView);
            mTvLat = itemView.findViewById(R.id.cell_tv_lat);
            mTvLong = itemView.findViewById(R.id.cell_tv_long);
            mTvAddress = itemView.findViewById(R.id.cell_tv_address);
            mIvImage = itemView.findViewById(R.id.cell_iv_capture);
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
