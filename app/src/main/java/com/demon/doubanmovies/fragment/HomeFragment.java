package com.demon.doubanmovies.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.demon.doubanmovies.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HomeFragment extends Fragment {

    private final static String[] TITLES = {"正在热映", "即将上映", "高分电影", "北美榜单"};

    @Bind(R.id.tab_home)
    TabLayout mTabLayout;
    @Bind(R.id.vp_home)
    ViewPager mViewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        initData();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void initData() {
        PagerAdapter mPagerAdapter = new HomePagerAdapter(getChildFragmentManager());
        mViewPager.setOffscreenPageLimit(TITLES.length);
        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mTabLayout.setTabTextColors(Color.GRAY, ContextCompat.getColor(getActivity(), R.color.blue_500));
        mTabLayout.setupWithViewPager(mViewPager);
    }

    class HomePagerAdapter extends FragmentStatePagerAdapter {

        public HomePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return HomePagerFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }
    }

}
