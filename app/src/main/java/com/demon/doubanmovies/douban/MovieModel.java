package com.demon.doubanmovies.douban;

import com.demon.doubanmovies.model.bean.CnMovieBean;
import com.demon.doubanmovies.model.bean.CelebrityBean;
import com.demon.doubanmovies.model.bean.SubjectBean;
import com.demon.doubanmovies.model.bean.UsMovieBean;

import rx.Observable;


public class MovieModel {
    private static final MovieModel instance = new MovieModel();

    // initial instance
    public static MovieModel getInstance() {
        return instance;
    }

    private MovieModel() {
    }

    public Observable<CnMovieBean> getMovieData(String type, int start) {
        return DoubanMovie.getInstance().getDoubanService().getCnMovieAPI(type, start);
    }

    public Observable<UsMovieBean> getMovieData() {
        return DoubanMovie.getInstance().getDoubanService().getUsMovieAPI();
    }

    public Observable<CelebrityBean> getCelebrityData(String id) {
        return DoubanMovie.getInstance().getDoubanService().getCelebrityAPI(id);
    }

    public Observable<SubjectBean> getSubjectData(String id) {
        return DoubanMovie.getInstance().getDoubanService().getSubjectAPI(id);
    }

    public Observable<CnMovieBean> getSearchMovieData(String query) {
        return DoubanMovie.getInstance().getDoubanService().getSearchMovieAPI(query);
    }

    public Observable<CnMovieBean> getRecommendMovieData(String tag) {
        return DoubanMovie.getInstance().getDoubanService().getRecommendMovieAPI(tag);
    }

}
