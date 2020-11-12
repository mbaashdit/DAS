package com.example.districtautomationsystem.fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.districtautomationsystem.PhotoNotUploadListActivity;
import com.example.districtautomationsystem.R;
import com.example.districtautomationsystem.Util.RegPrefManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@SuppressWarnings("All")
public class PhotoNotUploadedFragment extends Fragment {
    Calendar dateSelected = Calendar.getInstance();
    private TextView fromdateid, displayfromdate, todateid, displaytodate;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private Button searchbtn;
    private DatePickerDialog datePickerDialog;

    public PhotoNotUploadedFragment() {
        // Required empty public constructor
    }


    public static PhotoNotUploadedFragment newInstance() {
        PhotoNotUploadedFragment fragment = new PhotoNotUploadedFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_photo_not_uploaded, container, false);
        fromdateid = v.findViewById(R.id.fromdateid);
        displayfromdate = v.findViewById(R.id.displayfromdate);
        todateid = v.findViewById(R.id.todateid);
        displaytodate = v.findViewById(R.id.displaytodate);
        searchbtn = v.findViewById(R.id.searchbtn);

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c);
        displaytodate.setText(formattedDate);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -2);  // two month back
        SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate1 = df1.format(cal.getTime());

        displayfromdate.setText(formattedDate1);

        fromdateid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDateTimeField();
            }
        });

        todateid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTodayDateTimeField();
            }
        });

        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sdate = displayfromdate.getText().toString();
                String edate = displaytodate.getText().toString();
                RegPrefManager.getInstance(getContext()).setProjectIntiationOrCloser("Initiation");
                RegPrefManager.getInstance(getContext()).setIntiationProjectList(sdate, edate);
                Intent i = new Intent(getContext(), PhotoNotUploadListActivity.class);
                getContext().startActivity(i);
            }
        });

        return v;
    }

    private void setDateTimeField() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        displayfromdate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void setTodayDateTimeField() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        displaytodate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

}
