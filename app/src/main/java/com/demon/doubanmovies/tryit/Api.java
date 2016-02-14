package com.demon.doubanmovies.tryit;

import android.content.Context;
import android.util.SparseArray;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.util.LruCache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by user on 2015/12/27.
 */
public class Api {

    private static Api api;
    private static final String TAG = "FlickrSearchActivity";
    private static final String API_KEY = "f0e6fbb5fdf1f3842294a1d21f84e8a6";
    private static final String SIGNED_API_URL =
            "https://api.flickr.com/services/rest/?method=%s&format=json&api_key=" + API_KEY;
    // Incomplete size independent url for photos that can be cached per photo
    private static final String CACHEABLE_PHOTO_URL = "http://farm%s.staticflickr.com/%s/%s_%s_";
    private static final int MAX_URLS_TO_CACHE = 2000;
    private static final LruCache<UrlCacheKey, String> CACHED_URLS =
            new LruCache<>(MAX_URLS_TO_CACHE);
    private static final int MAX_ITEMS_PER_PAGE = 300;
    private static final String PER_PAGE = "&per_page=" + MAX_ITEMS_PER_PAGE;

    private static final SparseArray<String> EDGE_TO_SIZE_KEY = new SparseArray<String>() {
        {
            put(75, "s");
            put(100, "t");
            put(150, "q");
            put(240, "m");
            put(320, "n");
            put(640, "z");
            put(1024, "b");
        }
    };

    private static final List<Integer> SORTED_SIZE_KEYS = new ArrayList<>(EDGE_TO_SIZE_KEY.size());

    static {
        for (int i = 0; i < EDGE_TO_SIZE_KEY.size(); i++) {
            SORTED_SIZE_KEYS.add(EDGE_TO_SIZE_KEY.keyAt(i));
        }
        Collections.sort(SORTED_SIZE_KEYS);
    }

    public static final int SQUARE_THUMB_SIZE = SORTED_SIZE_KEYS.get(0);


    private static String getSizeKey(int width, int height) {
        final int largestEdge = Math.max(width, height);

        String result = EDGE_TO_SIZE_KEY.get(SORTED_SIZE_KEYS.get(SORTED_SIZE_KEYS.size() - 1));
        for (int edge : SORTED_SIZE_KEYS) {
            if (largestEdge <= edge) {
                result = EDGE_TO_SIZE_KEY.get(edge);
                break;
            }
        }

        return result;
    }

    public static String getCacheableUrl(Photo photo) {
        return String.format(CACHEABLE_PHOTO_URL, photo.getFarm(),
                photo.getServer(), photo.getId(), photo.getSecret());
    }

    public static Api get(Context context) {
        if (api == null) {
            api = new Api(context);
        }

        return api;
    }

    private final RequestQueue requestQueue;
    private final Set<QueryListener> queryListeners = new HashSet<>();
    private QueryResult lastQueryResult;

    protected Api(Context context) {
        this.requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        QueryListener queryListener = new QueryListener() {
            @Override
            public void onSearchCompleted(Query query, List<Photo> photos) {
                lastQueryResult = new QueryResult(query, photos);
            }

            @Override
            public void onSearchFailed(Query query, Exception e) {
                lastQueryResult = null;
            }
        };
        queryListeners.add(queryListener);
    }

    public void query(Query currentQuery) {
        if (lastQueryResult != null && lastQueryResult.query.equals(currentQuery)) {
            for (QueryListener listener : queryListeners) {
                listener.onSearchCompleted(lastQueryResult.query, lastQueryResult.results);
            }
            return;
        }

        FlickrQueryResponseListener responseListener = new FlickrQueryResponseListener(
                new PhotoJsonStringParser(), currentQuery, queryListeners);
        StringRequest request = new StringRequest(Request.Method.GET, currentQuery.getUrl(),
                responseListener, responseListener);
        request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }

    public void registerSearchListener(QueryListener queryListener) {
        queryListeners.add(queryListener);
    }

    public void unregisterSearchListener(QueryListener queryListener) {
        queryListeners.remove(queryListener);
    }

    public static String getRecentUrl() {
        return getUrlForMethod("flickr.photos.getRecent" + PER_PAGE);
    }

    private static String getUrlForMethod(String method) {
        return String.format(SIGNED_API_URL, method);
    }

    public static String getSearchUrl(String queryString) {
        return getUrlForMethod("flickr.photos.search") + "&text=" + queryString + PER_PAGE;
    }

    public static String getPhotoURL(Photo model, int width, int height) {
        return getPhotoUrl(model, getSizeKey(width, height));
    }

    private static String getPhotoUrl(Photo model, String sizeKey) {
        return model.getPartialUrl() + sizeKey + ".jpg";
    }

    public interface QueryListener {
        void onSearchCompleted(Query query, List<Photo> photos);

        void onSearchFailed(Query query, Exception e);
    }

    private static class UrlCacheKey {
        private final Photo photo;
        private final String sizeKey;

        public UrlCacheKey(Photo photo, String sizeKey) {
            this.photo = photo;
            this.sizeKey = sizeKey;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof UrlCacheKey) {
                UrlCacheKey other = (UrlCacheKey) o;
                return photo.equals(other.photo) && sizeKey.equals(other.sizeKey);
            }

            return false;
        }

        @Override
        public int hashCode() {
            int result = photo.hashCode();
            result = 31 * result + sizeKey.hashCode();
            return result;
        }
    }

    private class QueryResult {
        private final Query query;
        private final List<Photo> results;

        public QueryResult(Query query, List<Photo> photos) {
            this.query = query;
            this.results = photos;
        }
    }
}
