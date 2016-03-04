package com.demon.doubanmovies.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.demon.doubanmovies.R;
import com.demon.doubanmovies.adapter.base.BaseRecyclerAdapter;
import com.demon.doubanmovies.adapter.base.BaseRecyclerHolder;
import com.demon.doubanmovies.model.bean.CelebrityEntity;
import com.demon.doubanmovies.model.bean.ImagesEntity;
import com.demon.doubanmovies.model.bean.SimpleActorBean;

import java.util.Collection;
import java.util.List;

public class ActorAdapter extends BaseRecyclerAdapter<SimpleActorBean> {
    protected OnItemClickListener mCallback;

    public ActorAdapter(RecyclerView view, Collection<SimpleActorBean> datas) {
        super(view, datas, R.layout.item_simple_actor_layout);
        setOnItemClickListener((View v, Object data, int position) -> {
                    if (mCallback != null) {
                        SimpleActorBean bean = (SimpleActorBean) data;
                        mCallback.onItemClick(bean.entity.id,
                                bean.entity.avatars.large, false);
                    }
                }
        );
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mCallback = listener;
    }

    public void update(List<SimpleActorBean> data) {
        super.update(data);
    }

    @Override
    public void convert(BaseRecyclerHolder holder, SimpleActorBean item, int position, boolean isScrolling) {
        holder.setText(R.id.tv_item_simple_actor_text, item.entity.name);
        if (item.type == 1)
            holder.setText(R.id.tv_item_simple_director_text, mContext.getString(R.string.directors));
        String imageUrl = getImageUrl(item.entity);
        if (imageUrl != null)
            holder.setImageFromUrl(R.id.iv_item_simple_actor_image, imageUrl);
    }

    private String getImageUrl(CelebrityEntity entity) {
        ImagesEntity imagesEntity = entity.avatars;
        String url = null;
        if (imagesEntity != null) {
            // 如果有大图，使用大图
            url = imagesEntity.large;
            // 如果没有大图，使用中图
            if (url == null)
                url = imagesEntity.medium;
            // 如果没有中图，使用小图
            if (url == null)
                url = imagesEntity.small;
        }
        return url;
    }

    public interface OnItemClickListener {
        void onItemClick(String id, String imageUrl, Boolean isFilm);
    }
}
