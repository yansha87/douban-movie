package com.demon.doubanmovies.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.demon.doubanmovies.MovieApplication;
import com.demon.doubanmovies.R;
import com.demon.doubanmovies.activity.SubjectActivity;
import com.demon.doubanmovies.adapter.CollectAdapter;
import com.demon.doubanmovies.db.bean.SubjectBean;
import com.demon.doubanmovies.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

// 收藏
public class CollectFragment extends BaseFragment
        implements OnRefreshListener,
        CollectAdapter.OnItemClickListener {

    private List<SubjectBean> mData = new ArrayList<>();
    private CollectAdapter mAdapter;
    private View mView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = super.onCreateView(inflater, container, savedInstanceState);
        int padding = DensityUtil.dp2px(getContext(), 4f);
        mRecView.setPadding(padding, padding, padding, padding);
        return mView;
    }

    @Override
    protected void initData() {
        mAdapter = new CollectAdapter(getContext());
        mRecView.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        new MyAsyncTask().execute();
    }

    @Override
    protected void initEvent() {
        mRefreshLayout.setOnRefreshListener(this);
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onRefresh() {
        mRefreshLayout.setRefreshing(true);
        new MyAsyncTask().execute();
    }

    @Override
    public void itemClick(String id, String imageUrl) {
        SubjectActivity.toActivity(getActivity(), id, imageUrl);
    }

    @Override
    public void itemRemove(final int pos, final String id) {
        Snackbar.make(mView, getString(R.string.cancel), Snackbar.LENGTH_LONG).
                setAction(getString(R.string.ok),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                MovieApplication.getDataSource().deleteFilm(id);
                            }
                        }).
                setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
                        if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                            mAdapter.cancelRemove(pos);
                        }
                    }

                    @Override
                    public void onShown(Snackbar snackbar) {
                        super.onShown(snackbar);
                    }
                }).show();
    }

    private class MyAsyncTask extends AsyncTask<Void, Void, List<SubjectBean>> {

        @Override
        protected void onPreExecute() {
            mRefreshLayout.setRefreshing(true);
        }

        @Override
        protected List<SubjectBean> doInBackground(Void... voids) {
            return MovieApplication.getDataSource().getFilmForCollected();
        }

        @Override
        protected void onPostExecute(List<SubjectBean> subjectBeans) {
            mAdapter.update(subjectBeans);
            mRefreshLayout.setRefreshing(false);
        }
    }
}
