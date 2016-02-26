package com.demon.doubanmovies.douban;

import com.demon.doubanmovies.db.bean.CNMovieBean;

import rx.Observable;

/**
 * Created by user on 2016/2/25.
 */
public class CNMovieModel {
    private static final CNMovieModel instance = new CNMovieModel();

    public static CNMovieModel getInstance() {
        return instance;
    }

    private CNMovieModel() {

    }

    public Observable<CNMovieBean> getMovieData(String type, int start) {
        return DoubanMovie.getInstance().getDoubanService().getCNMovieAPI(type, start);
    }
}
