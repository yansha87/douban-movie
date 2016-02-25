package com.demon.doubanmovies.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.demon.doubanmovies.MovieApplication;
import com.demon.doubanmovies.R;
import com.demon.doubanmovies.activity.base.BaseToolbarActivity;
import com.demon.doubanmovies.adapter.BaseAdapter;
import com.demon.doubanmovies.adapter.SimpleMovieAdapter;
import com.demon.doubanmovies.db.bean.CelebrityBean;
import com.demon.doubanmovies.db.bean.SimpleCardBean;
import com.demon.doubanmovies.db.bean.WorksEntity;
import com.demon.doubanmovies.utils.Constant;
import com.demon.doubanmovies.utils.StringUtil;
import com.google.gson.GsonBuilder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CelebrityActivity extends BaseToolbarActivity
        implements BaseAdapter.OnItemClickListener {

    private static final String VOLLEY_TAG = "CelActivity";
    private static final String KEY_CEL_ID = "cel_id";

    @Bind(R.id.tv_cel_name)
    TextView mName;
    @Bind(R.id.tv_cel_name_en)
    TextView mNameEn;
    @Bind(R.id.tv_cel_gender)
    TextView mGender;
    @Bind(R.id.tv_cel_bron_place)
    TextView mBronPlace;
    @Bind(R.id.tv_cel_ake)
    TextView mAke;
    @Bind(R.id.tv_cel_ake_en)
    TextView mAkeEn;
    @Bind(R.id.iv_cel_image)
    ImageView mImage;
    @Bind(R.id.tv_celebrity_works)
    TextView mWorks;
    @Bind(R.id.rv_cel_works)
    RecyclerView mWorksView;
    @Bind(R.id.ll_cel_layout)
    LinearLayout mCelLayout;

    private CelebrityBean mCelebrity;
    private List<SimpleCardBean> mWorksData = new ArrayList<>();

    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options = MovieApplication.getLoaderOptions();

    public static void toActivity(Context context, String id) {
        Intent intent = new Intent(context, CelebrityActivity.class);
        intent.putExtra(KEY_CEL_ID, id);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_celebrity;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mActionBarHelper.setTitle("");
        mWorksView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void initData() {
        String mId = getIntent().getStringExtra(KEY_CEL_ID);
        String url = Constant.API + Constant.CELEBRITY + mId;
        volleyGetCelebrity(url);
    }

    protected void onStop() {
        super.onStop();
        MovieApplication.removeRequest(VOLLEY_TAG);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_celebrity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cel_search:
                startActivity(new Intent(this, SearchActivity.class));
                return true;
            case R.id.action_cel_skip:
                if (mCelebrity == null) {
                    return true;
                }
                WebActivity.toWebActivity(this,
                        mCelebrity.mobile_url,
                        mCelebrity.name);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 通过volley得到mCelebrity
     */
    private void volleyGetCelebrity(String url) {
        StringRequest request = new StringRequest(url,
                (String response) -> {
                    mCelebrity = new GsonBuilder().create().fromJson(response,
                            Constant.cleType);
                    setViewAfterGetData();

                },
                (VolleyError error) -> {
                    Toast.makeText(CelebrityActivity.this, error.toString(),
                            Toast.LENGTH_SHORT).show();

                });
        MovieApplication.addRequest(request, VOLLEY_TAG);
    }

    /**
     * 得到mCelebrity实例后设置界面
     */
    private void setViewAfterGetData() {
        if (mCelebrity == null) return;
        mActionBarHelper.setTitle(mCelebrity.name);
        imageLoader.displayImage(mCelebrity.avatars.medium, mImage, options);
        mName.setText(mCelebrity.name);
        mNameEn.setText(mCelebrity.name_en);
        String gender = getResources().getString(R.string.gender);
        mGender.setText(String.format("%s%s", gender, mCelebrity.gender));
        String bronPlace = getResources().getString(R.string.bron_place);
        mBronPlace.setText(String.format("%s%s", bronPlace, mCelebrity.born_place));

        if (mCelebrity.aka.size() > 0) {
            mAke.setText(StringUtil.getSpannableString(
                    getString(R.string.cel_ake), Color.BLACK));
            mAke.append(StringUtil.getListString(mCelebrity.aka, '/'));
        } else {
            mAke.setVisibility(View.GONE);
        }

        if (mCelebrity.aka_en.size() > 0) {
            mAkeEn.setText(StringUtil.getSpannableString(
                    getString(R.string.cel_ake_en), Color.BLACK));
            mAkeEn.append(StringUtil.getListString(mCelebrity.aka_en, '/'));
        } else {
            mAkeEn.setVisibility(View.GONE);
        }

        mWorks.setText(String.format("%s%s", mCelebrity.name, getString(R.string.video_works)));

        for (WorksEntity work : mCelebrity.works) {
            SimpleCardBean data = new SimpleCardBean(
                    work.getSubject().id,
                    work.getSubject().title,
                    work.getSubject().images.large,
                    true);
            mWorksData.add(data);
        }
        SimpleMovieAdapter mWorksAdapter =
                new SimpleMovieAdapter(CelebrityActivity.this);
        mWorksAdapter.setOnItemClickListener(this);
        mWorksView.setAdapter(mWorksAdapter);
        mWorksAdapter.update(mWorksData);

        mCelLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClick(String id, String imageUrl, Boolean isMovie) {
        SubjectActivity.toActivity(this, id, imageUrl);
    }

}
