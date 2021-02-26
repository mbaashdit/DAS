package com.aashdit.districtautomationsystem;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;

import com.aashdit.districtautomationsystem.Response.InitiationUpload;
import com.androidnetworking.AndroidNetworking;
import com.aashdit.districtautomationsystem.Response.InitiationPhotoUploadResponse;
import com.aashdit.districtautomationsystem.Util.ApiClient;
import com.aashdit.districtautomationsystem.Util.Constants;
import com.aashdit.districtautomationsystem.Util.FileUtils;
import com.aashdit.districtautomationsystem.Util.RegPrefManager;
import com.aashdit.districtautomationsystem.Util.SharedPrefManager;
import com.aashdit.districtautomationsystem.Util.WebApi;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Calendar;

import io.realm.Realm;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

@SuppressWarnings("All")
public class InitiationPhotoUploadActivity extends AppCompatActivity implements View.OnClickListener, LocationListener {

    private static final String TAG = "InitiationPhotoUploadAc";


    private static final int REQUEST_GALLERY_CADE = 201;
    private static final int REQUEST_CAMERA_CADE = 202;
    ProgressDialog progressDialog;
    Retrofit retrofit;
    LocationManager locationManager;
    double latitude, longitude;
    byte[] imageBytes;
    int SELECT_FILE = 1;
    int REQUEST_CAMERA = 2;
    String imageFilePath = "";
    Uri selectedImage;
    Bitmap bmp;
    private ImageView photoIv;
    private EditText remark;
    private Button uploadbtn;
    private WebApi webApi;
    private String tenderid, userid;
    private Uri uri;

    private Realm realm;
    private SharedPrefManager sp;
    private String tenderId;
    private Toolbar toolbar;
    private LinearLayout mLlInitiationRoot;

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initiation_photo_upload);

        realm = Realm.getDefaultInstance();
        mLlInitiationRoot = findViewById(R.id.ll_initiation_upload_root);
        toolbar = findViewById(R.id.toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        sp = SharedPrefManager.getInstance(this);
        retrofit = ApiClient.getRetrofit();
        webApi = retrofit.create(WebApi.class);

        tenderId = getIntent().getStringExtra("TENDER_ID");
        progressDialog = new ProgressDialog(InitiationPhotoUploadActivity.this);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        toolbar.setNavigationIcon(R.drawable.ic_back_button);
//        //  setTitle("My Profile");
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                startActivity(new Intent(InitiationPhotoUploadActivity.this, PhotoNotUploadListActivity.class));
////                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
////                finish();
//                onBackPressed();
//            }
//        });
        AndroidNetworking.initialize(getApplicationContext());
        photoIv = findViewById(R.id.photoIv);
        remark = findViewById(R.id.remark);
        uploadbtn = findViewById(R.id.uploadbtn);

        //get tenderid
        tenderid = RegPrefManager.getInstance(InitiationPhotoUploadActivity.this).getInitialTenderId();
        userid = String.valueOf(sp.getLongData(Constants.USER_ID));
//        userid = RegPrefManager.getInstance(InitiationPhotoUploadActivity.this).getLoginUserid();
        photoIv.setOnClickListener(this);
        uploadbtn.setOnClickListener(this);
        getLocation();

        if (ContextCompat.checkSelfPermission(InitiationPhotoUploadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(InitiationPhotoUploadActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(InitiationPhotoUploadActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                ActivityCompat.requestPermissions(InitiationPhotoUploadActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

            } else {

                ActivityCompat.requestPermissions(InitiationPhotoUploadActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }

        } else {

        }

        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                intent.putExtra("data",false);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.photoIv:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (ContextCompat.checkSelfPermission(InitiationPhotoUploadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        startDialog();
                    } else {
                        ActivityCompat.requestPermissions(InitiationPhotoUploadActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_GALLERY_CADE);
                    }
                } else {
                    startDialog();
                }
                break;
            case R.id.uploadbtn:
                if (isNetworkAvailable()) {

                    intiationUploadRecord();

                } else {
                    toastMessage("Please Check Internet Connection");

                    InitiationUpload initiationUpload = new InitiationUpload();
                    initiationUpload.imageFilePath = imageFilePath;
                    initiationUpload.lat = String.valueOf(latitude);
                    initiationUpload.lang = String.valueOf(longitude);
                    initiationUpload.tenderid = tenderid;
                    initiationUpload.userid = userid;
                    initiationUpload.uploadLevel = "INITIAL";
                    initiationUpload.remark = remark.getText().toString();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm.insertOrUpdate(initiationUpload);
                        }
                    });

                }

                break;
        }
    }

    private void startDialog() {
        final CharSequence[] items = {"Take Photo",
                "Cancel"};


        AlertDialog.Builder builder = new AlertDialog.Builder(InitiationPhotoUploadActivity.this);
        builder.setTitle("Add Photo!");

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Take Photo")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(InitiationPhotoUploadActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            openCamera();
                        } else {
                            ActivityCompat.requestPermissions(InitiationPhotoUploadActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_CADE);
                        }
                    } else {
                        openCamera();
                    }

                }
//                else if (items[item].equals("Choose from Gallery")) {
//                    Intent picIntent = new Intent(Intent.ACTION_GET_CONTENT, null);
//                    picIntent.setType("image/*");
//                    picIntent.putExtra("return_data", true);
//                    startActivityForResult(picIntent, SELECT_FILE);
//
//                }
                else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == SELECT_FILE) {

            onSelectFromGalleryResult(data);

        } else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CAMERA) {
            if (data != null) {
                if (data.hasExtra("data")) {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    onCaptureImageResult(bitmap);
                }
            } else {
                Toast.makeText(this, "null return", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void onCaptureImageResult(Bitmap bitmap) {
        if (bitmap != null) {
            if (!bitmap.equals("")) {
                uri = getImageUri(InitiationPhotoUploadActivity.this, bitmap);
                if (uri != null) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    try {
                        BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);
                        options.inSampleSize = calculateInSampleSize(options, 100, 100);
                        options.inJustDecodeBounds = false;
                        bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bmp.compress(Bitmap.CompressFormat.JPEG, 60, baos);
                        imageBytes = baos.toByteArray();


                        Log.v("ff", String.valueOf(bmp));
                        Bitmap compress = compressBitmap(bmp);
                        Log.v("ff1", String.valueOf(compress));

                        photoIv.setImageBitmap(compress);
                        Uri furi = getImageUri(InitiationPhotoUploadActivity.this, compress);
                        //File finalFile = new File(getRealPathFromUri(uri));
                        File finalFile = FileUtils.getFile(InitiationPhotoUploadActivity.this, furi);
                        imageFilePath = finalFile.toString();
                        Log.v("imagepath", imageFilePath);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        }
    }

    private Uri getImageUri(InitiationPhotoUploadActivity youractivity, Bitmap bitmap) {
        String path = "";
        if (!bitmap.equals("")) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            path = MediaStore.Images.Media.insertImage(youractivity.getContentResolver(), bitmap, "IMG_"+ System.currentTimeMillis() /*Calendar.getInstance().getTime()*/, null);

        }
        return Uri.parse(path);
    }

    private Bitmap compressBitmap(Bitmap btm) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        btm.compress(Bitmap.CompressFormat.JPEG, 50, stream);

        byte[] byteArray = stream.toByteArray();
        Bitmap compressedBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        return compressedBitmap;
    }

    private void onSelectFromGalleryResult(Intent data) {
        Uri selectedImageUri = data.getData();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImageUri), null, options);
            options.inSampleSize = calculateInSampleSize(options, 100, 100);
            options.inJustDecodeBounds = false;
            bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImageUri), null, options);


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 60, baos);
            imageBytes = baos.toByteArray();

            selectedImage = data.getData();
            Bitmap compress = compressBitmap(bmp);
            Log.v("ff1", String.valueOf(compress));
            Uri furi = getImageUri(InitiationPhotoUploadActivity.this, compress);

            photoIv.setImageBitmap(compress);
            //File finalFile = new File(getRealPathFromUri(furi));
            File finalFile = FileUtils.getFile(InitiationPhotoUploadActivity.this, furi);
            imageFilePath = finalFile.toString();
            Log.v("imagepath", imageFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void toastMessage(String data) {
        Toast.makeText(getApplicationContext(), data, Toast.LENGTH_LONG).show();
    }

    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("Tag", "LatLng===>" + location.getLatitude() + " " + location.getLongitude());
        latitude = location.getLatitude();
        longitude = location.getLongitude();
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


    private void intiationUploadRecord() {
        String remarktext = remark.getText().toString();
        String uploadLevel = "INITIAL";
        String lat = String.valueOf(latitude);
        String lang = String.valueOf(longitude);

        Log.d("Tag", "tenderid =>" + tenderid);
        Log.d("Tag", "tenderid =>" + tenderId);
        Log.d("Tag", "remarktext =>" + remarktext);
        Log.d("Tag", "userid =>" + userid);
        Log.d("Tag", "latitude =>" + lat);
        Log.d("Tag", "longitude =>" + lang);
        Log.d("Tag", "uploadLevel =>" + uploadLevel);
        Log.d("Tag", "uploadImg  =>" + imageFilePath);


        if (uri != null) {
            //creating a file
            File file = new File(imageFilePath);


//        AndroidNetworking.post(ServerApiList.BASE_URL + "api/tenderInitialInspectionUpload")
//                .addFileBody(file) // posting any type of file
//                .addPathParameter("tenderId",tenderid)
//                .addPathParameter("userId",userid)
//                .addPathParameter("remark",remarktext)
//                .addPathParameter("latitude",lat)
//                .addPathParameter("longitude",lang)
//                .addPathParameter("uploadLevel",uploadLevel)
//                .setTag("InitiateFileUploading")
//                .setPriority(Priority.MEDIUM)
//                .build()
//                .getAsJSONObject(new JSONObjectRequestListener() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        // do anything with response
//                        Log.i(TAG, "onResponse::::: "+response);
//                    }
//                    @Override
//                    public void onError(ANError error) {
//                        // handle error
//                        Log.i(TAG, "onResponse::::: "+error);
//                    }
//                });


            //creating request body for file
            RequestBody mFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("uploadImg", file.getName(), mFile);

            // RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(selectedImage)), file);
            RequestBody tenderidBody = RequestBody.create(MediaType.parse("text/plain"), tenderid);
            RequestBody useridBody = RequestBody.create(MediaType.parse("text/plain"), userid);
            RequestBody remarkBody = RequestBody.create(MediaType.parse("text/plain"), remarktext);
            RequestBody latBody = RequestBody.create(MediaType.parse("text/plain"), lat);
            RequestBody langBody = RequestBody.create(MediaType.parse("text/plain"), lang);
            RequestBody uploadLevelBody = RequestBody.create(MediaType.parse("text/plain"), uploadLevel);


            progressDialog.setMessage("Uploading");
            progressDialog.show();

            Call<InitiationPhotoUploadResponse> call = webApi.postIntiationUploadPhoto(fileToUpload, tenderidBody, useridBody, remarkBody, latBody, langBody, uploadLevelBody);
            call.enqueue(new Callback<InitiationPhotoUploadResponse>() {
                @Override
                public void onResponse(Call<InitiationPhotoUploadResponse> call, Response<InitiationPhotoUploadResponse> response) {
                    progressDialog.dismiss();
//                boolean status=response.body().isOutcome();

//                if (response.body().isOutcome().equals(""))
//                if(status == true){
                    if (response.isSuccessful() && response.body() != null) {
                        toastMessage(response.body().isOutcome());
                        Intent intent = getIntent();
                        intent.putExtra("data",true);
                        setResult(RESULT_OK,intent);
                        finish();
//                        startActivity(new Intent(InitiationPhotoUploadActivity.this, DashboardActivity.class));
                    }else{
                        Intent intent = getIntent();
                        intent.putExtra("data",false);
                        setResult(RESULT_OK,intent);
                        finish();
                    }
//                }
                }

                @Override
                public void onFailure(Call<InitiationPhotoUploadResponse> call, Throwable t) {
                    progressDialog.dismiss();
                    toastMessage(t.getLocalizedMessage());
                }
            });
        }else {
            Snackbar.make(mLlInitiationRoot,"Please select an Image",Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * This method is fetching the absolute path of the image file
     * if you want to upload other kind of files like .pdf, .docx
     * you need to make changes on this method only
     * Rest part will be the same
     * */
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
