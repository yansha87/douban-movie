package com.demon.doubanmovies.douban;

import android.util.Log;

import com.demon.doubanmovies.MovieApplication;
import com.demon.doubanmovies.utils.Constant;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Response;

import java.util.concurrent.TimeUnit;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

public class DoubanMovie {

    private static final String TAG = "DoubanMovie";
    private static DoubanMovie instance;
    private final DoubanService doubanService;

    private DoubanMovie() {
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(8000, TimeUnit.MILLISECONDS);

        okHttpClient.interceptors().add(chain -> {
            Response response = chain.proceed(chain.request());
            Log.i(TAG, "DoubanMovie url: " + response.request().urlString());
            return response;
        });

        // initial Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.API)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(MovieApplication.gson))
                .client(okHttpClient)
                .build();

        this.doubanService = retrofit.create(DoubanService.class);
    }

    public synchronized static DoubanMovie getInstance() {
        if (instance == null)
            instance = new DoubanMovie();
        return instance;
    }

    public DoubanService getDoubanService() {
        return doubanService;
    }
}
