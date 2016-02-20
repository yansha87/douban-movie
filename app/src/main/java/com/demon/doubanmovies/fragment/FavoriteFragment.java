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
import com.demon.doubanmovies.adapter.BaseAdapter;
import com.demon.doubanmovies.adapter.FavoriteAdapter;
import com.demon.doubanmovies.db.bean.SubjectBean;
import com.demon.doubanmovies.utils.DensityUtil;

import java.util.List;

// 收藏
public class FavoriteFragment extends BaseFragment implements BaseAdapter.OnItemClickListener {

    private FavoriteAdapter mAdapter;
    private View mView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = super.onCreateView(inflater, container, savedInstanceState);
        int padding = DensityUtil.dp2px(getContext(), 3f);
        recyclerView.setPadding(padding, padding, padding, padding);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        return mView;
    }

    @Override
    protected void initData() {
        mAdapter = new FavoriteAdapter(getContext());
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        new FavoriteAsyncTask().execute();
    }

    @Override
    protected void initEvent() {
        // mRefreshLayout.setOnRefreshListener(this);
        // disable refresh here
        mRefreshLayout.setEnabled(false);
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(String id, String imageUrl, Boolean isMovie) {
        SubjectActivity.toActivity(getActivity(), id, imageUrl);
    }

    private class FavoriteAsyncTask extends AsyncTask<Void, Void, List<SubjectBean>> {

        @Override
        protected void onPreExecute() {
            // mRefreshLayout.setRefreshing(true);
        }

        @Override
        protected List<SubjectBean> doInBackground(Void... voids) {
            return MovieApplication.getDataSource().getMovieForCollected();
        }

        @Override
        protected void onPostExecute(List<SubjectBean> subjectBeans) {
            mAdapter.update(subjectBeans);
            // mRefreshLayout.setRefreshing(false);
        }
    }
}
