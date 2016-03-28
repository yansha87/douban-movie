package com.demon.doubanmovies.douban;

import com.demon.doubanmovies.model.bean.CnMovieBean;
import com.demon.doubanmovies.model.bean.CelebrityBean;
import com.demon.doubanmovies.model.bean.SimpleSubjectBean;
import com.demon.doubanmovies.model.bean.SubjectBean;
import com.demon.doubanmovies.model.bean.UsMovieBean;
import com.demon.doubanmovies.utils.RxUtil;

import java.util.List;

import rx.Observable;

public class DataManager {
    private static DataManager dataManager;
    private final MovieModel movieModel;

    private DataManager() {
        movieModel = MovieModel.getInstance();
    }

    public synchronized static DataManager getInstance() {
        if (dataManager == null) {
            dataManager = new DataManager();
        }
        return dataManager;
    }

    /**
     * get china movie data at a start index
     *
     * @param type  movie type
     * @param start start index
     * @return Observable object
     */
    public Observable<CnMovieBean> getMovieData(String type, int start) {
        return this.movieModel.getMovieData(type, start)
                .filter(cnMovieBean -> cnMovieBean != null)
                .compose(RxUtil.applyIOToMainThreadSchedulers());
    }

    /**
     * get us box movie data
     *
     * @return Observable object
     */
    public Observable<UsMovieBean> getMovieData() {
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
