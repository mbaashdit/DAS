package com.aashdit.districtautomationsystem.app;

import android.util.Log;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class RealmMigrations implements RealmMigration {

    private static final String TAG = "RealmMigrations";

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
//        final RealmSchema schema = realm.getSchema();

        Log.i(TAG, "migrate: oldVersion " + oldVersion + " ::::newVersion " + newVersion);
        try {
            updateRealmSchema(realm, oldVersion, newVersion);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected void upgradeFrom_0(RealmSchema schema) {

    }

    protected void upgradeSchema(RealmSchema schema, long fromVersion) {
//        Log.i(TAG, "UPGRADE REALM SCHEMA >> " + fromVersion + " <<");
//        if (fromVersion == 0) {
//            upgradeFrom_0(schema);
//        }
    }

    protected void upgradeFrom_2(RealmSchema schema) {

    }
    protected void upgradeFrom_3(RealmSchema schema) {

    }
    protected void upgradeFrom_4(RealmSchema schema) {

    }
    protected void upgradeFrom_5(RealmSchema schema) {

    }

    protected void updateRealmSchema(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();

        if (schema != null) {
            Log.i(TAG, "schema IN");

//            for (long ver = oldVersion; ver <= newVersion; ver++) {
//                try {
//                    upgradeSchema(schema, ver);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
        }
    }
}
