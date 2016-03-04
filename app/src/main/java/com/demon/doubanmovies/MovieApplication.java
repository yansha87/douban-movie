package com.demon.doubanmovies;

import android.app.Application;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.bugly.crashreport.CrashReport;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MovieApplication extends Application {

    public static Gson gson;
    private static DisplayImageOptions mLoaderOptions;
    private static String mCrashReportId = "900019796";
    private static MovieApplication instance;

    public static MovieApplication getInstance() {
        return instance;
    }

    public static DisplayImageOptions getLoaderOptions() {
        return mLoaderOptions;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        // Bugly initial
        CrashReport.initCrashReport(getApplicationContext(), mCrashReportId, false);

        // LeakCanary initial
        LeakCanary.install(this);

        // imageloader initial
        initImageLoader(getApplicationContext());

        Realm.setDefaultConfiguration(new RealmConfiguration.Builder(this)
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build());

        initGson();
    }

    private void initGson() {
        this.gson = new GsonBuilder().create();
    }

    public void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.
                Builder(context).
                denyCacheImageMultipleSizesInMemory().
                threadPriority(Thread.NORM_PRIORITY - 2).
                diskCacheFileNameGenerator(new Md5FileNameGenerator()).
                tasksProcessingOrder(QueueProcessingType.FIFO).
                build();
        ImageLoader.getInstance().init(config);
        mLoaderOptions = new DisplayImageOptions.Builder().
                showImageOnLoading(R.drawable.no_image).
                showImageOnFail(R.drawable.no_image).
                showImageForEmptyUri(R.drawable.no_image).
                imageScaleType(ImageScaleType.EXACTLY_STRETCHED).
                cacheInMemory(true).
                cacheOnDisk(true).
                considerExifParams(true).
                build();
    }

}
