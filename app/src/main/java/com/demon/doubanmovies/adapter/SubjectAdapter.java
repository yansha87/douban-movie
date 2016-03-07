package com.demon.doubanmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SubjectAdapter extends BaseAdapter<RecyclerView.ViewHolder> {

    // FootView的显示类型
    public static final int FOOT_LOADING = 0;
    public static final int FOOT_COMPLETED = 1;
    public static final int FOOT_FAIL = 2;
    // 用于判断是否是加载失败时点击的FootView
    public static final String FOOT_VIEW_ID = "-1";
    // ItemView的类型，FootView应用于加载更多
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOT = 1;

    private FootViewHolder mFootView;
    private Context mContext;
    private List<SimpleSubjectBean> mData;
    /**
     * 用于加载更多数据
     */
    private int mTotalDataCount = 0;
    /**
     * 判断是否属于“即将上映”
     */
    private boolean isComingMovie;

    public SubjectAdapter(Context context, List<SimpleSubjectBean> data,
                          boolean isComingMovie) {
        this.mContext = context;
        this.mData = data;
        this.isComingMovie = isComingMovie;
    }

    /**
     * 用于加载数据时的url起点
     */
    public int getStart() {
        return mData.size();
    }

    /**
     * 返回adapter数据的总数
     */
    public int getTotalDataCount() {
        return mTotalDataCount;
    }

    public void setTotalDataCount(int totalDataCount) {
        this.mTotalDataCount = totalDataCount;
    }

    /**
     * 判断是否已经加载完毕
     */
    public boolean isLoadCompleted() {
        return mData.size() >= getTotalDataCount();
    }

    /**
     * 用于加载更多item
     */
    public void loadMoreData(List<SimpleSubjectBean> data) {
        this.mData.addAll(data);
        notifyDataSetChanged();
    }

    public void loadFailHint() {
        mFootView.setFootView(FOOT_FAIL);
    }

    /**
     * 用于更新数据
     *
     * @param data           更新的数据
     * @param totalDataCount 数据的总量，采取多次加载
     */
    public void updateList(List<SimpleSubjectBean> data, int totalDataCount) {
        this.mData = data;
        setTotalDataCount(totalDataCount);
        notifyDataSetChanged();
    }

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
        // 最后一个位置为foot
        if (position == mData.size()) {
            return TYPE_FOOT;
        } else {
            return TYPE_ITEM;
        }
    }


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

            Glide.with(mContext)
                    .load(subjectBean.images.large)
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
     * recyclerView上拉加载更多的footViewHolder
     */
    class FootViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ProgressBar progressBar;
        private TextView textLoadTip;


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
