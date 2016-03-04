package com.demon.doubanmovies;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.bugly.crashreport.CrashReport;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MovieApplication extends Application {

    public static Gson gson;
    private static String mCrashReportId = "900019796";
    private static MovieApplication instance;

    public static MovieApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        // Bugly initial
        CrashReport.initCrashReport(getApplicationContext(), mCrashReportId, false);

        // LeakCanary initial
        LeakCanary.install(this);

        // Realm initial
        Realm.setDefaultConfiguration(new RealmConfiguration.Builder(this)
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build());

        // Gson initial
        initGson();
    }

    private void initGson() {
        this.gson = new GsonBuilder().create();
    }

}
