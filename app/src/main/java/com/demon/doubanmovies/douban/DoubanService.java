package com.demon.doubanmovies.douban;


import com.demon.doubanmovies.db.bean.CNMovieBean;
import com.demon.doubanmovies.db.bean.CelebrityBean;
import com.demon.doubanmovies.db.bean.SimpleSubjectBean;
import com.demon.doubanmovies.db.bean.SubjectBean;
import com.demon.doubanmovies.db.bean.USMovieBean;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;


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

    // http://api.douban.com/v2/movie/celebrity/{id}
    @GET("v2/movie/celebrity/{id}")
    Observable<CelebrityBean> getCelebrityAPI(
            @Path("id") String id
    );

    // http://api.douban.com/v2/movie/subject/{id}
    @GET("v2/movie/subject/{id}")
    Observable<SubjectBean> getSubjectAPI(
            @Path("id") String id
    );

    // http://api.douban.com/v2/movie/search?q={query}
    @GET("v2/movie/search")
    Observable<CNMovieBean> getSearchMovieAPI(
            @Query("q") String query
    );

    // http://api.douban.com/v2/movie/search?tag={tag}
    @GET("v2/movie/search")
    Observable<CNMovieBean> getRecommendMovieAPI(
            @Query("tag") String tag
    );
}
