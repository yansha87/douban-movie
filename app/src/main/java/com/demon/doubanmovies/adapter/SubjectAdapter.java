package com.demon.doubanmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.demon.doubanmovies.R;
import com.demon.doubanmovies.adapter.base.BaseAdapter;
import com.demon.doubanmovies.model.bean.SimpleSubjectBean;
import com.demon.doubanmovies.utils.DensityUtil;
import com.demon.doubanmovies.utils.ImageUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Maybe need to use BaseRecyclerAdapter instead of BaseAdapter
 */
public class SubjectAdapter extends BaseAdapter<RecyclerView.ViewHolder> {

    // FootView is loading
    private static final int FOOT_LOADING = 0;
    // FootView loading completed
    private static final int FOOT_COMPLETED = 1;
    // FootView loading fail
    private static final int FOOT_FAIL = 2;

    public static final String FOOT_VIEW_ID = "-1";
    // ItemView type
    private static final int TYPE_ITEM = 0;
    // FootView type
    private static final int TYPE_FOOT = 1;

    private FootViewHolder mFootView;
    private final Context mContext;
    private List<SimpleSubjectBean> mData;

    // total data count
    private int mTotalDataCount = 0;

    // is coming movie of not
    private boolean isComingMovie;

    public SubjectAdapter(Context context, List<SimpleSubjectBean> data,
                          boolean isComingMovie) {
        this.mContext = context;
        this.mData = data;
        this.isComingMovie = isComingMovie;
    }

    /**
     * get start position of loading data
     *
     * @return start position
     */
    public int getStart() {
        return mData.size();
    }

    /**
     * get total data count
     *
     * @return total data count
     */
    public int getTotalDataCount() {
        return mTotalDataCount;
    }

    /**
     * set total data count
     *
     * @param totalDataCount total data count
     */
    public void setTotalDataCount(int totalDataCount) {
        this.mTotalDataCount = totalDataCount;
    }

    /**
     * load completed or not
     *
     * @return load completed or not
     */
    public boolean isLoadCompleted() {
        return mData.size() >= getTotalDataCount();
    }

    public void loadMoreData(List<SimpleSubjectBean> data) {
        this.mData.addAll(data);
        notifyDataSetChanged();
    }

    public void loadFailHint() {
        mFootView.setFootView(FOOT_FAIL);
    }

    /**
     * update recycler view
     *
     * @param data           update data list
     * @param totalDataCount total data count
     */
    public void updateList(List<SimpleSubjectBean> data, int totalDataCount) {
        if (data == null) return;
        this.mData = data;
        setTotalDataCount(totalDataCount);
        notifyDataSetChanged();
    }

    /**
     * create ViewHolder
     *
     * @param parent   parent
     * @param viewType view type
     * @return Foot ViewHolder or Item ViewHolder
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOT) {
            if (mFootView == null) {
                View view = LayoutInflater.from(mContext).
                        inflate(R.layout.view_load_tips, parent, false);
                mFootView = new FootViewHolder(view);
            }
            return mFootView;
        } else {
            View view = LayoutInflater.from(mContext).
                    inflate(R.layout.item_simple_subject_layout, parent, false);

            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder.getItemViewType() == TYPE_FOOT) {
            ((FootViewHolder) viewHolder).update();
        } else {
            ((ItemViewHolder) viewHolder).update();
        }
    }

    @Override
    public int getItemCount() {
        return mData.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        // last position is foot view
        if (position == mData.size()) {
            return TYPE_FOOT;
        } else {
            return TYPE_ITEM;
        }
    }

    /**
     * Item ViewHolder
     */
    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.iv_item_simple_subject_image)
        ImageView imageMovie;
        @Bind(R.id.ll_item_simple_subject_rating)
        LinearLayout layoutRating;
        @Bind(R.id.rb_item_simple_subject_rating)
        RatingBar ratingBar;
        @Bind(R.id.tv_item_simple_subject_rating)
        TextView textRating;
        @Bind(R.id.tv_item_simple_subject_title)
        TextView textTitle;

        SimpleSubjectBean subjectBean;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void update() {
            subjectBean = mData.get(getLayoutPosition());

            if (!isComingMovie) {
                layoutRating.setVisibility(View.VISIBLE);
                float rate = (float) subjectBean.rating.average;
                ratingBar.setRating(rate / 2);
                textRating.setText(String.format("%s", rate));
            }
            String title = subjectBean.title;
            textTitle.setText(title);

            // get display image url by preference
            String url = ImageUtil.getDisplayImage(mContext, subjectBean.images);
            Glide.with(mContext)
                    .load(url)
                    .into(imageMovie);
        }

        @Override
        public void onClick(View view) {
            if (mCallback != null) {
                int position = getLayoutPosition();
                mCallback.onItemClick(mData.get(position).id,
                        mData.get(position).images.large, true);
            }
        }
    }

    /**
     * Foot ViewHolder
     */
    class FootViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ProgressBar progressBar;
        private final TextView textLoadTip;

        public FootViewHolder(final View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.pb_view_load_tip);
            textLoadTip = (TextView) itemView.findViewById(R.id.tv_view_load_tip);
            itemView.setOnClickListener(this);
        }

        public void update() {
            if (isLoadCompleted()) {
                setFootView(FOOT_COMPLETED);
            } else {
                setFootView(FOOT_LOADING);
            }
        }

        public void setFootView(int event) {
            ViewGroup.LayoutParams params = itemView.getLayoutParams();
            switch (event) {
                case FOOT_LOADING:
                    params.height = DensityUtil.dp2px(mContext, 40f);
                    itemView.setLayoutParams(params);
                    progressBar.setVisibility(View.VISIBLE);
                    textLoadTip.setText(mContext.getString(R.string.foot_loading));
                    itemView.setClickable(false);
                    break;
                case FOOT_COMPLETED:
                    params.height = 0;
                    itemView.setLayoutParams(params);
                    progressBar.setVisibility(View.INVISIBLE);
                    textLoadTip.setVisibility(View.INVISIBLE);
                    itemView.setClickable(false);
                    break;
                case FOOT_FAIL:
                    params.height = DensityUtil.dp2px(mContext, 40f);
                    itemView.setLayoutParams(params);
                    progressBar.setVisibility(View.GONE);
                    textLoadTip.setText(mContext.getString(R.string.foot_fail));
                    itemView.setClickable(true);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onClick(View view) {
            if (mCallback != null) {
                setFootView(FOOT_LOADING);
                mCallback.onItemClick(FOOT_VIEW_ID, null, false);
            }
        }
    }
}
