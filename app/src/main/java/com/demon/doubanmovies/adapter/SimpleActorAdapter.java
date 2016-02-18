package com.demon.doubanmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.demon.doubanmovies.MovieApplication;
import com.demon.doubanmovies.R;
import com.demon.doubanmovies.db.bean.CelebrityEntity;
import com.demon.doubanmovies.db.bean.ImagesEntity;
import com.demon.doubanmovies.db.bean.SimpleActorBean;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SimpleActorAdapter extends RecyclerView.Adapter<SimpleActorAdapter.ViewHolder> {

    private static final String TAG = "SimpleActorAdapter";
    private Context mContext;
    private List<SimpleActorBean> mData = new ArrayList<>();
    private OnItemClickListener callback;

    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options = MovieApplication.getLoaderOptions();


    public SimpleActorAdapter(Context context) {
        mContext = context;
    }

    public void setOnItemClickListener(OnItemClickListener callback) {
        this.callback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).
                inflate(R.layout.item_simple_actor_layout, parent, false);
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

    public void update(List<SimpleActorBean> mActorData) {
        mData.clear();
        notifyDataSetChanged();
        for (int i = 0; i < mActorData.size(); i++) {
            mData.add(mActorData.get(i));
            notifyItemInserted(i);
        }
    }

    public interface OnItemClickListener {
        void itemClick(String id, String imageUrl);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.iv_item_simple_actor_image)
        ImageView imageMovie;
        @Bind(R.id.tv_item_simple_actor_text)
        TextView textTitle;
        @Bind(R.id.tv_item_simple_director_text)
        TextView textDirector;

        SimpleActorBean cardBean;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void update() {
            cardBean = mData.get(getLayoutPosition());
            if (cardBean == null) return;

            CelebrityEntity entity = cardBean.getEntity();
            if (entity == null) return;

            ImagesEntity imagesEntity = entity.getAvatars();
            if (imagesEntity != null) {
                String url = imagesEntity.getLarge();
                if (url == null) url = imagesEntity.getMedium();
                if (url == null) url = imagesEntity.getSmall();
                if (url != null)
                    imageLoader.displayImage(url, imageMovie, options);
            } else {
                Log.i(TAG, "update: imagesEntity is null");
            }
            textTitle.setText(entity.getName());

            if (cardBean.getType() == 1) {
                textDirector.setText(mContext.getString(R.string.directors));
            } else {
                textDirector.setText("");
            }
        }

        @Override
        public void onClick(View view) {
            int pos = getLayoutPosition();
            if (callback != null) {
                callback.itemClick(mData.get(pos).getEntity().getId(),
                        mData.get(pos).getEntity().getAvatars().getLarge());
            }
        }
    }
}
