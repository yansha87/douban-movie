package com.demon.doubanmovies.activity;

import android.app.SharedElementCallback;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;

import com.demon.doubanmovies.R;
import com.demon.doubanmovies.fragment.BaseFragment;
import com.demon.doubanmovies.fragment.FavoriteFragment;
import com.demon.doubanmovies.fragment.HomeFragment;
import com.demon.doubanmovies.utils.StringUtil;

import java.util.List;
import java.util.Map;

import static com.demon.doubanmovies.utils.StringUtil.RADIOHEAD_ALBUM_NAMES;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String EXTRA_CURRENT_ITEM_POSITION = "extra_current_item_position";
    public static final String EXTRA_OLD_ITEM_POSITION = "extra_old_item_position";
    private static final String TAG = "MainActivity";
    private static final boolean DEBUG = true;
    private RecyclerView mRecyclerView;
    private Bundle mTmpState;
    private boolean mIsReentering;
    private final SharedElementCallback mCallback = new SharedElementCallback() {
        @Override
        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            LOG("onMapSharedElements(List<String>, Map<String, View>)", mIsReentering);
            if (mIsReentering) {
                int oldPosition = mTmpState.getInt(EXTRA_OLD_ITEM_POSITION);
                int currentPosition = mTmpState.getInt(EXTRA_CURRENT_ITEM_POSITION);
                if (currentPosition != oldPosition) {
                    // If currentPosition != oldPosition the user must have swiped to a different
                    // page in the DetailsActivity. We must update the shared element so that the
                    // correct one falls into place.
                    String newTransitionName = RADIOHEAD_ALBUM_NAMES[currentPosition];
                    View newSharedView = mRecyclerView.findViewWithTag(newTransitionName);
                    if (newSharedView != null) {
                        names.clear();
                        names.add(newTransitionName);
                        sharedElements.clear();
                        sharedElements.put(newTransitionName, newSharedView);
                    }
                }
                mTmpState = null;
            }

            if (!mIsReentering) {
                View navigationBar = findViewById(android.R.id.navigationBarBackground);
                View statusBar = findViewById(android.R.id.statusBarBackground);
                int actionBarId = getResources().getIdentifier("action_bar_container", "id", "android");
                View actionBar = findViewById(actionBarId);

                if (navigationBar != null) {
                    names.add(navigationBar.getTransitionName());
                    sharedElements.put(navigationBar.getTransitionName(), navigationBar);
                }
                if (statusBar != null) {
                    names.add(statusBar.getTransitionName());
                    sharedElements.put(statusBar.getTransitionName(), statusBar);
                }
                if (actionBar != null) {
                    actionBar.setTransitionName("actionBar");
                    names.add(actionBar.getTransitionName());
                    sharedElements.put(actionBar.getTransitionName(), actionBar);
                }
            } else {
                names.remove(Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME);
                sharedElements.remove(Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME);
                names.remove(Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME);
                sharedElements.remove(Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME);
                names.remove("actionBar");
                sharedElements.remove("actionBar");
            }

            LOG("=== names: " + names.toString(), mIsReentering);
            LOG("=== sharedElements: " + StringUtil.setToString(sharedElements.keySet()), mIsReentering);
        }

        @Override
        public void onSharedElementStart(List<String> sharedElementNames, List<View> sharedElements,
                                         List<View> sharedElementSnapshots) {
            LOG("onSharedElementStart(List<String>, List<View>, List<View>)", mIsReentering);
            logSharedElementsInfo(sharedElementNames, sharedElements);
        }

        @Override
        public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements,
                                       List<View> sharedElementSnapshots) {
            LOG("onSharedElementEnd(List<String>, List<View>, List<View>)", mIsReentering);
            logSharedElementsInfo(sharedElementNames, sharedElements);
        }

        private void logSharedElementsInfo(List<String> names, List<View> sharedElements) {
            LOG("=== names: " + names.toString(), mIsReentering);
            for (View view : sharedElements) {
                int[] loc = new int[2];
                view.getLocationInWindow(loc);
                Log.i(TAG, "=== " + view.getTransitionName() + ": " + "(" + loc[0] + ", " + loc[1] + ")");
            }
        }
    };
    private String mTitle;
    private FragmentManager mFragmentManager;
    private Fragment mCurFragment;

    private static void LOG(String message, boolean isReentering) {
        if (DEBUG) {
            Log.i(TAG, String.format("%s: %s", isReentering ? "REENTERING" : "EXITING", message));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setExitSharedElementCallback(mCallback);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitle = getString(R.string.nav_home);
        toolbar.setTitle(mTitle);
        setSupportActionBar(toolbar);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                Intent intent = new Intent(MainActivity.this, FlickrSearchActivity.class);
                startActivity(intent);
            }
        });
        */

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mFragmentManager = getSupportFragmentManager();
        mCurFragment = mFragmentManager.findFragmentByTag(mTitle);
        if (mCurFragment == null) {
            Fragment homeFragment = new HomeFragment();
            mFragmentManager.beginTransaction().
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).
                    add(R.id.rl_main_container, homeFragment, mTitle).commit();
            mCurFragment = homeFragment;
        }
    }

    @Override
    public void onActivityReenter(int requestCode, Intent data) {
        LOG("onActivityReenter(int, Intent)", true);
        super.onActivityReenter(requestCode, data);
        mIsReentering = true;
        mTmpState = new Bundle(data.getExtras());
        int oldPosition = mTmpState.getInt(EXTRA_OLD_ITEM_POSITION);
        int currentPosition = mTmpState.getInt(EXTRA_CURRENT_ITEM_POSITION);
        if (oldPosition != currentPosition) {
            mRecyclerView.scrollToPosition(currentPosition);
        }
        postponeEnterTransition();
        mRecyclerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mRecyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                // TODO: hack! not sure why, but requesting a layout pass is necessary in order to fix re-mapping + scrolling glitches!
                mRecyclerView.requestLayout();
                startPostponedEnterTransition();
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        /* MenuItem setting = menu.findItem(R.id.action_settings);
        if (mTitle.equals(getString(R.string.nav_home))) {
            setting.setVisible(true);
        } else {
            setting.setVisible(false);
        } */
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search:
                prepareIntent(SearchActivity.class);
                break;
            // case R.id.action_settings:
            //    prepareIntent(PrefsActivity.class);
            //    break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void prepareIntent(Class cla) {
        this.startActivity(new Intent(MainActivity.this, cla));
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        item.setChecked(true);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        // drawer.closeDrawer(GravityCompat.START);

        drawer.closeDrawers();
        switchFragment(item.getTitle().toString());
        return true;
    }

    /**
     * 判断各种逻辑下的fragment显示问题
     */
    private void switchFragment(String title) {
        mTitle = title;
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
        if (mTitle != null) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) actionBar.setTitle(mTitle);
        }
    }

    /**
     * 根据menuItem的title返回对应的fragment
     */
    private Fragment createFragmentByTitle(String title) {
        switch (title) {
            case "首页":
                return new HomeFragment();
            case "收藏":
                return new FavoriteFragment();
            default:
                return new BaseFragment();
        }
    }
}
