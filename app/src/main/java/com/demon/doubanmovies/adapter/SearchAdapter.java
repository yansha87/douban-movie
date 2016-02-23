package com.demon.doubanmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.demon.doubanmovies.R;
import com.demon.doubanmovies.db.bean.SimpleSubjectBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchAdapter extends BaseAdapter<SearchAdapter.ViewHolder> {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<SimpleSubjectBean> mData;

    public SearchAdapter(Context context, List<SimpleSubjectBean> data) {
        this.mContext = context;
        this.mData = data;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_search_layout, parent, false);
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.iv_item_search_images)
        ImageView imageMovie;
        @Bind(R.id.rb_item_search_rating)
        RatingBar ratingBar;
        @Bind(R.id.tv_item_search_rating)
        TextView textRating;
        @Bind(R.id.tv_item_search_favorite_count)
        TextView textCollectCount;
        @Bind(R.id.tv_item_search_title)
        TextView textTitle;
        @Bind(R.id.tv_item_search_content)
        TextView textContent;

        SimpleSubjectBean mSubject;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void update() {
            mSubject = mData.get(getLayoutPosition());
            ratingBar.setRating(((float) mSubject.rating.average) / 2);
            textRating.setText(String.format("%s ", mSubject.rating.average));
            textCollectCount.setText(mContext.getString(R.string.left_brackets));
            textCollectCount.append(String.format("%d", mSubject.collect_count));
            textCollectCount.append(mContext.getString(R.string.count));
            textTitle.setText(mSubject.title);

            // 设置搜索结果
            setSearchResult();

            imageLoader.displayImage(mSubject.images.large, imageMovie, roundOptions);
        }

        private void setSearchResult() {
            List<String> entries = new ArrayList<>();
            if (mSubject.directors.size() > 0) {
                entries.add(mSubject.directors.get(0).name + mContext.getString(R.string.director));
            }

            for (int i = 0; i < mSubject.casts.size(); i++) {
                textContent.append(mSubject.casts.get(i).name + "/");
                entries.add(mSubject.casts.get(i).name);
            }
            for (int i = 0; i < mSubject.genres.size(); i++) {
                entries.add(mSubject.genres.get(i));
            }

            if (mSubject.year.length() > 0) {
                entries.add(mSubject.year);
            }

            StringBuffer stringBuffer = new StringBuffer();
            if (entries.size() > 0) {
                String sep = "/";
                for (String entry : entries) {
                    stringBuffer.append(entry).append(sep);
                }

                textContent.setText(stringBuffer.substring(0, stringBuffer.length() - 1));
            }
        }

        @Override
        public void onClick(View view) {
            if (mCallback != null) {
                int position = getLayoutPosition();
                mCallback.onItemClick(mData.get(position).id,
                        mData.get(position).images.large, false);
            }
        }
    }
}
