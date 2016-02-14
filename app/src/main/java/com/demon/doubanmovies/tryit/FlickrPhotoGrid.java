package com.demon.doubanmovies.tryit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.util.FixedPreloadSizeProvider;
import com.demon.doubanmovies.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2015/12/27.
 */
public class FlickrPhotoGrid extends Fragment implements PhotoViewer {
    private static final String STATE_POSITION_INDEX = "state_position_index";
    private static final String TAG = "FlickrSearchActivity";

    private static final String IMAGE_SIZE_KEY = "image_size";
    private static final String PRELOAD_KEY = "preload";
    private static final String THUMBNAIL_KEY = "thumbnail";
    private int photoSize;
    private boolean thumbnail;
    private DrawableRequestBuilder<Photo> fullRequest;
    private DrawableRequestBuilder<Photo> thumbnailRequest;
    private DrawableRequestBuilder<Photo> preloadRequest;

    private GridView grid;
    private PhotoAdapter adapter;
    private List<Photo> currentPhotos;

    public static Fragment newInstance(int pageSize, int preloadCount, boolean thumbnail) {
        FlickrPhotoGrid photoGrid = new FlickrPhotoGrid();
        Bundle args = new Bundle();
        args.putInt(IMAGE_SIZE_KEY, pageSize);
        args.putInt(PRELOAD_KEY, preloadCount);
        args.putBoolean(THUMBNAIL_KEY, thumbnail);
        photoGrid.setArguments(args);
        return photoGrid;
    }

    @Override
    public void onPhotosUpdate(List<Photo> photos) {
        currentPhotos = photos;
        if (adapter != null)
            adapter.setPhotos(currentPhotos);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (grid != null) {
            int index = grid.getFirstVisiblePosition();
            outState.putInt(STATE_POSITION_INDEX, index);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle args = getArguments();
        photoSize = args.getInt(IMAGE_SIZE_KEY);
        thumbnail = args.getBoolean(THUMBNAIL_KEY);

        fullRequest = Glide.with(this)
                .from(Photo.class)
                .centerCrop()
                .crossFade(R.anim.fade_in, 150);

        thumbnailRequest = Glide.with(this)
                .from(Photo.class)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .crossFade(R.anim.fade_in, 150)
                .override(Api.SQUARE_THUMB_SIZE, Api.SQUARE_THUMB_SIZE);

        preloadRequest = thumbnail ? thumbnailRequest.clone().priority(Priority.HIGH) : fullRequest;
        final View result = inflater.inflate(R.layout.flickr_photo_grid, container, false);

        grid = (GridView) result.findViewById(R.id.images);
        grid.setColumnWidth(photoSize);
        adapter = new PhotoAdapter();
        grid.setAdapter(adapter);

        final FixedPreloadSizeProvider<Photo> preloadSizeProvider =
                new FixedPreloadSizeProvider<>(photoSize, photoSize);

        final ListPreloader<Photo> preloader =
                new ListPreloader<Photo>(adapter, preloadSizeProvider, args.getInt(PRELOAD_KEY));

        grid.setOnScrollListener(preloader);

        if (currentPhotos != null)
            adapter.setPhotos(currentPhotos);

        if (savedInstanceState != null) {
            int index = savedInstanceState.getInt(STATE_POSITION_INDEX);
            grid.setSelection(index);
        }

        return result;
    }

    private class PhotoAdapter extends BaseAdapter implements ListPreloader.PreloadModelProvider<Photo> {

        private List<Photo> photos = new ArrayList<>(0);
        private final LayoutInflater inflater;

        public PhotoAdapter() {
            this.inflater = LayoutInflater.from(getActivity());
        }

        @Override
        public int getCount() {
            return photos.size();
        }

        @Override
        public Object getItem(int position) {
            return photos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Photo current = photos.get(position);
            final ImageView imageView;

            if (convertView == null) {
                imageView = (ImageView) inflater.inflate(R.layout.flickr_photo_grid_item,
                        parent, false);
                ViewGroup.LayoutParams params = imageView.getLayoutParams();
                params.width = photoSize;
                params.height = photoSize;
            } else {
                imageView = (ImageView) convertView;
            }

            fullRequest.load(current)
                    .thumbnail(thumbnail ? thumbnailRequest.load(current) : null)
                    .into(imageView);


            imageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                }
            });
            return imageView;
        }

        @Override
        public List<Photo> getPreloadItems(int position) {
            return photos.subList(position, position + 1);
        }

        @Override
        public GenericRequestBuilder getPreloadRequestBuilder(Photo item) {
            return preloadRequest.load(item);
        }

        public void setPhotos(List<Photo> photos) {
            Log.i(TAG, "setPhotos: " + photos.size());
            this.photos = photos;
            notifyDataSetChanged();
        }
    }
}
