package com.demon.doubanmovies.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.demon.doubanmovies.R;
import com.demon.doubanmovies.adapter.base.BaseRecyclerAdapter;
import com.demon.doubanmovies.adapter.base.BaseRecyclerHolder;
import com.demon.doubanmovies.model.bean.CelebrityEntity;
import com.demon.doubanmovies.model.bean.SubjectBean;
import com.demon.doubanmovies.utils.ImageUtil;

import java.util.Collection;
import java.util.List;

public class FavoriteAdapter extends BaseRecyclerAdapter<SubjectBean> {

    private OnItemClickListener mCallback;

    public FavoriteAdapter(RecyclerView view, Collection<SubjectBean> datas) {
        super(view, datas, R.layout.item_favorite_layout);
        setOnItemClickListener((View v, Object data, int position) -> {
            if (mCallback != null) {
                SubjectBean bean = (SubjectBean) data;
                String url = ImageUtil.getDisplayImage(mContext, bean.images);
                mCallback.onItemClick(bean.id, url, true);
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mCallback = listener;
    }

    public void update(List<SubjectBean> data) {
        super.update(data);
    }

    @Override
    public void convert(BaseRecyclerHolder holder, SubjectBean item, int position, boolean isScrolling) {
        holder.setText(R.id.tv_item_favorite_title, item.title);

        float rate = (float) item.rating.average;
        holder.setText(R.id.tv_item_favorite_rating, String.format("%s", rate));
        holder.setText(R.id.tv_item_favorite_cel, getBaseInformation(item));

        holder.setRoundImageFromUrl(R.id.iv_item_favorite_image, item.localImageFile);
    }

    private String getBaseInformation(SubjectBean item) {
        StringBuilder info = new StringBuilder();

        // director
        if (item.directors.size() > 0) {
            info.append(item.directors.get(0).name);
            info.append(mContext.getString(R.string.director));
        }

        // actor
        for (CelebrityEntity cast : item.casts) {
            info.append("/").append(cast.name);
        }

        return info.toString();
    }

    public interface OnItemClickListener {
        void onItemClick(String id, String imageUrl, Boolean isFilm);
    }
}