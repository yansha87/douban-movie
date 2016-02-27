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

import com.demon.doubanmovies.MovieApplication;
import com.demon.doubanmovies.R;
import com.demon.doubanmovies.activity.base.BaseToolbarActivity;
import com.demon.doubanmovies.adapter.MovieAdapter2;
import com.demon.doubanmovies.db.bean.CelebrityBean;
import com.demon.doubanmovies.douban.DataManager;
import com.demon.doubanmovies.utils.StringUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscriber;

public class CelebrityActivity extends BaseToolbarActivity {

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
    private MovieAdapter2 mWorksAdapter;

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
        mWorksAdapter = new MovieAdapter2(mWorksView, null);

        mWorksAdapter.setOnItemClickListener((String id, String imageUrl, Boolean isFilm) -> {
            SubjectActivity.toActivity(CelebrityActivity.this, id, imageUrl);
        });
        mWorksView.setAdapter(mWorksAdapter);
    }

    @Override
    protected void initData() {
        String mId = getIntent().getStringExtra(KEY_CEL_ID);
        loadCelebrityData(mId);
    }


    protected void onStop() {
        super.onStop();
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

    private void loadCelebrityData(String mId) {
        DataManager.getInstance().getCelebrityData(mId)
                .subscribe(new Subscriber<CelebrityBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(CelebrityActivity.this, e.toString(),
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(CelebrityBean celebrityBean) {
                        mCelebrity = celebrityBean;
                        setViewAfterGetData(celebrityBean);
                    }
                });
    }

    /**
     * 得到Celebrity实例后设置界面
     */
    private void setViewAfterGetData(CelebrityBean bean) {
        if (bean == null) return;
        mActionBarHelper.setTitle(bean.name);
        imageLoader.displayImage(bean.avatars.medium, mImage, options);
        mName.setText(bean.name);
        mNameEn.setText(bean.name_en);
        String gender = getResources().getString(R.string.gender);
        mGender.setText(String.format("%s%s", gender, bean.gender));
        String bronPlace = getResources().getString(R.string.bron_place);
        mBronPlace.setText(String.format("%s%s", bronPlace, bean.born_place));

        if (bean.aka.size() > 0) {
            mAke.setText(StringUtil.getSpannableString(
                    getString(R.string.cel_ake), Color.BLACK));
            mAke.append(StringUtil.getListString(bean.aka, '/'));
        } else {
            mAke.setVisibility(View.GONE);
        }

        if (bean.aka_en.size() > 0) {
            mAkeEn.setText(StringUtil.getSpannableString(
                    getString(R.string.cel_ake_en), Color.BLACK));
            mAkeEn.append(StringUtil.getListString(bean.aka_en, '/'));
        } else {
            mAkeEn.setVisibility(View.GONE);
        }

        mWorks.setText(String.format("%s%s", bean.name, getString(R.string.video_works)));

        mWorksAdapter.update(bean.works);

        mCelLayout.setVisibility(View.VISIBLE);
    }

}
