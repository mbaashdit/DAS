package com.aashdit.districtautomationsystem.app;

import android.app.Application;

import androidx.multidex.MultiDex;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import io.realm.Realm;

/**
 * Created by Manabendu on 07/08/20
 */
public class App extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        Realm.init(this);

        // Obtain the FirebaseAnalytics instance.
        FirebaseAnalytics.getInstance(this);
        FirebaseCrashlytics.getInstance();
    }
}
