package com.demon.doubanmovies.douban;


import com.demon.doubanmovies.model.bean.CnMovieBean;
import com.demon.doubanmovies.model.bean.CelebrityBean;
import com.demon.doubanmovies.model.bean.SubjectBean;
import com.demon.doubanmovies.model.bean.UsMovieBean;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;


public interface DoubanService {

    // http://api.douban.com/v2/movie/top250?start={start}
    // http://api.douban.com/v2/movie/in_theaters?start={start}
    // http://api.douban.com/v2/movie/coming_soon?start={start}
    @GET("v2/movie/{type}")
    Observable<CnMovieBean> getCnMovieAPI(
            @Path("type") String type,
            @Query("start") int start
    );

    // http://api.douban.com/v2/movie/us_box
    @GET("v2/movie/us_box")
    Observable<UsMovieBean> getUsMovieAPI();

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
    Observable<CnMovieBean> getSearchMovieAPI(
            @Query("q") String query
    );

    // http://api.douban.com/v2/movie/search?tag={tag}
    @GET("v2/movie/search")
    Observable<CnMovieBean> getRecommendMovieAPI(
            @Query("tag") String tag
    );
}
