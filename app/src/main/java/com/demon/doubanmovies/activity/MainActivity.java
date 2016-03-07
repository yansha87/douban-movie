package com.demon.doubanmovies.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.demon.doubanmovies.R;
import com.demon.doubanmovies.activity.base.BaseDrawerLayoutActivity;
import com.demon.doubanmovies.fragment.base.BaseFragment;
import com.demon.doubanmovies.fragment.FavoriteFragment;
import com.demon.doubanmovies.fragment.HomeFragment;
import com.demon.doubanmovies.fragment.SettingFragment;
import com.demon.doubanmovies.utils.Constant;

public class MainActivity extends BaseDrawerLayoutActivity {

    private FragmentManager mFragmentManager;
    private Fragment mCurFragment;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        String title = getString(R.string.nav_home);
        mFragmentManager = getSupportFragmentManager();
        mCurFragment = mFragmentManager.findFragmentByTag(title);
        if (mCurFragment == null) {
            Fragment homeFragment = new HomeFragment();
            mFragmentManager.beginTransaction().
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).
                    add(R.id.rl_main_container, homeFragment, title).commit();
            mCurFragment = homeFragment;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search:
                prepareIntent(SearchActivity.class);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void prepareIntent(Class clazz) {
        this.startActivity(new Intent(MainActivity.this, clazz));
    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void initData() {
        // SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        // String imageSize = sp.getString("image_size", "");
        // String language = sp.getString("language", "");
    }

    @Override
    protected NavigationView.OnNavigationItemSelectedListener getNavigationItemSelectedListener() {
        return item -> MainActivity.this.menuItemChecked(item.getItemId());
    }

    @Override
    protected int[] getMenuItemIds() {
        return Constant.menuIds;
    }

    @Override
    protected void onMenuItemOnClick(MenuItem now) {

        if (Constant.menuId2TitleDict.containsKey(now.getItemId())) {
            mActionBarHelper.setTitle(Constant.menuId2TitleDict.get(now.getItemId()));
            this.switchFragment(Constant.menuId2TitleDict.get(now.getItemId()));
        }
    }

    /**
     * 判断各种逻辑下的fragment显示问题
     */
    private void switchFragment(String title) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        Fragment fragment = mFragmentManager.findFragmentByTag(title);
        if (fragment == null) {
            transaction.hide(mCurFragment);
            fragment = createFragmentByTitle(title);
            transaction.add(R.id.rl_main_container, fragment, title);
            mCurFragment = fragment;
        } else if (fragment != mCurFragment) {
            transaction.hide(mCurFragment).show(fragment);
            mCurFragment = fragment;
        }
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).
                commit();
        supportInvalidateOptionsMenu();
    }

    /**
     * 根据menuItem的title返回对应的fragment
     */
    private Fragment createFragmentByTitle(String title) {
        switch (title) {
            case Constant.HOMEPAGE:
                return new HomeFragment();
            case Constant.FAVORITE:
                return new FavoriteFragment();
            case Constant.SETTING:
                return new SettingFragment();
            default:
                return new BaseFragment();
        }
    }
}
