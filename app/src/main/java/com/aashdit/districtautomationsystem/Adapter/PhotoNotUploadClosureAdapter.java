package com.aashdit.districtautomationsystem.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.aashdit.districtautomationsystem.R;
import com.aashdit.districtautomationsystem.Response.closureTenderResultResponse;
import com.aashdit.districtautomationsystem.Util.RegPrefManager;

import java.util.List;

public class PhotoNotUploadClosureAdapter extends RecyclerView.Adapter<PhotoNotUploadClosureAdapter.ChangeLocationHolder> {
    //this context we will use to inflate the layout
    private Context mCtx;
    private double latitude;
    private double longitude;
    //we are storing all the products in a list
    private List<closureTenderResultResponse> photosList;


    //getting the context and product list with constructor
    public PhotoNotUploadClosureAdapter(Context mCtx, List<closureTenderResultResponse> photosList) {
        this.mCtx = mCtx;
        this.photosList = photosList;
    }

    @Override
    public ChangeLocationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        // LayoutInflater.from(parent.getContext()).inflate(R.layout.card_listitem, parent, false);
        View view = inflater.inflate(R.layout.photosnotuploadedlist, null, false);
        return new ChangeLocationHolder(view);
    }

    @Override
    public void onBindViewHolder(ChangeLocationHolder holder, int position) {
        //getting the product of the specified position
        closureTenderResultResponse photoDTO1 = photosList.get(position);

        //binding the data with the viewholder views
        // holder.changeLocationTxt.setText(address.getAddress());

        holder.tendername.setText("Tender ID: " + photoDTO1.getTenderCode());
        holder.tenderagencyName.setText( photoDTO1.getAgencyName());
        holder.tenderprojectname.setText( photoDTO1.getProjectName());
        holder.tendercode.setVisibility(View.GONE);//.setText("Tender Code: "+photoDTO1.getTenderCode());
        holder.tenderprojectcode.setText( photoDTO1.getProjectCode());
        holder.tenderaggrementDate.setText(photoDTO1.getAggrementDate().trim());
        holder.tenderaggrementValue.setText( photoDTO1.getAggrementValue().trim());
        holder.tendertimeline.setText( photoDTO1.getTimeLine().trim());
        holder.tenderprojectid.setText( photoDTO1.getProjectId());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closureTenderResultResponse photoDTO1 = photosList.get(position);
                String tenderid = photoDTO1.getTenderId() + "";
                RegPrefManager.getInstance(mCtx).setClosureTenderId(tenderid);

                closureUploadListener.onClosureUpload(position);
//                Intent i = new Intent(mCtx, ClosuretakePhotoUploadActivity.class);
//                mCtx.startActivity(i);
            }
        });

        holder.uploadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
    }

    //get latitude and longitude from ADdress

    ClosureUploadListener closureUploadListener;

    public void setClosureUploadListener(ClosureUploadListener closureUploadListener) {
        this.closureUploadListener = closureUploadListener;
    }

    public interface ClosureUploadListener{
        void onClosureUpload(int position);
    }

    @Override
    public int getItemCount() {
        return photosList.size();
    }

    class ChangeLocationHolder extends RecyclerView.ViewHolder {

        TextView tendername, tenderagencyName, tenderprojectname, tendercode, tenderprojectcode, tenderaggrementDate, tenderaggrementValue, tendertimeline, tenderprojectid;
        Button uploadbtn;
        // CardView addressCardView;

        public ChangeLocationHolder(View itemView) {
            super(itemView);


            tendername = itemView.findViewById(R.id.tendername);
            tenderagencyName = itemView.findViewById(R.id.tenderagencyName);
            tenderprojectname = itemView.findViewById(R.id.tenderprojectname);
            tendercode = itemView.findViewById(R.id.tendercode);
            tenderprojectcode = itemView.findViewById(R.id.tenderprojectcode);
            tenderaggrementDate = itemView.findViewById(R.id.tenderaggrementDate);
            tenderaggrementValue = itemView.findViewById(R.id.tenderaggrementValue);
            tendertimeline = itemView.findViewById(R.id.tendertimeline);
            tenderprojectid = itemView.findViewById(R.id.tenderprojectid);


            uploadbtn = itemView.findViewById(R.id.uploadbtn);


            //      addressCardView = itemView.findViewById(R.id.addressCardView);

        }
    }
}
