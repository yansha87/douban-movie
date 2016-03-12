package com.demon.doubanmovies.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.demon.doubanmovies.MovieApplication;
import com.demon.doubanmovies.activity.SubjectActivity;
import com.demon.doubanmovies.adapter.FavoriteAdapter;
import com.demon.doubanmovies.fragment.base.BaseFragment;
import com.demon.doubanmovies.model.bean.SubjectBean;
import com.demon.doubanmovies.model.realm.SimpleSubject;
import com.demon.doubanmovies.utils.Constant;
import com.demon.doubanmovies.utils.DensityUtil;
import com.demon.doubanmovies.utils.RealmUtil;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;

/**
 * favorite fragment
 */
public class FavoriteFragment extends BaseFragment {

    private FavoriteAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = super.onCreateView(inflater, container, savedInstanceState);
        int padding = DensityUtil.dp2px(getContext(), 3f);
        recyclerView.setPadding(padding, padding, padding, padding);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        return mView;
    }

    @Override
    protected void initData() {
        mAdapter = new FavoriteAdapter(recyclerView, null);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        new FavoriteAsyncTask().execute();
    }

    @Override
    protected void initEvent() {
        // disable refresh here
        mRefreshLayout.setEnabled(false);
        mAdapter.setOnItemClickListener((String id, String imageUrl, Boolean isMovie) -> {
            SubjectActivity.toActivity(getActivity(), id, imageUrl);
        });
    }


    /**
     * AsyncTask to load cache data
     */
    private class FavoriteAsyncTask extends AsyncTask<Void, Void, List<SubjectBean>> {

        @Override
        protected List<SubjectBean> doInBackground(Void... voids) {

            RealmResults<SimpleSubject> subjects = RealmUtil.queryRecord(Constant.SIMPLE_SUBJECT_FOR,
                    Constant.SAVE_FAVORITE);
            List<SubjectBean> beanList = new ArrayList<>();
            for (SimpleSubject subject : subjects) {
                SubjectBean bean = MovieApplication.gson.fromJson(subject.getJsonStr(), Constant.subType);
                beanList.add(bean);
            }
            return beanList;
        }

        @Override
        protected void onPostExecute(List<SubjectBean> subjectBeans) {
            mAdapter.update(subjectBeans);
        }
    }
}
