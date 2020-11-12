package com.example.districtautomationsystem.Adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.districtautomationsystem.fragment.PhotoNotUploadedClosureFragment;
import com.example.districtautomationsystem.fragment.PhotoNotUploadedFragment;
import com.example.districtautomationsystem.fragment.PhotoUploadedClosureFragment;
import com.example.districtautomationsystem.fragment.PhotoUploadedFragment;

public class TabsPagerCloserAdapter extends FragmentPagerAdapter {
    private static final int FRAGMENT_COUNT = 2;
    public TabsPagerCloserAdapter(FragmentManager fm) {
        super(fm);
    }
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new PhotoNotUploadedClosureFragment();
            case 1:
                return new PhotoUploadedClosureFragment();
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
