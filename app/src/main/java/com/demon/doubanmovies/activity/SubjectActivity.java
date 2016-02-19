package com.demon.doubanmovies.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.demon.doubanmovies.adapter.SimpleActorAdapter;
import com.demon.doubanmovies.adapter.SimpleMovieAdapter;
import com.demon.doubanmovies.db.bean.CelebrityEntity;
import com.demon.doubanmovies.db.bean.SimpleActorBean;
import com.demon.doubanmovies.db.bean.SimpleCardBean;
import com.demon.doubanmovies.db.bean.SimpleSubjectBean;
import com.demon.doubanmovies.db.bean.SubjectBean;
import com.demon.doubanmovies.utils.BitmapUtil;
import com.demon.doubanmovies.utils.Constant;
import com.demon.doubanmovies.utils.DensityUtil;
import com.demon.doubanmovies.utils.StringUtil;
import com.demon.doubanmovies.widget.RoundedBackgroundSpan;
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
        SimpleActorAdapter.OnItemClickListener,
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


    @Bind(R.id.cl_container)
    CoordinatorLayout mContainer;
    @Bind(R.id.refresh_subject)
    SwipeRefreshLayout mRefresh;
    @Bind(R.id.btn_subject_skip)
    FloatingActionButton mFloatingButton;
    @Bind(R.id.nested_scroll_view)
    NestedScrollView scrollView;
    //movie header
    @Bind(R.id.header_container_subj)
    AppBarLayout mHeaderContainer;
    @Bind(R.id.toolbar_container_subj)
    CollapsingToolbarLayout mToolbarContainer;
    @Bind(R.id.iv_header_subj)
    ImageView mToolbarImage;
    @Bind(R.id.toolbar_subj)
    Toolbar mToolbar;
    @Bind(R.id.rb_subject_rating)
    RatingBar mRatingBar;
    @Bind(R.id.tv_subject_rating)
    TextView mRating;
    @Bind(R.id.tv_subject_favorite_count)
    TextView mCollect;
    @Bind(R.id.tv_subject_title)
    TextView mTitle;
    @Bind(R.id.tv_subject_genres)
    TextView mGenres;
    @Bind(R.id.tv_subject_countries)
    TextView mCountries;
    @Bind(R.id.movie_container_subj)
    LinearLayout mFilmContainer;
    // movie summary
    @Bind(R.id.tv_summary_hint)
    TextView mSummaryTip;
    @Bind(R.id.tv_subject_summary)
    TextView mSummaryText;

    // movie actor
    @Bind(R.id.tv_subject_actor)
    TextView mActorTip;
    @Bind(R.id.re_subject_actor)
    RecyclerView mActor;

    // movie recommend
    @Bind(R.id.tv_subject_recommend_tip)
    TextView mRecommendTip;
    @Bind(R.id.re_subject_recommend)
    RecyclerView mRecommend;

    // movie subject
    private String mId;
    private String mContent;
    private SubjectBean mSubject;


    private String mActorTags;
    private List<SimpleActorBean> mActorData = new ArrayList<>();
    private SimpleActorAdapter mActorAdapter;

    private String mRecommendTags;
    private List<SimpleCardBean> mRecommendData = new ArrayList<>();
    private SimpleMovieAdapter mRecommendMovieAdapter;
    private boolean isSummaryShow = false;

    private File mFile;
    private boolean isCollect = false;

    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options = MovieApplication.getLoaderOptions();

    private float titleDy = Float.MAX_VALUE;

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
        mSubject = MovieApplication.getDataSource().movieOfId(mId);


        if (mSubject != null) {
            isCollect = true;
            // initAfterGetData();
        }

        volleyGetSubject();
    }

    private void initView() {
        // 设置圆形刷新球的偏移量
        mRefresh.setProgressViewOffset(false,
                -DensityUtil.dp2px(getApplication(), 16f),
                DensityUtil.dp2px(getApplication(), 48f));
        mRefresh.setColorSchemeResources(R.color.green_500);
        mToolbar.setTitle("");

        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (titleDy == Float.MAX_VALUE) {
                    /** 计算显示标题需要滑动的距离 */
                    titleDy = mTitle.getY() + mTitle.getHeight();
                }

                if (scrollY >= titleDy) {
                    if (mSubject.getTitle() != null) {
                        mToolbarContainer.setTitle(mSubject.getTitle());
                    }
                } else {
                    mToolbarContainer.setTitle("");
                }
            }

        });

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        mActor.setLayoutManager(new LinearLayoutManager(SubjectActivity.this,
                LinearLayoutManager.HORIZONTAL, false));
        mActorAdapter = new SimpleActorAdapter(this);
        mActor.setAdapter(mActorAdapter);
        mActorAdapter.update(mActorData);

        mRecommend.setLayoutManager(new LinearLayoutManager(
                SubjectActivity.this, LinearLayoutManager.HORIZONTAL, false));
        mRecommendMovieAdapter = new SimpleMovieAdapter(this);
        mRecommend.setAdapter(mRecommendMovieAdapter);
        mRecommendMovieAdapter.update(mRecommendData);

        mFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), mId + URI_FOR_IMAGE);
        String imageUri = (mFile.exists() ?
                String.format("%s%s", URI_FOR_FILE, mFile.getPath()) :
                getIntent().getStringExtra(KEY_IMAGE_URL));

        imageLoader.displayImage(imageUri, mToolbarImage, options,
                new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view,
                                                  final Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);


                        Bitmap blurBitmap = BitmapUtil.fastBlur(loadedImage, 25);
                        BitmapDrawable drawable = new BitmapDrawable(getResources(), blurBitmap);
                        drawable.setAlpha(192);
                        /** toolbar模糊背景 */
                        mToolbarContainer.setBackground(drawable);
                    }
                });
    }


    private void initEvent() {
        mRefresh.setOnRefreshListener(this);
        mFloatingButton.setOnClickListener(this);
        mRecommendMovieAdapter.setOnItemClickListener(this);
        mRecommendTip.setOnClickListener(this);
        mRecommendTip.setClickable(false);

        mActorAdapter.setOnItemClickListener(this);
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
                        //如果movie已经收藏,更新数据
                        if (isCollect) saveMovie();
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

        if (mSubject.getRating() != null) {
            float rate = (float) (mSubject.getRating().getAverage() / 2);
            mRatingBar.setRating(rate);
            mRating.setText(String.format("%s", rate * 2));
        }

        mCollect.setText(getString(R.string.favorite));
        mCollect.append(String.format("%s", mSubject.getCollect_count()));
        mCollect.append(getString(R.string.count));
        mTitle.setText(String.format("%s   ", mSubject.getTitle()));
        mTitle.append(StringUtil.getSpannableString1(
                String.format("  %s  ", mSubject.getYear()),
                new ForegroundColorSpan(Color.WHITE),
                new RoundedBackgroundSpan(this),
                new RelativeSizeSpan(0.87f)));

        mGenres.setText(StringUtil.getListString(mSubject.getGenres(), '/'));
        mCountries.setText(StringUtil.getSpannableString(
                getString(R.string.countries), getResources().getColor(R.color.gray_black_1000)));
        mCountries.append(StringUtil.getListString(mSubject.getCountries(), '/'));

        mSummaryText.setText(StringUtil.getSpannableString(
                getString(R.string.summary), getResources().getColor(R.color.gray_500)));
        mSummaryText.append(System.getProperty("line.separator"));
        mSummaryText.append(mSubject.getSummary());
        mSummaryText.setEllipsize(TextUtils.TruncateAt.END);
        mSummaryText.setOnClickListener(this);
        mSummaryTip.setOnClickListener(this);

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
        mActorTip.setText(getString(R.string.actor_list));

        int directorCount = mSubject.getDirectors().size();
        for (int i = 0; i < directorCount; i++) {
            CelebrityEntity celebrity = mSubject.getDirectors().get(i);
            if (celebrity.getId() != null)
                mActorData.add(new SimpleActorBean(celebrity, 1));
        }

        for (int i = 0; i < mSubject.getCasts().size(); i++) {
            CelebrityEntity celebrity = mSubject.getCasts().get(i);
            if (celebrity.getId() != null)
                mActorData.add(new SimpleActorBean(celebrity, 3));

        }

        mActorAdapter.update(mActorData);
        mActor.setVisibility(View.VISIBLE);
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
        MenuItem favorite = menu.findItem(R.id.action_sub_favorite);
        if (isCollect) {
            favorite.setIcon(R.drawable.ic_action_collect);
        } else {
            favorite.setIcon(R.drawable.ic_action_uncollected);
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
            case R.id.action_sub_favorite:
                favoriteAndSaveMovie();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 点击收藏后将subject存入数据库中,并将图片存入文件
     */
    private void favoriteAndSaveMovie() {
        if (mSubject == null) return;
        if (isCollect) {
            Snackbar.make(mContainer, getString(R.string.favorite_cancel), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            unsaveMovie();
            isCollect = false;
        } else {
            Snackbar.make(mContainer, getString(R.string.favorite_completed), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            saveMovie();
            isCollect = true;
        }
        supportInvalidateOptionsMenu();
    }

    /**
     * 用于保存content和image
     */
    private void saveMovie() {
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
        // 将电影信息存入到数据库中
        mSubject.setLocalImageFile(mFile.getPath());
        String content = new Gson().toJson(mSubject, Constant.subType);
        MovieApplication.getDataSource().insertOrUpDataFilm(mId, content);
    }

    private void unsaveMovie() {
        // 将数据从数据库中删除
        MovieApplication.getDataSource().deleteFilm(mId);
        // 将保存的海报图片删除
        if (mFile.exists()) mFile.delete();
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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_subject_summary:
            case R.id.tv_summary_hint:
                if (isSummaryShow) {
                    isSummaryShow = false;
                    mSummaryText.setEllipsize(TextUtils.TruncateAt.END);
                    mSummaryText.setLines(5);
                    mSummaryTip.setText(getString(R.string.more_info));
                } else {
                    isSummaryShow = true;
                    mSummaryText.setEllipsize(null);
                    mSummaryText.setSingleLine(false);
                    mSummaryTip.setText(getString(R.string.put_away));
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

    @Override
    public void itemClick(String id, String imageUrl) {
        if (id == null) {
            Snackbar.make(mContainer, getString(R.string.no_detail_info), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else {
            CelebrityActivity.toActivity(SubjectActivity.this, id);
        }
    }

}
