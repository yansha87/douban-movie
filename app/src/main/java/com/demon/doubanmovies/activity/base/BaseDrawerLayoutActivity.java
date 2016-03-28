package com.demon.doubanmovies.activity.base;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.BuildConfig;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.demon.doubanmovies.R;
import com.demon.doubanmovies.utils.PrefsUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;


public abstract class BaseDrawerLayoutActivity extends BaseToolbarActivity {

    @Bind(R.id.drawer_layout)
    protected DrawerLayout mDrawerLayout;
    @Bind(R.id.nav_view)
    protected NavigationView mNavigationView;

    // save item with MenuItem
    private HashMap<Integer, MenuItem> mMenuItems;
    private ActionBarDrawerToggle mDrawerToggle;
    private TextView mNickname;
    private TextView mSignature;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // active StrictMode when debug
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
        }

        if (this.getNavigationItemSelectedListener() != null) {
            this.mNavigationView.setNavigationItemSelectedListener(this.getNavigationItemSelectedListener());
        }

        this.mDrawerLayout.addDrawerListener(new BaseDrawerListener());

        this.mMenuItems = new HashMap<>();
        int[] menuItemIds = this.getMenuItemIds();

        if (menuItemIds.length > 0) {
            for (int id : menuItemIds) {
                MenuItem menuItem = this.mNavigationView.getMenu().findItem(id);
                if (menuItem != null)
                    this.mMenuItems.put(id, menuItem);
            }
        }

        this.mDrawerToggle = new ActionBarDrawerToggle(this,
                this.mDrawerLayout,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        View v = mNavigationView.getHeaderView(0);
        mNickname = (TextView) v.findViewById(R.id.tv_name);
        mSignature = (TextView) v.findViewById(R.id.tv_sign);
    }

    protected abstract NavigationView.OnNavigationItemSelectedListener getNavigationItemSelectedListener();

    protected abstract int[] getMenuItemIds();

    protected abstract void onMenuItemOnClick(MenuItem now);

    /**
     *
     * @param itemId checked item id
     * @return check new item or not
     */
    protected boolean menuItemChecked(int itemId) {
        MenuItem old = null;
        MenuItem now;

        if (this.mMenuItems.containsKey(itemId)) {
            for (Map.Entry<Integer, MenuItem> entry : this.mMenuItems.entrySet()) {
                MenuItem menuItem = entry.getValue();

                if (menuItem.isChecked()) {
                    old = menuItem;
                }

                if (old != null && old.getItemId() == itemId) {
                    break;
                }

                if (menuItem.getItemId() == itemId) {
                    now = menuItem;
                    menuItem.setChecked(true);
                    this.onMenuItemOnClick(now);
                } else {
                    menuItem.setChecked(false);
                }
            }

            this.mDrawerLayout.closeDrawer(this.mNavigationView);
            return true;
        }

        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK &&
                this.mDrawerLayout.isDrawerOpen(this.mNavigationView)) {
            this.mDrawerLayout.closeDrawer(this.mNavigationView);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    // update navigation content
    private void updateNav() {
        mNickname.setText(PrefsUtil.getPrefNickname(getBaseContext()));
        mSignature.setText(PrefsUtil.getPrefSignature(getBaseContext()));
    }

    private class BaseDrawerListener implements DrawerLayout.DrawerListener {

        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
            BaseDrawerLayoutActivity.this.mDrawerToggle.onDrawerSlide(drawerView, slideOffset);
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            updateNav();

            BaseDrawerLayoutActivity.this.mDrawerToggle.onDrawerOpened(drawerView);
            if (BaseDrawerLayoutActivity.this.mActionBarHelper != null) {
                BaseDrawerLayoutActivity.this.mActionBarHelper.onDrawerOpened();
            }
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            BaseDrawerLayoutActivity.this.mDrawerToggle.onDrawerClosed(drawerView);
            BaseDrawerLayoutActivity.this.mActionBarHelper.onDrawerClosed();
        }

        @Override
        public void onDrawerStateChanged(int newState) {
            BaseDrawerLayoutActivity.this.mDrawerToggle.onDrawerStateChanged(newState);
        }
    }
}
