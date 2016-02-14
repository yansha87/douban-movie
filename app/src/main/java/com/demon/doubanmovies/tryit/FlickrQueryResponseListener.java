package com.demon.doubanmovies.tryit;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;

import java.util.Collection;
import java.util.List;

/**
 * Created by user on 2015/12/27.
 */
public class FlickrQueryResponseListener implements Response.Listener<String>,
        Response.ErrorListener {
    private final PhotoJsonStringParser parser;
    private final Query query;
    private final Collection<Api.QueryListener> listeners;

    public FlickrQueryResponseListener(PhotoJsonStringParser parser, Query query, Collection<Api.QueryListener> listeners) {
        this.listeners = listeners;
        this.parser = parser;
        this.query = query;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        notifyFailed(error);
    }

    @Override
    public void onResponse(String response) {
        try {
            notifySuccess(parser.parse(response));
        } catch (JSONException e) {
            notifyFailed(e);
        }
    }

    private void notifyFailed(Exception e) {
        for (Api.QueryListener listener : listeners) {
            listener.onSearchFailed(query, e);
        }
    }

    private void notifySuccess(List<Photo> results) {
        for (Api.QueryListener listener : listeners) {
            listener.onSearchCompleted(query, results);
        }
    }


}
