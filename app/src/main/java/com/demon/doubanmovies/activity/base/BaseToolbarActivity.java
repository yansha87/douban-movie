package com.demon.doubanmovies.activity.base;


import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.demon.doubanmovies.R;

import butterknife.Bind;

public abstract class BaseToolbarActivity extends BaseAppCompatActivity {

    @Bind(R.id.toolbar)
    protected Toolbar mToolbar;
    @Bind(R.id.app_bar_layout)
    protected AppBarLayout mAppBarLayout;

    protected ActionBarHelper mActionBarHelper;

    @Override
    protected void initToolbar(Bundle savedInstanceState) {
        this.initToolbarHelper();
    }

    private void initToolbarHelper() {
        if (this.mToolbar == null || this.mAppBarLayout == null)
            return;

        this.setSupportActionBar(this.mToolbar);

        this.mActionBarHelper = this.createActionBarHelper();
        this.mActionBarHelper.init();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            this.mAppBarLayout.setElevation(6f);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("unused")
    protected void showBack() {
        if (this.mActionBarHelper != null)
            this.mActionBarHelper.setDisplayHomeAsUpEnable(true);
    }

    @SuppressWarnings("unused")
    public void setAppBarLayoutAlpha(float alpha) {
        this.mAppBarLayout.setAlpha(alpha);
    }

    @SuppressWarnings("unused")
    protected void setAppBarLayoutVisibility(boolean visibility) {
        if (visibility) {
            this.mAppBarLayout.setVisibility(View.VISIBLE);
        } else {
            this.mAppBarLayout.setVisibility(View.GONE);
        }
    }

    private ActionBarHelper createActionBarHelper() {
        return new ActionBarHelper();
    }

    /**
     * A helper class to operator toolbar
     */
    public class ActionBarHelper {
        private final ActionBar mActionBar;

        public CharSequence mDrawerTitle;
        public CharSequence mTitle;

        public ActionBarHelper() {
            this.mActionBar = getSupportActionBar();
        }

        public void init() {
            if (this.mActionBar == null) return;
            this.mActionBar.setDisplayHomeAsUpEnabled(true);
            this.mActionBar.setDisplayShowHomeEnabled(false);
            this.mTitle = mDrawerTitle = getString(R.string.nav_home);
            // set nav_home string as title when app setup
            this.mActionBar.setTitle(this.mTitle);
        }

        public void onDrawerClosed() {
            if (this.mActionBar == null) return;
            this.mActionBar.setTitle(this.mTitle);
        }

        public void onDrawerOpened() {
            if (this.mActionBar == null) return;
            this.mActionBar.setTitle("");
        }

        public void setTitle(CharSequence mTitle) {
            this.mTitle = mTitle;
            this.mActionBar.setTitle(mTitle);
        }

        @SuppressWarnings("unused")
        public void setDrawerTitle(CharSequence mDrawerTitle) {
            this.mDrawerTitle = mDrawerTitle;
        }

        public void setDisplayHomeAsUpEnable(boolean showHomeAsUp) {
            if (this.mActionBar == null) return;
            this.mActionBar.setDisplayShowHomeEnabled(showHomeAsUp);
        }
    }


}
