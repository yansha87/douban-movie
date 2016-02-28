package com.demon.doubanmovies;

import android.app.Application;
import android.content.Context;

import com.demon.doubanmovies.db.DataSource;
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

import java.sql.SQLException;

public class MovieApplication extends Application {

    public static Gson gson;
    private static DisplayImageOptions mLoaderOptions;
    private static DataSource mSource;
    private static String mCrashReportId = "900019796";
    private static MovieApplication instance = new MovieApplication();

    public static MovieApplication getInstance() {
        return instance;
    }

    public static DataSource getDataSource() {
        return mSource;
    }

    public static DisplayImageOptions getLoaderOptions() {
        return mLoaderOptions;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        // Bugly initial
        CrashReport.initCrashReport(getApplicationContext(), mCrashReportId, false);

        // LeakCanary initial
        LeakCanary.install(this);

        // imageloader initial
        initImageLoader(getApplicationContext());
        mSource = new DataSource(getApplicationContext());
        try {
            mSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

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
