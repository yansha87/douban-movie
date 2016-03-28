package com.demon.doubanmovies.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.demon.doubanmovies.R;
import com.demon.doubanmovies.adapter.base.BaseRecyclerAdapter;
import com.demon.doubanmovies.adapter.base.BaseRecyclerHolder;
import com.demon.doubanmovies.model.bean.SimpleActorBean;
import com.demon.doubanmovies.utils.ImageUtil;

import java.util.Collection;
import java.util.List;

public class ActorAdapter extends BaseRecyclerAdapter<SimpleActorBean> {
    private OnItemClickListener mCallback;

    public ActorAdapter(RecyclerView view, Collection<SimpleActorBean> datas) {
        super(view, datas, R.layout.item_simple_actor_layout);
        setOnItemClickListener((View v, Object data, int position) -> {
                    if (mCallback != null) {
                        SimpleActorBean bean = (SimpleActorBean) data;
                        String url = ImageUtil.getDisplayImage(mContext, bean.entity.avatars);
                        mCallback.onItemClick(bean.entity.id, url, false);
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
        holder.setText(R.id.tv_item_simple_director_text, "");
        if (item.type == 1) {
            holder.setText(R.id.tv_item_simple_director_text, mContext.getString(R.string.directors));
        }

        String imageUrl = ImageUtil.getDisplayImage(mContext, item.entity.avatars);
        if (imageUrl != null)
            holder.setImageFromEntity(R.id.iv_item_simple_actor_image, item.entity.avatars);
    }

    public interface OnItemClickListener {
        void onItemClick(String id, String imageUrl, Boolean isFilm);
    }
}
