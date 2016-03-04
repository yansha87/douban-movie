package com.demon.doubanmovies.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.demon.doubanmovies.MovieApplication;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;


public class BaseAdapter<T extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<T> {
    protected OnItemClickListener mCallback;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    protected DisplayImageOptions options = MovieApplication.getLoaderOptions();

    public void setOnItemClickListener(OnItemClickListener listener) {
        mCallback = listener;
    }

    @Override
    public T onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(T holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public interface OnItemClickListener {
        void onItemClick(String id, String imageUrl, Boolean isFilm);
    }
}
