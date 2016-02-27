package com.demon.doubanmovies.douban;

import com.demon.doubanmovies.db.bean.CNMovieBean;
import com.demon.doubanmovies.db.bean.CelebrityBean;
import com.demon.doubanmovies.db.bean.SimpleSubjectBean;
import com.demon.doubanmovies.db.bean.SubjectBean;
import com.demon.doubanmovies.db.bean.USMovieBean;
import com.demon.doubanmovies.utils.RxUtil;

import java.util.List;
import java.util.Objects;

import rx.Observable;

public class DataManager {
    private static final String TAG = "DataManager";
    private static DataManager dataManager;
    private MovieModel movieModel;

    private DataManager() {
        movieModel = MovieModel.getInstance();
    }

    public synchronized static DataManager getInstance() {
        if (dataManager == null) {
            dataManager = new DataManager();
        }
        return dataManager;
    }

    public Observable<CNMovieBean> getMovieData(String type, int start) {
        return this.movieModel.getMovieData(type, start)
                .filter(cnMovieBean -> cnMovieBean != null)
                .compose(RxUtil.applyIOToMainThreadSchedulers());
    }

    public Observable<USMovieBean> getMovieData() {
        return this.movieModel.getMovieData()
                .filter(usMovieBean -> usMovieBean != null)
                .compose(RxUtil.applyIOToMainThreadSchedulers());
    }

    public Observable<CelebrityBean> getCelebrityData(String id) {
        return this.movieModel.getCelebrityData(id)
                .filter(celebrityBean -> celebrityBean != null)
                .compose(RxUtil.applyIOToMainThreadSchedulers());
    }

    public Observable<SubjectBean> getSubjectData(String id) {
        return this.movieModel.getSubjectData(id)
                .filter(subjectBean -> subjectBean != null)
                .compose(RxUtil.applyIOToMainThreadSchedulers());
    }
    public Observable<List<SimpleSubjectBean>> getSearchData(String query) {
        return this.movieModel.getSearchMovieData(query)
                .map(cnMovieBean -> cnMovieBean.subjects)
                .compose(RxUtil.applyIOToMainThreadSchedulers());
    }

    public Observable<List<SimpleSubjectBean>> getRecommendData(String tag) {
        return this.movieModel.getRecommendMovieData(tag)
                .map(cnMovieBean -> cnMovieBean.subjects)
                .compose(RxUtil.applyIOToMainThreadSchedulers());
    }


}
