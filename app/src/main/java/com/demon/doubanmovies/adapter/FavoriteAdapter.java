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
import com.demon.doubanmovies.db.bean.SubjectBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FavoriteAdapter extends BaseAdapter<FavoriteAdapter.ViewHolder> {

    private static final String URI_FOR_FILE = "file:/";

    private Context mContext;
    private LayoutInflater mInflater;
    private List<SubjectBean> mData;

    public FavoriteAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = new ArrayList<>();
    }


    public void add(List<SubjectBean> data) {
        for (int i = 0; i < data.size(); i++) {
            mData.add(data.get(i));
            notifyItemInserted(i);
        }
    }

    public void update(List<SubjectBean> data) {
        this.mData.clear();
        notifyDataSetChanged();
        add(data);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_favorite_layout, parent, false);
        return new ViewHolder(view);
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

        @Bind(R.id.iv_item_favorite_image)
        ImageView imageMovie;
        @Bind(R.id.tv_item_favorite_rating)
        TextView textRating;
        @Bind(R.id.tv_item_favorite_title)
        TextView textTitle;
        @Bind(R.id.tv_item_favorite_cel)
        TextView textCast;

        SubjectBean subjectBean;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ButterKnife.bind(this, itemView);
        }

        public void update() {
            subjectBean = mData.get(getLayoutPosition());
            if (subjectBean.getRating() != null) {
                float rate = (float) subjectBean.getRating().getAverage();
                textRating.setText(String.format("%s", rate));
            }
            String title = subjectBean.getTitle();
            textTitle.setText(title);
            textCast.setText("");
            if (subjectBean.getDirectors().size() > 0) {
                textCast.setText(String.format("%s%s",
                        subjectBean.getDirectors().get(0).getName(), mContext.getString(R.string.director)));
            }

            for (int i = 0; i < subjectBean.getCasts().size(); i++) {
                textCast.append('/' + subjectBean.getCasts().get(i).getName());
            }
            if (subjectBean.getLocalImageFile() != null) {
                imageLoader.displayImage(
                        String.format("%s%s", URI_FOR_FILE, subjectBean.getLocalImageFile()),
                        imageMovie, options);
            }
        }

        @Override
        public void onClick(View view) {
            if (mCallback != null) {
                int position = getLayoutPosition();
                mCallback.onItemClick(mData.get(position).getId(),
                        mData.get(position).getImages().getLarge());
            }
        }
    }
}