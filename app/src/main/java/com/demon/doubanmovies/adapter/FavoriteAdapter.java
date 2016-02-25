package com.demon.doubanmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.demon.doubanmovies.R;
import com.demon.doubanmovies.adapter.base.BaseRecyclerAdapter;
import com.demon.doubanmovies.adapter.base.BaseRecyclerHolder;
import com.demon.doubanmovies.db.bean.CelebrityEntity;
import com.demon.doubanmovies.db.bean.SimpleSubjectBean;
import com.demon.doubanmovies.db.bean.SubjectBean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FavoriteAdapter extends BaseRecyclerAdapter<SubjectBean> {

    private static final String URI_FOR_FILE = "file:/";
    protected OnItemClickListener mCallback;
    private Context mContext;

    public FavoriteAdapter(RecyclerView view, Collection<SubjectBean> datas) {
        super(view, datas, R.layout.item_favorite_layout);
        mContext = view.getContext();
        setOnItemClickListener((View v, Object data, int position) -> {
            if (mCallback != null) {
                SubjectBean bean = (SubjectBean) data;
                mCallback.onItemClick(bean.id,
                        bean.images.large, true);
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

        String url = String.format("%s%s", URI_FOR_FILE, item.localImageFile);
        holder.setImageFromUrl(R.id.iv_item_favorite_image, url);
    }

    private String getBaseInformation(SubjectBean item) {
        StringBuffer infor = new StringBuffer();

        // 导演
        if (item.directors.size() > 0) {
            infor.append(item.directors.get(0).name);
            infor.append(mContext.getString(R.string.director));
        }

        // 演员
        for (CelebrityEntity cast : item.casts) {
            infor.append("/").append(cast.name);
        }

        return infor.toString();
    }

    public interface OnItemClickListener {
        void onItemClick(String id, String imageUrl, Boolean isFilm);
    }
}