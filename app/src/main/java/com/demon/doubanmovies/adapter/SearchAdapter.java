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
import com.demon.doubanmovies.utils.StringUtil;

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
        @Bind(R.id.tv_item_search_collect_count)
        TextView textCollectCount;
        @Bind(R.id.tv_item_search_title)
        TextView textTitle;
        @Bind(R.id.tv_item_search_original_title)
        TextView textOriginalTitle;
        @Bind(R.id.tv_item_search_genres)
        TextView textGenres;
        @Bind(R.id.tv_item_search_director)
        TextView textDirector;
        @Bind(R.id.tv_item_search_cast)
        TextView textCast;

        SimpleSubjectBean mSubject;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void update() {
            mSubject = mData.get(getLayoutPosition());
            ratingBar.setRating(((float) mSubject.getRating().getAverage()) / 2);
            textRating.setText(String.format("%s", mSubject.getRating().getAverage()));
            textCollectCount.setText(mContext.getString(R.string.collect));
            textCollectCount.append(String.format("%d", mSubject.getCollect_count()));
            textCollectCount.append(mContext.getString(R.string.count));
            textTitle.setText(mSubject.getTitle());
            if (mSubject.getOriginal_title().equals(mSubject.getTitle())) {
                textOriginalTitle.setVisibility(View.GONE);
            } else {
                textOriginalTitle.setText(mSubject.getOriginal_title());
            }
            textGenres.setText(StringUtil.getListString(mSubject.getGenres(), ','));
            textDirector.setText(mContext.getString(R.string.directors));
            // textDirector.append(CelebrityUtil.list2String(mSubject.getDirectors(), '/'));
            for (int i = 0; i < mSubject.getDirectors().size(); i++) {
                textCast.append('/' + mSubject.getDirectors().get(i).getName());
            }
            textCast.setText(mContext.getString(R.string.actors));
            // textCast.append(CelebrityUtil.list2String(mSubject.getCasts(), '/'));
            for (int i = 0; i < mSubject.getCasts().size(); i++) {
                textCast.append('/' + mSubject.getCasts().get(i).getName());
            }
            imageLoader.displayImage(mSubject.getImages().getLarge(), imageMovie, options);
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
