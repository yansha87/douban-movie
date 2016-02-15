package com.demon.doubanmovies.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.demon.doubanmovies.MovieApplication;
import com.demon.doubanmovies.R;
import com.demon.doubanmovies.adapter.SimpleMovieAdapter;
import com.demon.doubanmovies.bean.CelebrityEntity;
import com.demon.doubanmovies.bean.SimpleCardBean;
import com.demon.doubanmovies.bean.SimpleSubjectBean;
import com.demon.doubanmovies.bean.SubjectBean;
import com.demon.doubanmovies.utils.Constant;
import com.demon.doubanmovies.utils.DensityUtil;
import com.demon.doubanmovies.utils.StringUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.app.ActivityOptions.makeSceneTransitionAnimation;

public class SubjectActivity extends AppCompatActivity
        implements SimpleMovieAdapter.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener,
        AppBarLayout.OnOffsetChangedListener,
        View.OnClickListener {

    //intent中subjectId的key用于查询数据
    private static final String KEY_SUBJECT_ID = "subject_id";
    private static final String KEY_IMAGE_URL = "image_url";
    //json中subject的标签
    private static final String JSON_SUBJECTS = "subjects";

    private static final String SHARE_IMAGE = "share_image";
    private static final String URI_FOR_FILE = "file:/";
    private static final String URI_FOR_IMAGE = ".png";
    @Bind(R.id.refresh_subj)
    SwipeRefreshLayout mRefresh;
    @Bind(R.id.btn_subject_skip)
    FloatingActionButton mFloatingButton;
    //film header
    @Bind(R.id.header_container_subj)
    AppBarLayout mHeaderContainer;
    @Bind(R.id.toolbar_container_subj)
    CollapsingToolbarLayout mToolbarContainer;
    @Bind(R.id.iv_header_subj)
    ImageView mToolbarImage;
    @Bind(R.id.introduce_container_subj)
    LinearLayout mIntroduceContainer;
    @Bind(R.id.toolbar_subj)
    Toolbar mToolbar;
    @Bind(R.id.iv_subject_images)
    ImageView mImage;
    @Bind(R.id.rb_subject_rating)
    RatingBar mRatingBar;
    @Bind(R.id.tv_subject_rating)
    TextView mRating;
    @Bind(R.id.tv_subject_collect_count)
    TextView mCollect;
    @Bind(R.id.tv_subject_title)
    TextView mTitle;
    @Bind(R.id.tv_subject_original_title)
    TextView mOriginal_title;
    @Bind(R.id.tv_subject_genres)
    TextView mGenres;
    @Bind(R.id.tv_subject_ake)
    TextView mAke;
    @Bind(R.id.tv_subject_countries)
    TextView mCountries;
    @Bind(R.id.film_container_subj)
    LinearLayout mFilmContainer;
    //film summary
    @Bind(R.id.tv_subject_summary)
    TextView mSummaryText;
    //film recommend
    @Bind(R.id.tv_subject_recommend_tip)
    TextView mRecommendTip;
    @Bind(R.id.re_subject_recommend)
    RecyclerView mRecommend;

    private int[] actor_id = {R.id.view_actor_layout_1,
            R.id.view_actor_layout_2, R.id.view_actor_layout_3, R.id.view_actor_layout_4,
            R.id.view_actor_layout_5, R.id.view_actor_layout_6};
    private ActorViewHolder[] actorViewHolders = new ActorViewHolder[6];
    //film subject
    private String mId;
    private String mContent;
    private SubjectBean mSubject;

    private String mRecommendTags;
    private List<SimpleCardBean> mRecommendData = new ArrayList<>();
    private SimpleMovieAdapter mRecommendMovieAdapter;

    private boolean isSummaryShow = false;

    private File mFile;

    private boolean isCollect = false;

    private int mImageWidth;
    private FrameLayout.LayoutParams mIntroduceContainerParams;

    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options = MovieApplication.getLoaderOptions();

    public static void toActivity(Activity activity, String id, String imageUrl) {
        Intent intent = new Intent(activity, SubjectActivity.class);
        intent.putExtra(KEY_SUBJECT_ID, id);
        intent.putExtra(KEY_IMAGE_URL, imageUrl);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            activity.startActivity(intent,
                    makeSceneTransitionAnimation(activity).toBundle());
        } else {
            activity.startActivity(intent);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void toActivityWithShareElement(Activity context, ImageView image, String id) {
        Intent intent = new Intent(context, SubjectActivity.class);
        intent.putExtra(KEY_SUBJECT_ID, id);
        image.setTransitionName(SHARE_IMAGE);
        context.startActivity(intent, makeSceneTransitionAnimation(
                context, image, image.getTransitionName()).toBundle());
    }

    /**
     * activity转场动画
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private Transition makeTransition() {
        TransitionSet transition = new TransitionSet();
        transition.addTransition(new Explode());
        transition.addTransition(new Fade());
        transition.setDuration(400);
        return transition;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(makeTransition());
        }

        mId = getIntent().getStringExtra(KEY_SUBJECT_ID);
        initView();
        initEvent();
        mSubject = MovieApplication.getDataSource().filmOfId(mId);
        if (mSubject != null) {
            isCollect = true;
            initAfterGetData();
        } else {
            volleyGetSubject();
        }
    }

    private void initView() {
        //设置圆形刷新球的偏移量
        mRefresh.setProgressViewOffset(false,
                -DensityUtil.dp2px(getApplication(), 8f),
                DensityUtil.dp2px(getApplication(), 32f));
        mToolbar.setTitle("");

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        //用于collapsingToolbar缩放时content中内容和图片的动作
        mImageWidth = mImage.getLayoutParams().width + DensityUtil.dp2px(getApplication(), 8f);
        mIntroduceContainerParams =
                (FrameLayout.LayoutParams) mIntroduceContainer.getLayoutParams();

        for (int i = 0; i < 6; i++) {
            View view = findViewById(actor_id[i]);
            actorViewHolders[i] = new ActorViewHolder(view);
        }

        mRecommend.setLayoutManager(new LinearLayoutManager(
                SubjectActivity.this, LinearLayoutManager.HORIZONTAL, false));
        mRecommendMovieAdapter = new SimpleMovieAdapter(this);
        mRecommend.setAdapter(mRecommendMovieAdapter);
        mRecommendMovieAdapter.update(mRecommendData);

        mFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), mId + URI_FOR_IMAGE);
        String imageUri = (mFile.exists() ?
                String.format("%s%s", URI_FOR_FILE, mFile.getPath()) :
                getIntent().getStringExtra(KEY_IMAGE_URL));
        imageLoader.displayImage(imageUri, mImage, options);
        imageLoader.displayImage(imageUri, mToolbarImage, options,
                new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view,
                                                  final Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        Palette.from(loadedImage).generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(Palette palette) {
                                int defaultBgColor = Color.parseColor("#009688");
                                int bgColor = palette.getDarkVibrantColor(defaultBgColor);
                                mToolbarContainer.setBackgroundColor(bgColor);
                            }
                        });
                    }
                });
    }

    private void initEvent() {
        mRefresh.setOnRefreshListener(this);
        mFloatingButton.setOnClickListener(this);
        mRecommendMovieAdapter.setOnItemClickListener(this);
        mRecommendTip.setOnClickListener(this);
        mRecommendTip.setClickable(false);
        //利用appBarLayout的回调接口禁止或启用swipeRefreshLayout
        mHeaderContainer.addOnOffsetChangedListener(this);
    }

    private void volleyGetSubject() {
        String url = Constant.API + Constant.SUBJECT + mId;
        mRefresh.setRefreshing(true);
        StringRequest stringRequest = new StringRequest(url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        mContent = response;
                        //如果film已经收藏,更新数据
                        if (isCollect) filmSave();
                        mSubject = new Gson().fromJson(mContent, Constant.subType);
                        initAfterGetData();
                        mRefresh.setRefreshing(false);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SubjectActivity.this, error.toString(),
                                Toast.LENGTH_SHORT).show();
                        mRefresh.setRefreshing(false);
                    }
                });
        MovieApplication.addRequest(stringRequest, mId);
    }


    /**
     * 得到网络返回数据初始化界面
     */
    private void initAfterGetData() {
        if (mSubject == null) return;
        if (mSubject.getTitle() != null) {
            mToolbarContainer.setTitle(mSubject.getTitle());
        }

        if (mSubject.getRating() != null) {
            float rate = (float) (mSubject.getRating().getAverage() / 2);
            mRatingBar.setRating(rate);
            mRating.setText(String.format("%s", rate * 2));
        }

        mCollect.setText(getString(R.string.collect));
        mCollect.append(String.format("%s", mSubject.getCollect_count()));
        mCollect.append(getString(R.string.count));
        mTitle.setText(String.format("%s   ", mSubject.getTitle()));
        mTitle.append(StringUtil.getSpannableString1(
                String.format("  %s  ", mSubject.getYear()),
                new ForegroundColorSpan(Color.WHITE),
                new BackgroundColorSpan(Color.parseColor("#5ea4ff")),
                new RelativeSizeSpan(0.88f)));

        if (!mSubject.getOriginal_title().equals(mSubject.getTitle())) {
            mOriginal_title.setText(mSubject.getOriginal_title());
            mOriginal_title.setVisibility(View.VISIBLE);
        } else {
            mOriginal_title.setVisibility(View.GONE);
        }
        mGenres.setText(StringUtil.getListString(mSubject.getGenres(), ','));
        mAke.setText(StringUtil.getSpannableString(
                getString(R.string.ake), Color.GRAY));
        mAke.append(StringUtil.getListString(mSubject.getAka(), '/'));
        mCountries.setText(StringUtil.getSpannableString(
                getString(R.string.countries), Color.GRAY));
        mCountries.append(StringUtil.getListString(mSubject.getCountries(), '/'));

        mSummaryText.setText(StringUtil.getSpannableString(
                getString(R.string.summary), Color.parseColor("#5ea4ff")));
        mSummaryText.append(mSubject.getSummary());
        mSummaryText.setEllipsize(TextUtils.TruncateAt.END);
        mSummaryText.setOnClickListener(this);

        //获得导演演员数据列表
        getActorData();

        //显示View并配上动画
        mFilmContainer.setAlpha(0f);
        mFilmContainer.setVisibility(View.VISIBLE);
        mFilmContainer.animate().alpha(1f).setDuration(800);

        //加载推荐
        mRecommendTip.setText(getString(R.string.recommend_loading));
        StringBuilder tag = new StringBuilder();
        for (int i = 0; i < mSubject.getGenres().size(); i++) {
            tag.append(mSubject.getGenres().get(i));
            if (i == 1) break;
        }
        mRecommendTags = tag.toString();
        volleyGetRecommend();
    }

    /**
     * 获得导演演员的数据
     */
    private void getActorData() {
        boolean isDirWithActor = false;
        for (int i = 0; (i < mSubject.getDirectors().size() && i < 2); i++) {
            CelebrityEntity celebrity = mSubject.getDirectors().get(i);
            //判断导演是不是主演，如果是打上“导演兼主演”标签
            //为满足一些特殊的数据，需要做null判断
            String id = mSubject.getCasts().get(0).getId();
            if (i == 0 && id != null && celebrity.getId() != null
                    && celebrity.getId().equals(id)) {
                isDirWithActor = true;
                actorViewHolders[i].bindDataForDir(celebrity, true);
            } else {
                isDirWithActor = false;
                actorViewHolders[i].bindDataForDir(celebrity, false);
            }
        }

        int j = 2;
        for (int i = 0; i < 4; i++) {
            //判断主演是否是导演，如果是就跳过
            if (i == 0 && isDirWithActor) i++;
            if (i == mSubject.getCasts().size()) break;
            CelebrityEntity cel = mSubject.getCasts().get(i);
            actorViewHolders[j++].bindData(cel);
        }
    }

    /**
     * 通过查询tag获得recommend数据
     */
    private void volleyGetRecommend() {

        if (TextUtils.isEmpty(mRecommendTags)) return;
        String url = Constant.API + Constant.SEARCH_TAG + mRecommendTags;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new GsonBuilder().create();
                        try {
                            mRecommendTip.setText(getString(R.string.recommend_list));
                            mRecommendTip.setClickable(false);
                            String json = response.getString(JSON_SUBJECTS);
                            List<SimpleSubjectBean> data = gson.fromJson(json,
                                    Constant.simpleSubTypeList);
                            mRecommendData = new ArrayList<>();
                            for (SimpleSubjectBean simpleSub : data) {
                                mRecommendData.add(new SimpleCardBean(
                                        simpleSub.getId(),
                                        simpleSub.getTitle(),
                                        simpleSub.getImages().getLarge(),
                                        true));
                            }
                            mRecommendMovieAdapter.update(mRecommendData);
                            mRecommend.setVisibility(View.VISIBLE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mRecommendTip.setText(getString(R.string.recommend_load_fail));
                        mRecommendTip.setClickable(true);
                    }
                });
        MovieApplication.addRequest(request, mId);
    }

    @Override
    protected void onStop() {
        super.onStop();
        MovieApplication.removeRequest(mId);
    }

    @Override
    public void itemClick(String id, String imageUrl, boolean isFilm) {
        if (isFilm) {
            SubjectActivity.toActivity(this, id, imageUrl);
        } else {
            CelebrityActivity.toActivity(this, id);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sub, menu);
        MenuItem collect = menu.findItem(R.id.action_sub_collect);
        if (isCollect) {
            collect.setIcon(R.drawable.ic_action_collected);
        } else {
            collect.setIcon(R.drawable.ic_action_uncollected);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                    this.finishAfterTransition();
                } else {
                    this.finish();
                }
                break;
            case R.id.action_sub_search:
                this.startActivity(new Intent(this, SearchActivity.class));
                break;
            case R.id.action_sub_collect:
                collectFilmAndSaveImage();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 点击收藏后将subject存入数据库中,并将图片存入文件
     */
    private void collectFilmAndSaveImage() {
        if (mSubject == null) return;
        if (isCollect) {
            cancelSave();
            isCollect = false;
        } else {
            filmSave();
            isCollect = true;
        }
        supportInvalidateOptionsMenu();
    }

    /**
     * 用于保存filmContent和filmImage
     */
    private void filmSave() {
        if (mFile.exists()) mFile.delete();
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(mFile);
            Bitmap bitmap = imageLoader.loadImageSync(mSubject.getImages().getLarge());
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //将电影信息存入到数据库中
        mSubject.setLocalImageFile(mFile.getPath());
        String content = new Gson().toJson(mSubject, Constant.subType);
        MovieApplication.getDataSource().insertOrUpDataFilm(mId, content);
        Toast.makeText(this, getString(R.string.collect_completed), Toast.LENGTH_SHORT).show();
    }

    private void cancelSave() {
        //将数据从数据库中删除
        MovieApplication.getDataSource().deleteFilm(mId);
        //将保存的海报图片删除
        if (mFile.exists()) mFile.delete();
        Toast.makeText(this, R.string.collect_cancel, Toast.LENGTH_SHORT).show();
    }

    /**
     * SwipeRefreshLayout onRefreshListener
     */
    @Override
    public void onRefresh() {
        volleyGetSubject();
    }

    /**
     * AppBarLayout onOffsetChangeListener
     */
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        //利用AppBarLayout的回调接口启用或者关闭下拉刷新
        mRefresh.setEnabled(i == 0);
        //设置AppBarLayout下方内容的滚动效果
        float alpha = Math.abs(i) * 1.0f / appBarLayout.getTotalScrollRange();
        changeContentLayout(alpha);
    }

    /**
     * 使content中内容位置和图片透明度随着AppBarLayout的伸缩而改变
     */
    private void changeContentLayout(float alpha) {
        setContentGravity(alpha == 1 ? Gravity.START : Gravity.CENTER_HORIZONTAL);
        mImage.setAlpha(alpha);
        mIntroduceContainerParams.leftMargin = (int) (mImageWidth * alpha);
        mIntroduceContainer.setLayoutParams(mIntroduceContainerParams);
    }

    private void setContentGravity(int gravity) {
        mIntroduceContainer.setGravity(gravity);
        mAke.setGravity(gravity);
        mCountries.setGravity(gravity);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_subject_summary:
                if (isSummaryShow) {
                    isSummaryShow = false;
                    mSummaryText.setEllipsize(TextUtils.TruncateAt.END);
                    mSummaryText.setLines(3);
                } else {
                    isSummaryShow = true;
                    mSummaryText.setEllipsize(null);
                    mSummaryText.setSingleLine(false);
                }
                break;
            case R.id.btn_subject_skip://跳往豆瓣电影的移动版网页
                if (mSubject == null) break;
                WebActivity.toWebActivity(this,
                        mSubject.getMobile_url(), mSubject.getTitle());
                break;
            case R.id.tv_subject_recommend_tip:
                volleyGetRecommend();
                break;
        }
    }

    class ActorViewHolder implements View.OnClickListener {

        CelebrityEntity mActorData;
        View mActorView;
        ImageView mImage;
        TextView mName;

        ActorViewHolder(View actorView) {
            mActorView = actorView;
            mImage = (ImageView) actorView.findViewById(R.id.iv_item_simple_actor_image);
            mName = (TextView) actorView.findViewById(R.id.tv_item_simple_actor_text);
            mActorView.setVisibility(View.GONE);
        }

        void setActorData(CelebrityEntity data) {
            mActorData = data;
        }

        void bindData(CelebrityEntity data) {
            setActorData(data);
            mName.setText(data.getName());

            mActorView.setVisibility(View.VISIBLE);
            mActorView.setOnClickListener(this);

            if (data.getAvatars() == null) return;
            imageLoader.displayImage(data.getAvatars().getLarge(), mImage, options);
        }

        void bindDataForDir(CelebrityEntity data, boolean isActor) {
            setActorData(data);
            if (isActor) {
                mName.setText(data.getName() + getString(R.string.dir_and_actor));
            } else {
                mName.setText(data.getName() + getString(R.string.director));
            }

            mActorView.setVisibility(View.VISIBLE);
            mActorView.setOnClickListener(this);

            if (data.getAvatars() == null) return;
            imageLoader.displayImage(data.getAvatars().getLarge(), mImage, options);
        }

        @Override
        public void onClick(View view) {
            if (mActorData != null) {
                if (mActorData.getId() == null) {
                    Toast.makeText(SubjectActivity.this, "暂无资料", Toast.LENGTH_SHORT).show();
                } else {
                    CelebrityActivity.toActivity(
                            SubjectActivity.this, mActorData.getId());
                }
            }
        }
    }
}
