package com.demon.doubanmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.demon.doubanmovies.MovieApplication;
import com.demon.doubanmovies.R;
import com.demon.doubanmovies.db.bean.SimpleCardBean;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SimpleMovieAdapter extends BaseAdapter<SimpleMovieAdapter.ViewHolder> {

    private Context mContext;
    private List<SimpleCardBean> mData = new ArrayList<>();

    public SimpleMovieAdapter(Context context) {
        this.mContext = context;
    }

    public void setOnItemClickListener(OnItemClickListener callback) {
        mCallback = callback;
    }

    public void update(List<SimpleCardBean> data) {
        mData.clear();
        notifyDataSetChanged();
        for (int i = 0; i < data.size(); i++) {
            mData.add(data.get(i));
            notifyItemInserted(i);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).
                inflate(R.layout.item_simple_movie_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.update();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.iv_item_simple_movie_image) ImageView imageMovie;
        @Bind(R.id.tv_item_simple_movie_text) TextView textTitle;

        SimpleCardBean cardBean;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void update() {
            cardBean = mData.get(getLayoutPosition());
            imageLoader.displayImage(cardBean.image, imageMovie, options);
            textTitle.setText(cardBean.name);
        }

        @Override
        public void onClick(View view) {
            int pos = getLayoutPosition();
            if (mCallback != null) {
                mCallback.onItemClick(mData.get(pos).id,
                        mData.get(pos).image,
                        mData.get(pos).isFilm);
            }
        }
    }

}
