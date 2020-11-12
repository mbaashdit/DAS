package com.example.districtautomationsystem.Adapter;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.districtautomationsystem.R;
import com.example.districtautomationsystem.fragment.PhotoNotUploadedFragment;
import com.example.districtautomationsystem.fragment.PhotoUploadedFragment;

public class TabsPagerAdapter extends FragmentPagerAdapter {
    private static final int FRAGMENT_COUNT = 2;
    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new PhotoNotUploadedFragment();
            case 1:
                return new PhotoUploadedFragment();
        }
        return null;
    }
    @Override
    public int getCount() {
        return FRAGMENT_COUNT;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Photo Not Uploaded";
            case 1:
                return "Photo Uploaded";

        }
        return null;
    }

}
