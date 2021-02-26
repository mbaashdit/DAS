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

import com.aashdit.districtautomationsystem.Response.ClosureUpload;
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

import io.realm.Realm;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

@SuppressWarnings("All")
public class ClosuretakePhotoUploadActivity extends AppCompatActivity implements View.OnClickListener, LocationListener {
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
    Uri selectedImage, furi;
    Bitmap bmp;
    private ImageView photoIv;
    private EditText remark;
    private Button uploadbtn;
    private WebApi webApi;
    private String tenderid, userid;
    private Uri uri;
    private SharedPrefManager sp;
    private Toolbar toolbar;
    private LinearLayout mLlClosureUpload;
    private Realm realm;
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
        setContentView(R.layout.activity_closuretake_photo_upload);
        realm = Realm.getDefaultInstance();
        mLlClosureUpload = findViewById(R.id.ll_closure_upload_root);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        sp = SharedPrefManager.getInstance(this);
        retrofit = ApiClient.getRetrofit();
        webApi = retrofit.create(WebApi.class);

        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                intent.putExtra("data",false);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        progressDialog = new ProgressDialog(ClosuretakePhotoUploadActivity.this);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        toolbar.setNavigationIcon(R.drawable.ic_back_button);
//        //  setTitle("My Profile");
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                startActivity(new Intent(ClosuretakePhotoUploadActivity.this, PhotoNotUploadListActivity.class));
////                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
////                finish();
//                onBackPressed();
//            }
//        });

        photoIv = findViewById(R.id.photoIv);
        remark = findViewById(R.id.remark);
        uploadbtn = findViewById(R.id.uploadbtn);

        //get tenderid
        tenderid = RegPrefManager.getInstance(ClosuretakePhotoUploadActivity.this).getClosureTenderId();
        userid = String.valueOf(sp.getLongData(Constants.USER_ID));
//        userid = RegPrefManager.getInstance(ClosuretakePhotoUploadActivity.this).getLoginUserid();
        photoIv.setOnClickListener(this);
        uploadbtn.setOnClickListener(this);
        getLocation();

        if (ContextCompat.checkSelfPermission(ClosuretakePhotoUploadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(ClosuretakePhotoUploadActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(ClosuretakePhotoUploadActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                ActivityCompat.requestPermissions(ClosuretakePhotoUploadActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

            } else {

                ActivityCompat.requestPermissions(ClosuretakePhotoUploadActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }

        } else {

        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.photoIv:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (ContextCompat.checkSelfPermission(ClosuretakePhotoUploadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        startDialog();
                    } else {
                        ActivityCompat.requestPermissions(ClosuretakePhotoUploadActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_GALLERY_CADE);
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

                    ClosureUpload closureUpload = new ClosureUpload();
                    closureUpload.imageFilePath = imageFilePath;
                    closureUpload.lat = String.valueOf(latitude);
                    closureUpload.lang = String.valueOf(longitude);
                    closureUpload.tenderid = tenderid;
                    closureUpload.userid = userid;
                    closureUpload.uploadLevel = "CLOSURE";
                    closureUpload.remark = remark.getText().toString();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm.insertOrUpdate(closureUpload);
                        }
                    });

                }

                break;
        }
    }

    private void startDialog() {
        final CharSequence[] items = {"Take Photo",
                "Cancel"};


        AlertDialog.Builder builder = new AlertDialog.Builder(ClosuretakePhotoUploadActivity.this);
        builder.setTitle("Add Photo!");

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Take Photo")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(ClosuretakePhotoUploadActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            openCamera();
                        } else {
                            ActivityCompat.requestPermissions(ClosuretakePhotoUploadActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_CADE);
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

            if (data != null) {
                onSelectFromGalleryResult(data);
            } else {
                Toast.makeText(this, "Null Data", Toast.LENGTH_SHORT).show();
            }

        } else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CAMERA) {
            if (data.hasExtra("data")) {
                selectedImage = data.getData();
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                onCaptureImageResult(bitmap);
            }
        }
    }

    private void onCaptureImageResult(Bitmap bitmap) {
        uri = getImageUri(ClosuretakePhotoUploadActivity.this, bitmap);
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
            Uri furi = getImageUri(ClosuretakePhotoUploadActivity.this, compress);
            //File finalFile = new File(getRealPathFromUri(uri));
            File finalFile = FileUtils.getFile(ClosuretakePhotoUploadActivity.this, furi);
            imageFilePath = finalFile.toString();
            Log.v("imagepath", imageFilePath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Uri getImageUri(ClosuretakePhotoUploadActivity youractivity, Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        String path = MediaStore.Images.Media.insertImage(youractivity.getContentResolver(), bitmap, "IMG_"+ System.currentTimeMillis() /*"Title"*/, null);
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
            Uri furi = getImageUri(ClosuretakePhotoUploadActivity.this, compress);

            photoIv.setImageBitmap(compress);
            //File finalFile = new File(getRealPathFromUri(furi));
            File finalFile = FileUtils.getFile(ClosuretakePhotoUploadActivity.this, furi);
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
        String uploadLevel = "CLOSURE";
        String lat = String.valueOf(latitude);
        String lang = String.valueOf(longitude);

        Log.d("Tag", "tenderid =>" + tenderid);
        Log.d("Tag", "remarktext =>" + remarktext);
        Log.d("Tag", "userid =>" + userid);
        Log.d("Tag", "latitude =>" + lat);
        Log.d("Tag", "longitude =>" + lang);
        Log.d("Tag", "uploadLevel =>" + uploadLevel);
        Log.d("Tag", "uploadImg  =>" + uri);


        if (uri != null) {
            //creating a file
            File file = new File(getRealPathFromURI(uri));

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

            Call<InitiationPhotoUploadResponse> call = webApi.postClosureUploadPhoto(fileToUpload, tenderidBody, useridBody, remarkBody, latBody, langBody, uploadLevelBody);
            call.enqueue(new Callback<InitiationPhotoUploadResponse>() {
                @Override
                public void onResponse(Call<InitiationPhotoUploadResponse> call, Response<InitiationPhotoUploadResponse> response) {
                    progressDialog.dismiss();
//                boolean status = response.body().isOutcome();
//                if (status == true) {
                    if (response.isSuccessful() && response.body() != null) {
                        toastMessage(response.body().isOutcome());
//                        Intent intent = new Intent(ClosuretakePhotoUploadActivity.this, DashboardActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(intent);
                        Intent intent = getIntent();
                        intent.putExtra("data",true);
                        setResult(RESULT_OK,intent);
                        finish();
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
            Snackbar.make(mLlClosureUpload,"Please select an Image",Snackbar.LENGTH_LONG).show();
        }
    }

    /*
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
