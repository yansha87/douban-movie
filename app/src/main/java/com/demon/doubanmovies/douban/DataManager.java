package com.demon.doubanmovies.douban;

import com.demon.doubanmovies.db.bean.CNMovieBean;
import com.demon.doubanmovies.db.bean.USMovieBean;
import com.demon.doubanmovies.utils.RxUtil;

import rx.Observable;

/**
 * Created by user on 2016/2/25.
 */
public class DataManager {
    private static final String TAG = "DataManager";
    private static DataManager dataManager;
    private CNMovieModel cnMovieModel;
    private USMovieModel usMovieModel;

    private DataManager() {
        cnMovieModel = CNMovieModel.getInstance();
        usMovieModel = usMovieModel.getInstance();
    }

    public synchronized static DataManager getInstance() {
        if (dataManager == null) {
            dataManager = new DataManager();
        }
        return dataManager;
    }

    public Observable<CNMovieBean> getMovieData(String type, int start) {
        return this.cnMovieModel.getMovieData(type, start)
                .filter(cnMovieBean -> cnMovieBean != null)
                .compose(RxUtil.applyIOToMainThreadSchedulers());
    }

    public Observable<USMovieBean> getMovieData() {
        return this.usMovieModel.getMovieData()
                .filter(usMovieBean -> usMovieBean != null)
                .compose(RxUtil.applyIOToMainThreadSchedulers());
    }
}
