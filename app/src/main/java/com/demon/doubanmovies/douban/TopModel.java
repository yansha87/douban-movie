package com.demon.doubanmovies.douban;

import com.demon.doubanmovies.db.bean.TopBean;

import rx.Observable;

/**
 * Created by user on 2016/2/25.
 */
public class TopModel {
    private static final TopModel instance = new TopModel();

    public static TopModel getInstance() {
        return instance;
    }

    private TopModel() {

    }

    public Observable<TopBean> getTop250() {
        return DoubanMovie.getInstance().getDoubanService().getTop250();
    }
}
