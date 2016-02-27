package com.demon.doubanmovies.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.demon.doubanmovies.R;
import com.demon.doubanmovies.adapter.base.BaseRecyclerAdapter;
import com.demon.doubanmovies.adapter.base.BaseRecyclerHolder;
import com.demon.doubanmovies.db.bean.SimpleSubjectBean;
import com.demon.doubanmovies.db.bean.SubjectBean;
import com.demon.doubanmovies.db.bean.WorksEntity;

import java.util.Collection;
import java.util.List;

/**
 * Created by demon on 2016/2/27.
 */
public class MovieAdapter extends BaseRecyclerAdapter<SimpleSubjectBean> {
    protected OnItemClickListener mCallback;

    public MovieAdapter(RecyclerView view, Collection<SimpleSubjectBean> datas) {
        super(view, datas, R.layout.item_simple_movie_layout);
        setOnItemClickListener((View v, Object data, int position) -> {
                    if (mCallback != null) {
                        SimpleSubjectBean bean = (SimpleSubjectBean) data;
                        mCallback.onItemClick(bean.id,
                                bean.images.large, true);
                    }
                }
        );
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mCallback = listener;
    }

    public void update(List<SimpleSubjectBean> data) {
        super.update(data);
    }

    @Override
    public void convert(BaseRecyclerHolder holder, SimpleSubjectBean item, int position, boolean isScrolling) {
        holder.setText(R.id.tv_item_simple_movie_text, item.title);
        holder.setImageFromUrl(R.id.iv_item_simple_movie_image, item.images.large);
    }

    public interface OnItemClickListener {
        void onItemClick(String id, String imageUrl, Boolean isFilm);
    }
}
