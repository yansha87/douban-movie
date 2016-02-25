package com.demon.doubanmovies.douban;


import com.demon.doubanmovies.db.bean.TopBean;

import retrofit.http.GET;
import rx.Observable;

/**
 * Created by user on 2016/2/25.
 */
public interface DoubanService {

    // http://api.douban.com/v2/movie/top250?start=10
    @GET("v2/movie/top250")
    Observable<TopBean> getTop250();
}
