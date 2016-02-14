package com.demon.doubanmovies.tryit;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.prefill.PreFillType;
import com.bumptech.glide.request.FutureTarget;
import com.demon.doubanmovies.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class FlickrSearchActivity extends AppCompatActivity
        implements SearchView.OnQueryTextListener {

    private static final String STATE_QUERY = "state_search_string";
    private static final String TAG = "FlickrSearchActivity";

    private SearchView searchView;
    private View searching;
    private View searchLoading;
    private Query currentQuery;
    private List<Photo> currentPhotos = new ArrayList<>();
    private Set<PhotoViewer> photoViewers = new HashSet<>();
    private HandlerThread backgroundThread;
    private Handler backgroundHandler;
    private final QueryListener queryListener = new QueryListener();
    private BackgroundThumbnailFetcher backgroundThumbnailFetcher;
    private TextView searchTerm;

    private enum Page {
        SMALL,
        MEDIUM,
        LIST
    }

    private static final Map<Page, Integer> PAGE_TO_TITLE = new HashMap<Page, Integer>() {
        {
            put(Page.SMALL, R.string.small);
            put(Page.MEDIUM, R.string.medium);
            put(Page.LIST, R.string.list);
        }

    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_activity, menu);
        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));
        searchView.setSubmitButtonEnabled(true);
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(this);

        return true;
    }


    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof PhotoViewer) {
            PhotoViewer photoViewer = (PhotoViewer) fragment;
            photoViewer.onPhotosUpdate(currentPhotos);
            if (!photoViewers.contains(photoViewer)) {
                photoViewers.add(photoViewer);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build());
        backgroundThread = new HandlerThread("BackgroundThumbnailHandlerThread");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());

        setContentView(R.layout.activity_flickr_search);
        searching = findViewById(R.id.searching);
        searchLoading = findViewById(R.id.search_loading);
        searchTerm = (TextView) findViewById(R.id.search_term);

        Resources resources = getResources();
        ViewPager pager = (ViewPager) findViewById(R.id.view_pager);
        pager.setPageMargin(resources.getDimensionPixelOffset(R.dimen.page_margin));
        pager.setAdapter(new FlickrPagerAdapter(getSupportFragmentManager()));

        Api.get(this).registerSearchListener(queryListener);
        if (savedInstanceState != null) {
            Query saveQuery = savedInstanceState.getParcelable(STATE_QUERY);
            if (saveQuery != null) {
                executeQuery(saveQuery);
            }
        } else {
            executeQuery(RecentQuery.get());
        }

        int smallGridSize = resources.getDimensionPixelSize(R.dimen.small_photo_side);
        int mediumGridSize = resources.getDimensionPixelSize(R.dimen.medium_photo_side);
        int listHeightSize = resources.getDimensionPixelSize(R.dimen.flickr_list_item_height);
        int screenWidth = getScreenWidth();

        if (savedInstanceState == null) {
            Glide.get(this).preFillBitmapPool(
                    new PreFillType.Builder(smallGridSize).setWeight(1),
                    new PreFillType.Builder(mediumGridSize).setWeight(1),
                    new PreFillType.Builder(screenWidth / 2, listHeightSize).setWeight(6));
        }
    }


    private int getScreenWidth() {
        return getResources().getDisplayMetrics().widthPixels;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        executeSearch(query);
        searchView.setQuery("", false);
        return false;
    }

    private void executeSearch(String queryString) {
        Query query = TextUtils.isEmpty(queryString) ? null : new SearchQuery(queryString);
        executeQuery(query);
    }

    private void executeQuery(Query query) {
        currentQuery = query;
        if (query == null) {
            queryListener.onSearchCompleted(null, Collections.<Photo>emptyList());
            return;
        }

        searching.setVisibility(View.VISIBLE);
        searchLoading.setVisibility(View.VISIBLE);
        searchTerm.setText(getString(R.string.searching_for, currentQuery.getDescription()));
        Api.get(this).query(currentQuery);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    private class QueryListener implements Api.QueryListener {

        @Override
        public void onSearchCompleted(Query query, List<Photo> photos) {
            Log.i(TAG, "onSearchCompleted: " + photos.size());
            if (!isCurrentQuery(query)) {
                return;
            }

            searching.setVisibility(View.INVISIBLE);

            for (PhotoViewer viewer : photoViewers) {
                viewer.onPhotosUpdate(photos);
            }
            if (backgroundThumbnailFetcher != null) {
                backgroundThumbnailFetcher.cancel();
            }

            backgroundThumbnailFetcher =
                    new BackgroundThumbnailFetcher(FlickrSearchActivity.this, photos);
            backgroundHandler.post(backgroundThumbnailFetcher);
            currentPhotos = photos;
        }

        private boolean isCurrentQuery(Query query) {
            return currentQuery != null && currentQuery.equals(query);
        }

        @Override
        public void onSearchFailed(Query query, Exception e) {
            if (!isCurrentQuery(query)) {
                return;
            }

            searching.setVisibility(View.VISIBLE);
            searchLoading.setVisibility(View.INVISIBLE);

            searchTerm.setText(getString(R.string.search_failed, currentQuery.getDescription()));
        }
    }


    private class BackgroundThumbnailFetcher implements Runnable {
        private boolean isCancelled;
        private Context context;
        private List<Photo> photos;

        public BackgroundThumbnailFetcher(Context context, List<Photo> photos) {
            this.context = context;
            this.photos = photos;
        }

        public void cancel() {
            isCancelled = true;
        }


        @Override
        public void run() {
            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_LOWEST);
            for (Photo photo : photos) {
                if (isCancelled)
                    return;

                FutureTarget<File> futureTarget = Glide.with(context)
                        .load(photo)
                        .downloadOnly(Api.SQUARE_THUMB_SIZE, Api.SQUARE_THUMB_SIZE);
                try {
                    futureTarget.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                Glide.clear(futureTarget);
            }
        }
    }

    private class FlickrPagerAdapter extends FragmentPagerAdapter {

        private int mLastPosition = -1;
        private Fragment mLastFragment;

        public FlickrPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return pageToFragment(position);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            if (position != mLastPosition) {
                if (mLastPosition >= 0) {
                    Glide.with(mLastFragment).pauseRequests();
                }
            }

            Fragment current = (Fragment) object;
            mLastPosition = position;
            mLastFragment = current;

            if (current.isAdded()) {
                Glide.with(current).resumeRequests();
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Page page = Page.values()[position];
            int titleId = PAGE_TO_TITLE.get(page);
            return getString(titleId);
        }

        @Override
        public int getCount() {
            return Page.values().length;
        }

        private Fragment pageToFragment(int position) {
            Page page = Page.values()[position];
            if (page == Page.SMALL) {
                int pageSize = getPageSize(R.dimen.small_photo_side);
                return FlickrPhotoGrid.newInstance(pageSize, 15, false);
            } else if (page == Page.MEDIUM) {
                int pageSize = getPageSize(R.dimen.small_photo_side);
                return FlickrPhotoGrid.newInstance(pageSize, 10, false);
            } else if (page == Page.LIST) {
                return FlickrPhotoList.newInstance();
            } else {
                throw new IllegalArgumentException("No fragment class for page=" + page);
            }
        }

        private int getPageSize(int id) {
            return getResources().getDimensionPixelOffset(id);
        }


    }
}
