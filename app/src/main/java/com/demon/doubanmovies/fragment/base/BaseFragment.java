package com.demon.doubanmovies.fragment.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.demon.doubanmovies.R;
import com.demon.doubanmovies.utils.DensityUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BaseFragment extends Fragment {

    @Bind(R.id.rv_fragment)
    protected RecyclerView recyclerView;
    @Bind(R.id.fresh_fragment)
    protected SwipeRefreshLayout mRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base, container, false);
        ButterKnife.bind(this, view);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mRefreshLayout.setProgressViewOffset(false, 0, DensityUtil.dp2px(getContext(), 32f));
        initData();
        initEvent();
        return view;
    }


    /**
     * initial data
     */
    protected void initData() {

    }


    /**
     * initial event listener
     */
    protected void initEvent() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
