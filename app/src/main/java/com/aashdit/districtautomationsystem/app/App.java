package com.aashdit.districtautomationsystem.app;

import android.app.Application;

import androidx.multidex.MultiDex;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Manabendu on 07/08/20
 */
public class App extends Application {

    public static double longitude = 0.0, latitude = 0.0;
    public static String capturedAddress = "";

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        Realm.init(this);
        final RealmConfiguration configuration = new RealmConfiguration.Builder()
                .name("default.realm").schemaVersion(0)
                .migration(new RealmMigrations())
                .build();
        Realm.setDefaultConfiguration(configuration);
        Realm.getInstance(configuration);
        // Obtain the FirebaseAnalytics instance.
        FirebaseAnalytics.getInstance(this);
        FirebaseCrashlytics.getInstance();
    }


    @Override
    public void onTerminate() {
        Realm.getDefaultInstance().close();
        super.onTerminate();
    }
}
