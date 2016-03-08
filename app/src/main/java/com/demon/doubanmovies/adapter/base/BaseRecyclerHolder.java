package com.demon.doubanmovies.adapter.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.demon.doubanmovies.model.bean.ImagesEntity;
import com.demon.doubanmovies.utils.DensityUtil;
import com.demon.doubanmovies.utils.ImageUtil;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class BaseRecyclerHolder extends RecyclerView.ViewHolder {
    private final SparseArray<View> mViews;
    private Context mContext;

    public BaseRecyclerHolder(View itemView) {
        super(itemView);

        mContext = itemView.getContext();
        // item 最多只能有16个 view，超过就跪了
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
        Glide.with(mContext)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .bitmapTransform(new RoundedCornersTransformation(mContext,
                        DensityUtil.dp2px(mContext, 2), 0))
                .centerCrop()
                .crossFade()
                .into(view);
        return this;
    }

    public BaseRecyclerHolder setRoundImageFromEntity(int viewId, ImagesEntity entity) {
        String url = ImageUtil.getDisplayImage(mContext, entity);
        ImageView view = getView(viewId);
        Glide.with(mContext)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .bitmapTransform(new RoundedCornersTransformation(mContext,
                        DensityUtil.dp2px(mContext, 2), 0))
                .centerCrop()
                .crossFade()
                .into(view);
        return this;
    }

    public BaseRecyclerHolder setImageFromEntity(int viewId, ImagesEntity entity) {
        String url = ImageUtil.getDisplayImage(mContext, entity);
        ImageView view = getView(viewId);
        Glide.with(mContext)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .crossFade()
                .into(view);
        return this;
    }

    public BaseRecyclerHolder setRatingBar(int viewId, float rating) {
        RatingBar view = getView(viewId);
        view.setRating(rating);
        return this;
    }

}
