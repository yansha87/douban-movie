package com.demon.doubanmovies.adapter.base;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.demon.doubanmovies.MovieApplication;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.w3c.dom.Text;

/**
 * Created by user on 2016/2/24.
 */
public class BaseRecyclerHolder extends RecyclerView.ViewHolder {
    private final SparseArray<View> mViews;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    protected DisplayImageOptions options = MovieApplication.getLoaderOptions();
    protected DisplayImageOptions roundOptions = MovieApplication.getLoaderRoundedOptions();

    public BaseRecyclerHolder(View itemView) {
        super(itemView);

        // 最多16个view
        this.mViews = new SparseArray<>(16);
    }

    public SparseArray<View> getViews() {
        return mViews;
    }

    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public BaseRecyclerHolder setText(int viewId, String text) {
        TextView view = getView(viewId);
        view.setText(text);
        return this;
    }

    public BaseRecyclerHolder setImageResource(int viewId, int drawableId) {
        ImageView view = getView(viewId);
        view.setImageResource(drawableId);
        return this;
    }

    public BaseRecyclerHolder setImageBitmap(int viewId, Bitmap bitmap) {
        ImageView view = getView(viewId);
        view.setImageBitmap(bitmap);
        return this;
    }

    public BaseRecyclerHolder setRoundImageFromUrl(int viewId, String url) {
        ImageView view = getView(viewId);
        imageLoader.displayImage(url, view, roundOptions);
        return this;
    }

    public BaseRecyclerHolder setImageFromUrl(int viewId, String url) {
        ImageView view = getView(viewId);
        imageLoader.displayImage(url, view, options);
        return this;
    }

    public BaseRecyclerHolder setRatingBar(int viewId, float rating) {
        RatingBar view = getView(viewId);
        view.setRating(rating);
        return this;
    }

}
