package com.demon.doubanmovies.activity;

import android.app.ActivityOptions;
import android.app.SharedElementCallback;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ImageView;

import com.demon.doubanmovies.R;
import com.demon.doubanmovies.fragment.HomeFragment;
import com.demon.doubanmovies.transitions.DetailsActivity;
import com.demon.doubanmovies.transitions.Utils;
import com.demon.doubanmovies.tryit.FlickrSearchActivity;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

import static com.demon.doubanmovies.transitions.Utils.RADIOHEAD_ALBUM_NAMES;
import static com.demon.doubanmovies.transitions.Utils.RADIOHEAD_ALBUM_URLS;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private static final boolean DEBUG = true;

    public static final String EXTRA_CURRENT_ITEM_POSITION = "extra_current_item_position";
    public static final String EXTRA_OLD_ITEM_POSITION = "extra_old_item_position";

    private RecyclerView mRecyclerView;
    private Bundle mTmpState;
    private boolean mIsReentering;

    private String mTitle;

    private FragmentManager mFragmentManager;
    private Fragment mCurFragment;

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
            LOG("=== sharedElements: " + Utils.setToString(sharedElements.keySet()), mIsReentering);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setExitSharedElementCallback(mCallback);

        // Resources res = getResources();
        // mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        // mRecyclerView.setLayoutManager(new GridLayoutManager(this, res.getInteger(R.integer.num_columns)));
        // mRecyclerView.setAdapter(new CardAdapter());

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


        //初始化Viewpager
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

    private class CardAdapter extends RecyclerView.Adapter<CardHolder> {
        @Override
        public CardHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            return new CardHolder(inflater.inflate(R.layout.image_card, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(CardHolder holder, int position) {
            holder.bind(position);
        }

        @Override
        public int getItemCount() {
            return RADIOHEAD_ALBUM_URLS.length;
        }
    }

    private class CardHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView mImage;
        private int mPosition;

        public CardHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mImage = (ImageView) itemView.findViewById(R.id.image);
        }

        public void bind(int position) {
            Picasso.with(mImage.getContext()).load(RADIOHEAD_ALBUM_URLS[position]).into(mImage);
            mImage.setTransitionName(RADIOHEAD_ALBUM_NAMES[position]);
            mImage.setTag(RADIOHEAD_ALBUM_NAMES[position]);
            mPosition = position;
        }

        @Override
        public void onClick(View v) {
            mIsReentering = false;
            LOG("startActivity(Intent, Bundle)", false);
            Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
            intent.putExtra(EXTRA_CURRENT_ITEM_POSITION, mPosition);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(
                    MainActivity.this, mImage, mImage.getTransitionName()).toBundle());
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

    private static void LOG(String message, boolean isReentering) {
        if (DEBUG) {
            Log.i(TAG, String.format("%s: %s", isReentering ? "REENTERING" : "EXITING", message));
        }
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

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem setting = menu.findItem(R.id.action_settings);
        if (mTitle.equals(getString(R.string.nav_home))) {
            setting.setVisible(true);
        } else {
            setting.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search:
                // prepareIntent(SearchActivity.class);
                break;
            case R.id.action_settings:
                // prepareIntent(PrefsActivity.class);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
