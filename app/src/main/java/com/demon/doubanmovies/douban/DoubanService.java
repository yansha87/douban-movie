package com.demon.doubanmovies.douban;


import com.demon.doubanmovies.db.bean.CNMovieBean;
import com.demon.doubanmovies.db.bean.USMovieBean;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by user on 2016/2/25.
 */
public interface DoubanService {

    // http://api.douban.com/v2/movie/top250?start={start}
    // http://api.douban.com/v2/movie/in_theaters?start={start}
    // http://api.douban.com/v2/movie/coming_soon?start={start}
    @GET("v2/movie/{type}")
    Observable<CNMovieBean> getCNMovieAPI(
            @Path("type") String type,
            @Query("start") int start
    );

    // http://api.douban.com/v2/movie/us_box
    @GET("v2/movie/us_box")
    Observable<USMovieBean> getUSMovieAPI();
}
