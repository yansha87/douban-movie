package com.demon.doubanmovies.adapter.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
    private static final String TAG = "BaseRecyclerHolder";
    private final SparseArray<View> mViews;
    private Context mContext;
    private final static int viewCount = 16;

    public BaseRecyclerHolder(View itemView) {
        super(itemView);

        mContext = itemView.getContext();

        // at most 16 view in RecyclerView item
        this.mViews = new SparseArray<>(viewCount);
    }

    @SuppressWarnings("unused")
    public SparseArray<View> getViews() {
        return mViews;
    }

    /**
     * get view by id
     * @param viewId view id
     * @param <T> view type
     * @return view
     */
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            if (mViews.size() < viewCount) {
                mViews.put(viewId, view);
            } else {
                // may be need to throw an exception
                Log.i(TAG, "view size over viewCount");
            }
        }

        return (T) view;
    }

    public BaseRecyclerHolder setText(int viewId, String text) {
        TextView view = getView(viewId);
        view.setText(text);
        return this;
    }

    @SuppressWarnings("unused")
    public BaseRecyclerHolder setImageResource(int viewId, int drawableId) {
        ImageView view = getView(viewId);
        view.setImageResource(drawableId);
        return this;
    }

    @SuppressWarnings("unused")
    public BaseRecyclerHolder setImageBitmap(int viewId, Bitmap bitmap) {
        ImageView view = getView(viewId);
        view.setImageBitmap(bitmap);
        return this;
    }

    /**
     * display round image by url
     * @param viewId view id
     * @param url image url
     * @return BaseRecyclerHolder
     */
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

    /**
     * display round image by entity
     * @param viewId view id
     * @param entity image entity
     * @return BaseRecyclerHolder
     */
    public BaseRecyclerHolder setRoundImageFromEntity(int viewId, ImagesEntity entity) {
        // get display image url according preference
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

    /**
     * display image by entity
     * @param viewId view id
     * @param entity image entity
     * @return BaseRecyclerHolder
     */
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

    /**
     * set rating bar
     * @param viewId view id
     * @param rating rating value
     * @return BaseRecyclerHolder
     */
    public BaseRecyclerHolder setRatingBar(int viewId, float rating) {
        RatingBar view = getView(viewId);
        view.setRating(rating);
        return this;
    }

}
