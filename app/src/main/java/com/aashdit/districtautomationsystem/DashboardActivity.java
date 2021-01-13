package com.aashdit.districtautomationsystem;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.aashdit.districtautomationsystem.Util.RegPrefManager;
import com.aashdit.districtautomationsystem.Util.SharedPrefManager;
import com.aashdit.districtautomationsystem.fragment.NavigationFragment;
import com.aashdit.districtautomationsystem.fragment.ProjectCloserFragment;
import com.aashdit.districtautomationsystem.fragment.ProjectIntiationFragment;
import com.google.android.material.navigation.NavigationView;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, NavigationFragment.MenuClickListener {
    AlertDialog.Builder builder;
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    private Toolbar toolbar;
    private ImageView image_home;
    private TextView toolbartext;
    private FragmentManager fragmentManager;
    private Fragment fragment = null;
    private SharedPrefManager sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // getSupportActionBar().setDisplayShowTitleEnabled(false);
        setContentView(R.layout.activity_dashboard);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        dl = findViewById(R.id.activity_main);
        image_home = findViewById(R.id.image_home);
        toolbartext = findViewById(R.id.toolbartext);


//        mBtnPhotoNotUploaded = findViewById(R.id.btn_photo_not_uploaded);
//        mBtnPhotoUploaded = findViewById(R.id.btn_photo_uploaded);

        /**
         * Initially Photo Not Uploaded tab is selected. & Photo Uploaded tab is unselected
         * */


        sp = SharedPrefManager.getInstance(this);

        t = new ActionBarDrawerToggle(this, dl, R.string.Open, R.string.Close);
        dl.addDrawerListener(t);
        t.syncState();


        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        nv = findViewById(R.id.navigationView);
        nv.setNavigationItemSelectedListener(this);

        // Set the home as default
        // toolbar.setTitle("Project Intiation");
        fragment = new ProjectIntiationFragment();
        loadFragments(fragment,"INI");
        image_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dl.openDrawer(Gravity.LEFT);
            }
        });


        NavigationFragment navigationFragment = (NavigationFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        if (navigationFragment != null) {
            navigationFragment.setUp(dl, this);
            setDrawerListener();
        }


    }

    private void loadFragments(Fragment fragment,String tag) {
        toolbartext.setText("Project Initiation");
        RegPrefManager.getInstance(DashboardActivity.this).setProjectIntiationOrCloser("Initiation");
        fragmentManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (fragmentManager.findFragmentByTag(tag) == null) {
            fragmentTransaction.add(R.id.frameLayout, fragment,tag);
        }
        fragmentTransaction.commit();
        if (fragment != null) {
            if (this.fragment != null){
                fragmentTransaction.hide(this.fragment);
            }
            if (this.fragment != null){
                fragmentTransaction.hide(this.fragment);
            }

            if (tag.equals("INI")){
                fragmentTransaction.show(this.fragment);
            }
            if (tag.equals("CLOSURE")){
                fragmentTransaction.show(this.fragment);
            }
        }

    }

    private void setDrawerListener() {
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, dl, toolbar, R.string.Open, R.string.Close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

            }
        };
        dl.addDrawerListener(mDrawerToggle);
        dl.post(mDrawerToggle::syncState);
        mDrawerToggle.syncState();
        builder = new AlertDialog.Builder(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        //  Fragment fragment = null ;


        switch (id) {
            case R.id.intial:
                //   toolbar.setTitle("Project Intiation");
                toolbartext.setText("Project Initiation");
                RegPrefManager.getInstance(DashboardActivity.this).setProjectIntiationOrCloser("Initiation");
                fragment = new ProjectIntiationFragment();
                loadFragments(fragment,"INI");
                break;
            case R.id.closer:
                //  toolbar.setTitle("Project Closure");
                toolbartext.setText("Project Closure");
                RegPrefManager.getInstance(DashboardActivity.this).setProjectIntiationOrCloser("Closer");
                fragment = new ProjectCloserFragment();
                loadFragments(fragment,"CLOSURE");
                break;
            case R.id.changepassword:
                startActivity(new Intent(DashboardActivity.this, ChangePasswordActivity.class));
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                break;
            case R.id.logout:

                break;


        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_main);
        //  assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);

        return true;

    }

    private void showLogoutDialog() {
        builder.setMessage("Do you want to close this application ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//                        RegPrefManager.getInstance(DashboardActivity.this).clearData();
//                        Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
//                        startActivity(intent);
//                        finish();
                        RegPrefManager.getInstance(DashboardActivity.this).clearData();
                        sp.clear();
                        Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Exit ?");
        alert.show();
    }

//    @Override
//    public void onBackPressed() {
//        //this is only needed if you have specific things
//        //that you want to do when the user presses the back button.
//        /* your specific things...*/
//        super.onBackPressed();
//        Intent a = new Intent(Intent.ACTION_MAIN);
//        a.addCategory(Intent.CATEGORY_HOME);
//        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(a);
//
//    }

    @Override
    public void onClickMenuItem(int position) {
        dl.closeDrawer(GravityCompat.START);

        if (position == 0) {
            toolbartext.setText("Project Initiation");
            RegPrefManager.getInstance(DashboardActivity.this).setProjectIntiationOrCloser("Initiation");
            fragment = new ProjectIntiationFragment();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.frameLayout, fragment);
            transaction.commit();
        }

        if (position == 1) {
            toolbartext.setText("Project Closure");
            RegPrefManager.getInstance(DashboardActivity.this).setProjectIntiationOrCloser("Closer");
            fragment = new ProjectCloserFragment();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.frameLayout, fragment);
            transaction.commit();
        }
        if (position == 2) {
            startActivity(new Intent(DashboardActivity.this, ChangePasswordActivity.class));
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        }
        if (position == 3) {


            showLogoutDialog();

        }


    }


    @Override
    public void onBackPressed() {

        if (dl.isDrawerOpen(GravityCompat.START)) {
            dl.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

}



