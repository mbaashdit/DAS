package com.aashdit.districtautomationsystem.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aashdit.districtautomationsystem.Adapter.ImagesAdapter;
import com.aashdit.districtautomationsystem.LoginActivity;
import com.aashdit.districtautomationsystem.Util.Constants;
import com.aashdit.districtautomationsystem.Util.RegPrefManager;
import com.aashdit.districtautomationsystem.Util.ServerApiList;
import com.aashdit.districtautomationsystem.Util.SharedPrefManager;
import com.aashdit.districtautomationsystem.Util.Utility;
import com.aashdit.districtautomationsystem.databinding.ActivityGeoTaggingBinding;
import com.aashdit.districtautomationsystem.model.TagData;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import views.ImageFullScreen;

public class GeoTaggingActivity extends AppCompatActivity implements LocationListener, ImagesAdapter.ImagesListener {

    private static final String TAG = "GeoTaggingActivity";

    private final int CAMERA_REQ_CODE = 100;

    private ActivityGeoTaggingBinding binding;

    private final int SETTINGS_REQ_CODE = 101;
    double longitude = 0.0, latitude = 0.0;

    private Long stageId, projectId;

    private ArrayList<TagData> tagData = new ArrayList<>();
    private ImagesAdapter adapter;
    private SharedPrefManager sp;

    private String remark = "";
    private String currentPhaseCode = "";
    private String currentStageCode = "";
    private String stageCode = "";

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
    AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        binding = ActivityGeoTaggingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        sp = SharedPrefManager.getInstance(this);
        userId = sp.getLongData(Constants.USER_ID);
        builder = new AlertDialog.Builder(this);
        stageId = getIntent().getLongExtra("STAGE_ID", 0L);
        projectId = getIntent().getLongExtra("PROJ_ID", 0L);
        stageCode = getIntent().getStringExtra("STAGECODE");
        currentStageCode = getIntent().getStringExtra("CURRENT_STAGE_CODE");
        currentPhaseCode = getIntent().getStringExtra("CURRENT_PHASE_CODE");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestCameraPermission();
        }

        binding.progress.setVisibility(View.GONE);
        getGeoTagDetailsByProjectIdAndStageId();
        getLocation();



        if (currentPhaseCode.equals("BEFORE_GEO_TAG") || currentPhaseCode.equals("GEO_TAG_REVERTED")) {
            binding.ivGeoTagged.setVisibility(View.VISIBLE);
//            binding.rlSubmitPhase.setVisibility(View.VISIBLE);
            if (tagData.size() >0){
                binding.rlSubmitPhase.setVisibility(View.VISIBLE);
            }else{
                binding.rlSubmitPhase.setVisibility(View.GONE);
            }
        } else {
            binding.ivGeoTagged.setVisibility(View.GONE);
            binding.rlSubmitPhase.setVisibility(View.GONE);
        }
//        if (stageCode.equals(currentStageCode)) {
//            binding.tvRemarkLbl.setVisibility(View.VISIBLE);
//            binding.remark.setVisibility(View.VISIBLE);
//        } else {
//            binding.tvRemarkLbl.setVisibility(View.GONE);
//            binding.remark.setVisibility(View.GONE);
//        }

        if (longitude == 0.0 || latitude == 0.0) {
            Toast.makeText(GeoTaggingActivity.this,"Fetching Location, Please wait.", Toast.LENGTH_LONG).show();
            binding.progress.setVisibility(View.VISIBLE);
        }else{
            binding.progress.setVisibility(View.GONE);
        }

        binding.ivGeoTagged.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (longitude != 0.0 || latitude != 0.0) {
                    openCamera();
                }else{
                    Toast.makeText(GeoTaggingActivity.this,"Fetching Location, Please wait.", Toast.LENGTH_LONG).show();
                    binding.progress.setVisibility(View.VISIBLE);
                }
            }
        });
        binding.rlSubmitPhase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remark = binding.remark.getText().toString().trim();
                if (!TextUtils.isEmpty(remark)) {
                    captureGeoTagDetails();
                }else{
                    Toast.makeText(GeoTaggingActivity.this, "Please Enter Remark", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void showMessage(String msg) {
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        finish();

                    }
                });

        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Exit ?");
        alert.show();
    }
    private void captureGeoTagDetails() {
        AndroidNetworking.post(ServerApiList.BASE_URL.concat("api/awc/anganwadiConstruction/captureGeoTagDetails"))
                .addBodyParameter("projectId", String.valueOf(projectId))
                .addBodyParameter("remarks", remark)
                .addBodyParameter("userId", String.valueOf(userId))
                .setTag("captureGeoTagDetails")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject resObj = new JSONObject(response);
                            if (resObj.optString("flag").equals("Success")) {
                                showMessage(resObj.optString("Message"));
//                                Toast.makeText(GeoTaggingActivity.this, resObj.optString("Message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });
    }

    Long currentStageId, userId;
    String currentStageName, imagePath, latestRemarks;

    private void getGeoTagDetailsByProjectIdAndStageId() {
        binding.progress.setVisibility(View.VISIBLE);
        AndroidNetworking.get(ServerApiList.BASE_URL.concat("api/awc/anganwadiConstruction/getGeoTagDetailsByProjectIdAndStageId" +
                "?projectId=" + projectId + "&stageId=" + stageId))
                .setTag("stopWorkPlan")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        binding.progress.setVisibility(View.GONE);
                        if (Utility.isStringValid(response)) {
                            try {
                                JSONObject resObj = new JSONObject(response);
                                if (resObj.optString("flag").equals("Success")) {

                                    if (tagData.size() >0){
                                        binding.rlSubmitPhase.setVisibility(View.VISIBLE);
                                    }else{
                                        binding.rlSubmitPhase.setVisibility(View.GONE);
                                    }

                                    currentStageId = resObj.optLong("currentStageId");
                                    projectId = resObj.optLong("projectId");
                                    currentStageCode = resObj.optString("currentStageCode");
                                    currentStageName = resObj.optString("currentStageName");
                                    currentPhaseCode = resObj.optString("currentPhaseCode");
                                    latestRemarks = resObj.optString("latestRemarks");
                                    binding.remark.setText(latestRemarks);
                                    imagePath = resObj.optString("imagePath");
                                    if (currentPhaseCode.equals("BEFORE_GEO_TAG") || currentPhaseCode.equals("GEO_TAG_REVERTED")) {
                                        binding.ivGeoTagged.setVisibility(View.VISIBLE);
//                                        binding.rlSubmitPhase.setVisibility(View.VISIBLE);
                                        if (tagData.size() >0){
                                            binding.rlSubmitPhase.setVisibility(View.VISIBLE);
                                        }else{
                                            binding.rlSubmitPhase.setVisibility(View.GONE);
                                        }
                                    } else {
                                        binding.ivGeoTagged.setVisibility(View.GONE);
                                        binding.rlSubmitPhase.setVisibility(View.GONE);
                                    }

                                    JSONArray imageArray = resObj.optJSONArray("geoTagList");
                                    if (imageArray != null && imageArray.length() > 0) {
                                        tagData.clear();
                                        for (int i = 0; i < imageArray.length(); i++) {
                                            TagData tag = TagData.parseTagData(imageArray.optJSONObject(i));
                                            tagData.add(tag);
                                        }
                                    }
                                    if (tagData.size() > 0) {
                                        binding.tvRemarkLbl.setVisibility(View.VISIBLE);
                                        binding.remark.setVisibility(View.VISIBLE);
                                    } else {
                                        binding.tvRemarkLbl.setVisibility(View.GONE);
                                        binding.remark.setVisibility(View.GONE);
                                    }
                                    adapter = new ImagesAdapter(GeoTaggingActivity.this, tagData, imagePath, currentPhaseCode, currentStageCode, stageCode);
                                    adapter.setImagesListener(GeoTaggingActivity.this);
                                    binding.rvUploadedImg.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                                            RecyclerView.HORIZONTAL, false));
                                    binding.rvUploadedImg.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        binding.progress.setVisibility(View.GONE);
                    }
                });
    }

    private LocationManager locationManager;

    private void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQ_CODE && resultCode == RESULT_OK && data != null) {
            if (data.getExtras() != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");

                // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                Uri tempUri = getImageUri(getApplicationContext(), photo);

                // CALL THIS METHOD TO GET THE ACTUAL PATH
                File finalFile = new File(getRealPathFromURI(tempUri));

                Log.i(TAG, "onActivityResult: imagePath tempUri::::: "+tempUri);
                Log.i(TAG, "onActivityResult: imagePath finalFile::::: "+finalFile);

                captureGeoTagImage(finalFile);
            }
        }
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage,
                "IMG_"+System.currentTimeMillis(), null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }
    //              "projectId="+projectId+"&imagePath="+path+"&latitude="+latitude+"&longitude="+longitude+"&address="+capturedAddress
    private void captureGeoTagImage(File path) {

        binding.progress.setVisibility(View.VISIBLE);
        AndroidNetworking.upload(ServerApiList.BASE_URL.concat("api/awc/anganwadiConstruction/captureGeoTagImage"))
                .addMultipartFile("imagePath", path)
                .addMultipartParameter("projectId", String.valueOf(projectId))
                .addMultipartParameter("latitude", String.valueOf(latitude))
                .addMultipartParameter("longitude", String.valueOf(longitude))
                .addMultipartParameter("address", String.valueOf(capturedAddress))
                .addMultipartParameter("userId", String.valueOf(userId))
                .setTag("Upload Capture Image")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        binding.progress.setVisibility(View.GONE);
                        try {
                            JSONObject resObj = new JSONObject(response);
                            if (resObj.optString("flag").equals("Success")) {
                                Toast.makeText(GeoTaggingActivity.this, resObj.optString("Message"), Toast.LENGTH_LONG).show();
                                getGeoTagDetailsByProjectIdAndStageId();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        binding.progress.setVisibility(View.GONE);
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });

    }

    /**
     * Requesting camera permission
     */
    private void requestCameraPermission() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        // permission is granted
                        if (locationManager != null) {
                            openCamera();
                        }
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        // check for permanent denial of permission
                        if (response.isPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQ_CODE);
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(GeoTaggingActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();

    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, SETTINGS_REQ_CODE);
    }

    String capturedAddress = "";

    @Override
    public void onLocationChanged(Location location) {
        Log.d("Tag", "LatLng===>" + location.getLatitude() + " " + location.getLongitude());

        if (location.getLatitude() != 0.0 && location.getLongitude() != 0.0) {
            binding.progress.setVisibility(View.GONE);
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            Geocoder gc = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = gc.getFromLocation(latitude, longitude, 1);
                StringBuilder sb = new StringBuilder();
                if (addresses.size() > 0) {
                    Address address = addresses.get(0);
                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                        sb.append(address.getAddressLine(i)).append("\n");
                    }
                    if (address.getAddressLine(0) != null)
                        capturedAddress = address.getAddressLine(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(this, "Fetching Location, Please wait.", Toast.LENGTH_LONG).show();
            binding.progress.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onImageZoom(int position, Long imgId) {
        new ImageFullScreen(this, "http://209.97.136.18:8080/dist_auto_system/api/awc/anganwadiConstruction/viewAwcProjectGeoTagImage?projectGeoTaggingId=" + imgId);

    }

    @Override
    public void onImageDelete(int position, Long imgId) {

        AndroidNetworking.post(ServerApiList.BASE_URL.concat("api/awc/anganwadiConstruction/deleteGeoTagImage"))
                .addBodyParameter("projectGeoTaggingId", String.valueOf(imgId))
                .addBodyParameter("userId", String.valueOf(userId))
                .setTag("Delete Capture Image")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject resObj = new JSONObject(response);
                            if (resObj.optString("flag").equals("Success")) {
                                Toast.makeText(GeoTaggingActivity.this, resObj.optString("Message"), Toast.LENGTH_LONG).show();
                                getGeoTagDetailsByProjectIdAndStageId();
                                adapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError.getErrorDetail());
                    }
                });
    }
}