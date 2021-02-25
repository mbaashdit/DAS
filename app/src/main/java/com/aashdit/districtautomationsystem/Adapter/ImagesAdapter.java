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
    private String imageUrl, currPhaseCode, currStageCode, stageCode;

    public ImagesAdapter(Context mContext, ArrayList<TagData> projects,String imageUrl,
                         String currPhaseCode,String currStageCode,String stageCode) {
        this.mContext = mContext;
        this.projects = projects;
        this.imageUrl = imageUrl;
        this.currPhaseCode = currPhaseCode;
        this.currStageCode = currStageCode;
        this.stageCode = stageCode;
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
        holder.mTvLat.setText("Latitude : "+p.latitude);
        holder.mTvLong.setText("Longitude : "+p.longitude);
        holder.mTvAddress.setText("Address : "+p.address);

        Glide.with(mContext).load("http://209.97.136.18:8080/dist_auto_system/api/awc/anganwadiConstruction/viewAwcProjectGeoTagImage?projectGeoTaggingId="+p.projectGeoTaggingId)
                .thumbnail(0.5f)
                .placeholder(R.drawable.avatardefault)
                .error(R.drawable.avatardefault)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.mIvImage);

        if(currStageCode.equals(stageCode) && (currPhaseCode.equals("BEFORE_GEO_TAG") || currPhaseCode.equals("GEO_TAG_REVERTED"))){
            holder.mIvDelete.setVisibility(View.VISIBLE);
        }else{
            holder.mIvDelete.setVisibility(View.GONE);
        }

        holder.mIvZoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagesListener.onImageZoom(position,p.projectGeoTaggingId);
            }
        });
        holder.mIvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagesListener.onImageDelete(position,p.projectGeoTaggingId);
            }
        });

    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

    public static class ProjectListHolder extends RecyclerView.ViewHolder {

        TextView mTvLat, mTvLong,mTvAddress;
        ImageView mIvImage,mIvDelete,mIvZoom;

        public ProjectListHolder(@NonNull View itemView) {
            super(itemView);
            mTvLat = itemView.findViewById(R.id.cell_tv_lat);
            mTvLong = itemView.findViewById(R.id.cell_tv_long);
            mTvAddress = itemView.findViewById(R.id.cell_tv_address);
            mIvImage = itemView.findViewById(R.id.cell_iv_capture);
            mIvDelete = itemView.findViewById(R.id.iv_delete);
            mIvZoom = itemView.findViewById(R.id.iv_zoom);
        }
    }
    ImagesListener imagesListener;

    public void setImagesListener(ImagesListener imagesListener) {
        this.imagesListener = imagesListener;
    }

    public interface ImagesListener{
        void onImageZoom(int position,Long imgId);
        void onImageDelete(int position,Long imgId);
    }
}
