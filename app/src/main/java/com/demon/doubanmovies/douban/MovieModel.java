package com.demon.doubanmovies.douban;

import com.demon.doubanmovies.model.bean.CNMovieBean;
import com.demon.doubanmovies.model.bean.CelebrityBean;
import com.demon.doubanmovies.model.bean.SubjectBean;
import com.demon.doubanmovies.model.bean.USMovieBean;

import rx.Observable;


public class MovieModel {
    private static final MovieModel instance = new MovieModel();

    // initial instance
    public static MovieModel getInstance() {
        return instance;
    }

    private MovieModel() {

    }

    public Observable<CNMovieBean> getMovieData(String type, int start) {
        return DoubanMovie.getInstance().getDoubanService().getCNMovieAPI(type, start);
    }

    public Observable<USMovieBean> getMovieData() {
        return DoubanMovie.getInstance().getDoubanService().getUSMovieAPI();
    }

    public Observable<CelebrityBean> getCelebrityData(String id) {
        return DoubanMovie.getInstance().getDoubanService().getCelebrityAPI(id);
    }

    public Observable<SubjectBean> getSubjectData(String id) {
        return DoubanMovie.getInstance().getDoubanService().getSubjectAPI(id);
    }

    public Observable<CNMovieBean> getSearchMovieData(String query) {
        return DoubanMovie.getInstance().getDoubanService().getSearchMovieAPI(query);
    }

    public Observable<CNMovieBean> getRecommendMovieData(String tag) {
        return DoubanMovie.getInstance().getDoubanService().getRecommendMovieAPI(tag);
    }

}
