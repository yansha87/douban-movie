package com.demon.doubanmovies;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

import com.demon.doubanmovies.utils.PrefsUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.bugly.crashreport.CrashReport;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MovieApplication extends Application {

    public static Gson gson;
    private static String mCrashReportId = "900019796";

    @Override
    public void onCreate() {
        super.onCreate();
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

        // switch day night mode
        String mode = PrefsUtil.getPrefDayNightMode(this);
        PrefsUtil.switchDayNightMode(mode);
    }

    private void initGson() {
        this.gson = new GsonBuilder().create();
    }

}
