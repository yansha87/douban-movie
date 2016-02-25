package com.demon.doubanmovies.adapter.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by user on 2016/2/24.
 */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseRecyclerHolder> {
    protected final int mItemLayoutId;
    protected List<T> mDatas;
    protected boolean isScrolling;
    protected Context mContext;

    private OnItemClickListener mListener;

    public BaseRecyclerAdapter(RecyclerView view, Collection<T> datas, int itemLayoutId) {
        if (datas == null) {
            this.mDatas = new ArrayList<>();
        } else if (datas instanceof List) {
            this.mDatas = (List<T>) datas;
        } else {
            this.mDatas = new ArrayList<>(datas);
        }

        this.mItemLayoutId = itemLayoutId;
        mContext = view.getContext();
        view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                isScrolling = !(newState == RecyclerView.SCROLL_STATE_IDLE);
                if (!isScrolling) {
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public BaseRecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View root = inflater.inflate(mItemLayoutId, parent, false);
        return new BaseRecyclerHolder(root);
    }

    @Override
    public void onBindViewHolder(BaseRecyclerHolder holder, int position) {
        convert(holder, this.mDatas.get(position), position, isScrolling);
        holder.itemView.setOnClickListener(getOnClickListener(position));
    }

    private View.OnClickListener getOnClickListener(final int position) {
        return (View v) -> {
            if (mListener != null && v != null) {
                mListener.onItemClick(v, mDatas.get(position), position);
            }
        };
    }

    public void addData(List<T> data) {
        for (int i = 0; i < data.size(); i++) {
            mDatas.add(data.get(i));
            notifyItemInserted(i);
        }
    }

    public void update(List<T> data) {
        this.mDatas.clear();
        notifyDataSetChanged();
        addData(data);
    }

    public BaseRecyclerAdapter<T> refresh(Collection<T> datas) {
        if (datas == null) {
            this.mDatas = new ArrayList<>();
        } else if (datas instanceof List) {
            this.mDatas = (List<T>) datas;
        } else {
            this.mDatas = new ArrayList<>(datas);
        }
        return this;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public int getItemCount() {
        return this.mDatas.size();
    }

    public abstract void convert(BaseRecyclerHolder holder, T item, int position, boolean isScrolling);

    public interface OnItemClickListener {
        void onItemClick(View view, Object data, int position);
    }
}
