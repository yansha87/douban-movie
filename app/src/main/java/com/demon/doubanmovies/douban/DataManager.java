package com.demon.doubanmovies.douban;

import android.util.Log;

import com.demon.doubanmovies.db.bean.SimpleSubjectBean;
import com.demon.doubanmovies.db.bean.TopBean;
import com.demon.doubanmovies.utils.RxUtil;

import java.util.List;

import rx.Subscriber;

/**
 * Created by user on 2016/2/25.
 */
public class DataManager {
    private static final String TAG = "DataManager";
    private static DataManager dataManager;
    private TopModel topModel;

    private DataManager() {
        topModel = TopModel.getInstance();
    }

    public synchronized static DataManager getInstance() {
        if (dataManager == null) {
            dataManager = new DataManager();
        }
        return dataManager;
    }

    public void getTop250ByNetwork(int start) {
        this.topModel.getTop250()
                .compose(RxUtil.applyIOToMainThreadSchedulers())
                .map(topBean -> {
                    return topBean.subjects;
                })
                .subscribe(new Subscriber<List<SimpleSubjectBean>>() {
                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onNext(List<SimpleSubjectBean> simpleSubjectBeans) {
                        for (SimpleSubjectBean simpleSubjectBean : simpleSubjectBeans) {
                            Log.i(TAG, "onNext: " + simpleSubjectBean.title);
                        }
                    }
                });
    }
}
