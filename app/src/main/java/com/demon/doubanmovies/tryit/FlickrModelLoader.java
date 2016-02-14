package com.demon.doubanmovies.tryit;

import android.content.Context;

import com.bumptech.glide.load.model.GenericLoaderFactory;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelCache;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;

import java.io.InputStream;

/**
 * Created by user on 2015/12/28.
 */
public class FlickrModelLoader extends BaseGlideUrlLoader<Photo> {

    public static class Factory implements ModelLoaderFactory<Photo, InputStream> {

        private final ModelCache<Photo, GlideUrl> modelCache = new ModelCache<Photo, GlideUrl>(500);

        @Override
        public ModelLoader<Photo, InputStream> build(Context context, GenericLoaderFactory factories) {
            return new FlickrModelLoader(context, modelCache);
        }

        @Override
        public void teardown() {

        }
    }

    public FlickrModelLoader(Context context, ModelCache<Photo, GlideUrl> modelCache) {
        super(context, modelCache);
    }

    @Override
    protected String getUrl(Photo model, int width, int height) {
        return Api.getPhotoURL(model, width, height);
    }
}
