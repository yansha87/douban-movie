package com.demon.doubanmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.demon.doubanmovies.R;
import com.demon.doubanmovies.bean.SubjectBean;
import com.demon.doubanmovies.utils.CelebrityUtil;
import com.demon.doubanmovies.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CollectAdapter extends BaseAdapter<CollectAdapter.ViewHolder> {

    private static final String URI_FOR_FILE = "file:/";

    private Context mContext;
    private LayoutInflater mInflater;
    private List<SubjectBean> mData;

    private OnItemClickListener callback;
    private SubjectBean undoSub;

    public void setOnItemClickListener(OnItemClickListener callback) {
        this.callback = callback;
    }

    public CollectAdapter(Context context) {
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

    /**
     * 移除相应的item
     */
    private void remove(int pos) {
        notifyItemRemoved(pos);
        undoSub = mData.get(pos);
        mData.remove(pos);
        callback.itemRemove(pos, undoSub.getId());
    }

    /**
     * 用于撤销“取消收藏”操作
     */
    public void cancelRemove(int pos) {
        if (undoSub != null) {
            notifyItemInserted(pos);
            mData.add(pos, undoSub);
            undoSub = null;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_collect_layout, parent, false);
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

    class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

        @Bind(R.id.iv_item_collect_image)
        ImageView imageMovie;
        @Bind(R.id.tv_item_collect_rating)
        TextView textRating;
        @Bind(R.id.tv_item_collect_title)
        TextView textTitle;
        @Bind(R.id.tv_item_collect_year)
        TextView textYear;
        @Bind(R.id.tv_item_collect_genres)
        TextView textGenres;
        @Bind(R.id.tv_item_collect_cel)
        TextView textCast;
        @Bind(R.id.tv_item_collect_delete)
        TextView btnDelete;
        @Bind(R.id.tv_item_collect_enter)
        TextView btnEnter;

        SubjectBean subjectBean;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            btnDelete.setOnClickListener(this);
            btnEnter.setOnClickListener(this);
        }

        public void update() {
            subjectBean = mData.get(getLayoutPosition());
            if (subjectBean.getRating() != null) {
                float rate = (float) subjectBean.getRating().getAverage();
                textRating.setText(String.format("%s", rate));
            }
            String title = subjectBean.getTitle();
            textTitle.setText(title);
            textYear.setText(String.format("  %s  ", subjectBean.getYear()));
            textGenres.setText(StringUtil.getListString(subjectBean.getGenres(), ','));
            textCast.setText(mContext.getString(R.string.directors));
            textCast.append(String.format("%s//",
                    CelebrityUtil.list2String(subjectBean.getDirectors(), ',')));
            textCast.append(mContext.getString(R.string.actors));
            textCast.append(CelebrityUtil.list2String(subjectBean.getCasts(), ','));
            if (subjectBean.getLocalImageFile() != null) {
                imageLoader.displayImage(
                        String.format("%s%s", URI_FOR_FILE, subjectBean.getLocalImageFile()),
                        imageMovie, options);
            }
        }

        @Override
        public void onClick(View view) {
            if (view == btnDelete) {
                remove(getLayoutPosition());
            } else {
                callback.itemClick(mData.get(getLayoutPosition()).getId(),
                        mData.get(getLayoutPosition()).getImages().getLarge());
            }
        }
    }

    public interface OnItemClickListener {
        void itemClick(String id, String imageUrl);

        void itemRemove(int pos, String id);
    }
}