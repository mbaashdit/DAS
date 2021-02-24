package com.aashdit.districtautomationsystem.activities;

import android.Manifest;
import android.content.Context;
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
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.aashdit.districtautomationsystem.Adapter.ImagesAdapter;
import com.aashdit.districtautomationsystem.Util.Constants;
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

public class GeoTaggingActivity extends AppCompatActivity implements LocationListener {

    private static final String TAG = "GeoTaggingActivity";

    private final int CAMERA_REQ_CODE = 100;

    private ActivityGeoTaggingBinding binding;

    private final int SETTINGS_REQ_CODE = 101;
    double longitude = 0.0, latitude = 0.0;

    private Long stageId, projectId;

    private ArrayList<TagData> tagData = new ArrayList<>();
    private ImagesAdapter adapter;
    private SharedPrefManager sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGeoTaggingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sp = SharedPrefManager.getInstance(this);
        userId = sp.getLongData(Constants.USER_ID);

        stageId = getIntent().getLongExtra("STAGE_ID", 0L);
        projectId = getIntent().getLongExtra("PROJ_ID", 0L);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestCameraPermission();
        }

        getGeoTagDetailsByProjectIdAndStageId();



        getLocation();

//        LocationFinder finder;
//        finder = new LocationFinder(this);
//        if (finder.canGetLocation()) {
//            latitude = finder.getLatitude();
//            longitude = finder.getLongitude();
//            Toast.makeText(this, "lat-lng " + latitude + " — " + longitude, Toast.LENGTH_LONG).show();
//        } else {
//            finder.showSettingsAlert();
//        }

        binding.ivGeoTagged.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (longitude != 0.0 || latitude != 0.0) {
                    openCamera();
                }
            }
        });
    }

    Long currentStageId,userId;
    String currentStageCode, currentStageName,imagePath;

    private void getGeoTagDetailsByProjectIdAndStageId() {
        AndroidNetworking.get(ServerApiList.BASE_URL.concat("api/awc/anganwadiConstruction/getGeoTagDetailsByProjectIdAndStageId" +
                "?projectId=" + projectId + "&stageId=" + stageId))
                .setTag("stopWorkPlan")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        if (Utility.isStringValid(response)) {
                            try {
                                JSONObject resObj = new JSONObject(response);
                                if (resObj.optString("flag").equals("Success")) {
                                    currentStageId = resObj.optLong("currentStageId");
                                    projectId = resObj.optLong("projectId");
                                    currentStageCode = resObj.optString("currentStageCode");
                                    currentStageName = resObj.optString("currentStageName");
                                    imagePath = resObj.optString("imagePath");

                                    JSONArray imageArray = resObj.optJSONArray("geoTagList");
                                    if (imageArray != null && imageArray.length() > 0) {
                                        for (int i = 0; i < imageArray.length(); i++) {
                                            TagData tag = TagData.parseTagData(imageArray.optJSONObject(i));
                                            tagData.add(tag);
                                        }
                                    }

                                    adapter = new ImagesAdapter(GeoTaggingActivity.this,tagData,imagePath);
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

    ArrayList<Bitmap> capturedBitmaps = new ArrayList<Bitmap>();

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
                if (photo != null) {

                    captureGeoTagImage(finalFile);
                }
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


        AndroidNetworking.upload(ServerApiList.BASE_URL.concat("api/awc/anganwadiConstruction/captureGeoTagImage"))
                .addMultipartFile("imagePath",path)
                .addMultipartParameter("projectId",String.valueOf(projectId))
                .addMultipartParameter("latitude",String.valueOf(latitude))
                .addMultipartParameter("longitude",String.valueOf(longitude))
                .addMultipartParameter("address",String.valueOf(capturedAddress))
                .addMultipartParameter("userId",String.valueOf(userId))
                .setTag("Upload Capture Image")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        getGeoTagDetailsByProjectIdAndStageId();
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: "+anError.getErrorDetail() );
                    }
                });



//        AndroidNetworking.post(ServerApiList.BASE_URL.concat("api/awc/anganwadiConstruction/captureGeoTagImage"))
//                .addBodyParameter("projectId",String.valueOf(projectId))
//                .addBodyParameter("imagePath",path.getAbsolutePath())
//                .addBodyParameter("latitude",String.valueOf(latitude))
//                .addBodyParameter("longitude",String.valueOf(longitude))
//                .addBodyParameter("address",String.valueOf(capturedAddress))
//                .addBodyParameter("userId",String.valueOf(userId))
//                .setTag("Upload Capture Image")
//                .setPriority(Priority.HIGH)
//                .build()
//                .getAsString(new StringRequestListener() {
//                    @Override
//                    public void onResponse(String response) {
//                        getGeoTagDetailsByProjectIdAndStageId();
//                    }
//
//                    @Override
//                    public void onError(ANError anError) {
//                        Log.e(TAG, "onError: "+anError.getErrorDetail() );
//                    }
//                });
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
                        } else {
                            Toast.makeText(GeoTaggingActivity.this, "Please Enable GPS", Toast.LENGTH_SHORT).show();
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
}