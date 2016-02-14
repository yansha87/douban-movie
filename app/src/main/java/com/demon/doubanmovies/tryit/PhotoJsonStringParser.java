package com.demon.doubanmovies.tryit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2015/12/27.
 */
final public class PhotoJsonStringParser {
    private static final int FLICKR_API_PREFIX_LENGTH = 14;

    List<Photo> parse(String response) throws JSONException {
        JSONObject searchResults = new JSONObject(
                response.substring(FLICKR_API_PREFIX_LENGTH, response.length() - 1));
        JSONArray photos = searchResults.getJSONObject("photos").getJSONArray("photo");
        List<Photo> results = new ArrayList<>(photos.length());
        for (int i = 0, size = photos.length(); i < size; i++) {
            results.add(new Photo(photos.getJSONObject(i)));
        }

        return results;
    }
}
