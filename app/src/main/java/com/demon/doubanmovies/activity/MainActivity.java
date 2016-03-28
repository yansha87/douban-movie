package com.demon.doubanmovies.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;

import com.demon.doubanmovies.R;
import com.demon.doubanmovies.activity.base.BaseDrawerLayoutActivity;
import com.demon.doubanmovies.fragment.FavoriteFragment;
import com.demon.doubanmovies.fragment.HomeFragment;
import com.demon.doubanmovies.fragment.SettingFragment;
import com.demon.doubanmovies.fragment.base.BaseFragment;
import com.demon.doubanmovies.utils.Constant;

import java.util.List;

public class MainActivity extends BaseDrawerLayoutActivity {

    public static final String ACTION_LOCAL_SEND = "action.local.send";
    private static final String SAVE_STATE_TITLE = "title";
    private static String mTitle;
    private FragmentManager mFragmentManager;
    private Fragment mCurFragment;

    // local receiver to receive setting broadcast
    private LocalBroadcastReceiver localReceiver = new LocalBroadcastReceiver();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        String title = null;
        if (savedInstanceState != null) {
            title = savedInstanceState.getString(SAVE_STATE_TITLE);
        }
        if (title == null) {
            title = getString(R.string.nav_home);
        }

        // clear other fragments when switch day & night mode
        removeFragment(title);

        mCurFragment = mFragmentManager.findFragmentByTag(title);
        if (mCurFragment == null) {
            Fragment homeFragment = new HomeFragment();
            mFragmentManager.beginTransaction().
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).
                    add(R.id.rl_main_container, homeFragment, title).commit();
            mCurFragment = homeFragment;
        }
    }

    private void removeFragment(String title) {
        mFragmentManager = getSupportFragmentManager();
        List<Fragment> fragments = mFragmentManager.getFragments();
        if (fragments == null) {
            return;
        }

        // retain current fragment
        for (Fragment fragment : fragments) {
            if (fragment == null || fragment.getTag().equals(title))
                continue;

            mFragmentManager.beginTransaction().remove(fragment).commit();
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
        LocalBroadcastManager.getInstance(this).registerReceiver(localReceiver,
                new IntentFilter(ACTION_LOCAL_SEND));
    }

    @Override
    protected void initDatas() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(localReceiver);
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

        if (Constant.menuId2TitleDict.get(now.getItemId()) != null) {
            mActionBarHelper.setTitle(Constant.menuId2TitleDict.get(now.getItemId()));
            mTitle = Constant.menuId2TitleDict.get(now.getItemId());
            this.switchFragment(mTitle);
        }
    }

    /**
     * switch fragment by title
     * @param title fragment title
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
     * create fragment by title
     * @param title fragment title
     * @return fragment
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVE_STATE_TITLE, mTitle);
    }

    public class LocalBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // recreate activity
            recreate();
        }
    }
}
