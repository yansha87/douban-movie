package com.demon.doubanmovies.douban;

import com.demon.doubanmovies.db.bean.CNMovieBean;
import com.demon.doubanmovies.db.bean.USMovieBean;

import rx.Observable;

/**
 * Created by user on 2016/2/25.
 */
public class USMovieModel {
    private static final USMovieModel instance = new USMovieModel();

    private USMovieModel() {

    }

    public static USMovieModel getInstance() {
        return instance;
    }

    public Observable<USMovieBean> getMovieData() {
        return DoubanMovie.getInstance().getDoubanService().getUSMovieAPI();
    }
}
