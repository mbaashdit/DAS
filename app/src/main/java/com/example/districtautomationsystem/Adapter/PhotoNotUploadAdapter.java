package com.example.districtautomationsystem.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.districtautomationsystem.DTO.PhotoNotUploadDTO;
import com.example.districtautomationsystem.InitiationPhotoUploadActivity;
import com.example.districtautomationsystem.R;
import com.example.districtautomationsystem.Response.resultResponse;
import com.example.districtautomationsystem.Util.RegPrefManager;


import java.util.List;

public class PhotoNotUploadAdapter extends RecyclerView.Adapter<PhotoNotUploadAdapter.PhotoNotUploadedHolder> {
    //this context we will use to inflate the layout
    private Context mCtx;
 private double latitude;
 private double longitude;
    //we are storing all the products in a list
    private List<resultResponse> photosList;

    //getting the context and product list with constructor
    public PhotoNotUploadAdapter(Context mCtx, List<resultResponse> photosList) {
        this.mCtx = mCtx;
        this.photosList = photosList;
    }

    @Override
    public PhotoNotUploadedHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
       // LayoutInflater.from(parent.getContext()).inflate(R.layout.card_listitem, parent, false);
        View view = inflater.inflate(R.layout.photosnotuploadedlist, null,false);
        return new PhotoNotUploadedHolder(view);
    }

    @Override
    public void onBindViewHolder(PhotoNotUploadedHolder holder, int position) {
        //getting the product of the specified position
        resultResponse photoDTO1 = photosList.get(position);

        //binding the data with the viewholder views

          holder.tendername.setText("Tender Code: "+photoDTO1.getTenderCode());
        holder.tenderagencyName.setText(photoDTO1.getAgencyName());
        holder.tenderprojectname.setText(photoDTO1.getProjectName());
        holder.tendercode.setVisibility(View.GONE);//.setText("Tender Code: "+photoDTO1.getTenderCode());
        holder.tenderprojectcode.setText(photoDTO1.getProjectCode());
        holder.tenderaggrementDate.setText(photoDTO1.getAggrementDate());
        holder.tenderaggrementValue.setText(photoDTO1.getAggrementValue());
        holder.tendertimeline.setText(photoDTO1.getTimeLine());
        holder.tenderprojectid.setText(photoDTO1.getProjectId());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultResponse photoDTO1 = photosList.get(position);
                String tenderid=photoDTO1.getTenderId()+"";
                RegPrefManager.getInstance(mCtx).setInitialTenderId(tenderid);

                Intent intent =new Intent(mCtx, InitiationPhotoUploadActivity.class);
                intent.putExtra("TENDER_ID",tenderid);
                mCtx.startActivity(intent);
            }
        });
        holder.uploadbtn.setVisibility(View.GONE);
        holder.uploadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultResponse photoDTO1 = photosList.get(position);
                String tenderid=photoDTO1.getTenderId()+"";
                RegPrefManager.getInstance(mCtx).setInitialTenderId(tenderid);

                notUploadedListener.notUploaded(photoDTO1);

            }
        });
    }

    //get latitude and longitude from ADdress

    NotUploadedListener notUploadedListener;

    public void setNotUploadedListener(NotUploadedListener notUploadedListener) {
        this.notUploadedListener = notUploadedListener;
    }

    public interface NotUploadedListener{
        void notUploaded(resultResponse resultResponse);
    }


    @Override
    public int getItemCount() {
        return photosList.size();
    }

    class PhotoNotUploadedHolder extends RecyclerView.ViewHolder {

        TextView tendername,tenderagencyName,tenderprojectname,tendercode,tenderprojectcode,tenderaggrementDate,tenderaggrementValue,tendertimeline,tenderprojectid;
        Button uploadbtn;
       // CardView addressCardView;

        public PhotoNotUploadedHolder(View itemView) {
            super(itemView);

            tendername = itemView.findViewById(R.id.tendername);
            tenderagencyName = itemView.findViewById(R.id.tenderagencyName);
            tenderprojectname= itemView.findViewById(R.id.tenderprojectname);
            tendercode= itemView.findViewById(R.id.tendercode);
            tenderprojectcode= itemView.findViewById(R.id.tenderprojectcode);
            tenderaggrementDate= itemView.findViewById(R.id.tenderaggrementDate);
            tenderaggrementValue= itemView.findViewById(R.id.tenderaggrementValue);
            tendertimeline= itemView.findViewById(R.id.tendertimeline);
            tenderprojectid= itemView.findViewById(R.id.tenderprojectid);


            uploadbtn = itemView.findViewById(R.id.uploadbtn);



      //      addressCardView = itemView.findViewById(R.id.addressCardView);

        }
    }
}
