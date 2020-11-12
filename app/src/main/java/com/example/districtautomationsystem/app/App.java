package com.example.districtautomationsystem.app;

import android.app.Application;

import androidx.multidex.MultiDex;

/**
 * Created by Manabendu on 07/08/20
 */
public class App extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
    }
}
