package com.demon.doubanmovies.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.demon.doubanmovies.R;
import com.demon.doubanmovies.adapter.base.BaseRecyclerAdapter;
import com.demon.doubanmovies.adapter.base.BaseRecyclerHolder;
import com.demon.doubanmovies.model.bean.WorksEntity;

import java.util.Collection;
import java.util.List;

public class WorksMovieAdapter extends BaseRecyclerAdapter<WorksEntity> {
    protected OnItemClickListener mCallback;

    public WorksMovieAdapter(RecyclerView view, Collection<WorksEntity> datas) {
        super(view, datas, R.layout.item_simple_movie_layout);
        setOnItemClickListener((View v, Object data, int position) -> {
                    if (mCallback != null) {
                        WorksEntity bean = (WorksEntity) data;
                        mCallback.onItemClick(bean.subject.id,
                                bean.subject.images.large, true);
                    }
                }
        );
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mCallback = listener;
    }

    public void update(List<WorksEntity> data) {
        super.update(data);
    }

    @Override
    public void convert(BaseRecyclerHolder holder, WorksEntity item, int position, boolean isScrolling) {
        holder.setText(R.id.tv_item_simple_movie_text, item.subject.title);
        holder.setImageFromUrl(R.id.iv_item_simple_movie_image, item.subject.images.large);
    }

    public interface OnItemClickListener {
        void onItemClick(String id, String imageUrl, Boolean isFilm);
    }
}
