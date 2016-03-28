package com.demon.doubanmovies.fragment;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.demon.doubanmovies.MovieApplication;
import com.demon.doubanmovies.R;
import com.demon.doubanmovies.activity.SubjectActivity;
import com.demon.doubanmovies.adapter.SubjectAdapter;
import com.demon.doubanmovies.adapter.base.BaseAdapter;
import com.demon.doubanmovies.douban.DataManager;
import com.demon.doubanmovies.model.bean.CnMovieBean;
import com.demon.doubanmovies.model.bean.SimpleSubjectBean;
import com.demon.doubanmovies.model.bean.UsSubjectBean;
import com.demon.doubanmovies.model.realm.SimpleSubject;
import com.demon.doubanmovies.utils.Constant;
import com.demon.doubanmovies.utils.DensityUtil;
import com.demon.doubanmovies.utils.PrefsUtil;
import com.demon.doubanmovies.utils.RealmUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import rx.Subscriber;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;
import static com.demon.doubanmovies.utils.Constant.COMING;
import static com.demon.doubanmovies.utils.Constant.IN_THEATERS;
import static com.demon.doubanmovies.utils.Constant.POS_COMING;
import static com.demon.doubanmovies.utils.Constant.POS_IN_THEATERS;
import static com.demon.doubanmovies.utils.Constant.POS_TOP250;
import static com.demon.doubanmovies.utils.Constant.POS_US_BOX;
import static com.demon.doubanmovies.utils.Constant.TOP250;
import static com.demon.doubanmovies.utils.Constant.simpleSubTypeList;
import static com.demon.doubanmovies.utils.Constant.title2TypeDict;

public class HomePagerFragment extends Fragment implements BaseAdapter.OnItemClickListener {

    private static final int RECORD_COUNT = 20;

    private static final String KEY_FRAGMENT_TITLE = "title";
    private static final String[] TYPE = {"in theaters", "coming", "top", "us box"};
    private static final String TAG = "HomePagerFragment";

    @Bind(R.id.rv_fragment)
    RecyclerView mRecyclerView;
    @Bind(R.id.fresh_fragment)
    SwipeRefreshLayout mRefresh;
    @Bind(R.id.btn_fragment)
    FloatingActionButton mFloatingButton;

    private SubjectAdapter mSubjectAdapter;

    private int mTitlePos;
    private boolean isFirstRefresh = true;
    private int mStart = 0;

    public static HomePagerFragment newInstance(int titlePos) {
        HomePagerFragment fragment = new HomePagerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_FRAGMENT_TITLE, titlePos);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTitlePos = getArguments().getInt(KEY_FRAGMENT_TITLE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base, container, false);
        ButterKnife.bind(this, view);

        initData();
        initEvent();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFirstRefresh || PrefsUtil.getAutoRefresh(getActivity())) {
            updateMovieData();
            isFirstRefresh = false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void initData() {
        mRefresh.setColorSchemeResources(R.color.colorPrimary);
        mRefresh.setProgressViewOffset(
                false, 0, DensityUtil.dp2px(getContext(), 32f));
        switch (mTitlePos) {
            case POS_IN_THEATERS:
            case POS_TOP250:
            case POS_US_BOX:
                initSimpleRecyclerView(false);
                break;
            case POS_COMING:
                initSimpleRecyclerView(true);
                break;
            default:
        }
    }

    private void initEvent() {
        mRefresh.setOnRefreshListener(() -> {
            mStart = 0;
            updateMovieData();
        });
        mFloatingButton.setOnClickListener((View view) -> {
            if (mRecyclerView.getAdapter() != null) {
                mRecyclerView.scrollToPosition(0);
            }
        });

        mRecyclerView.addOnScrollListener(
                new OnScrollListener() {
                    int lastVisibleItem;
                    boolean isShow = false;

                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView,
                                                     int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        if (newState == SCROLL_STATE_IDLE
                                && lastVisibleItem + 2 > mSubjectAdapter.getItemCount()
                                && mSubjectAdapter.getItemCount() - 1 < mSubjectAdapter.getTotalDataCount()) {
                            loadMoreMovieData();
                        }
                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                        lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                        if (layoutManager.findFirstVisibleItemPosition() == 0) {
                            if (isShow) {
                                animatorForGone();
                                isShow = false;
                            }
                        } else if (dy < -50 && !isShow) {
                            animatorForVisible();
                            isShow = true;
                        } else if (dy > 20 && isShow) {
                            animatorForGone();
                            isShow = false;
                        }
                    }
                }
        );
    }

    /**
     * initial RecyclerView
     *
     * @param isComing is coming movie or not
     */
    private void initSimpleRecyclerView(boolean isComing) {
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(),
                getResources().getInteger(R.integer.num_columns));
        mRecyclerView.setLayoutManager(layoutManager);

        // request load last movie data
        List<SimpleSubjectBean> mSimpleData = new ArrayList<>();
        mSubjectAdapter = new SubjectAdapter(getActivity(), mSimpleData, isComing);
        mSimpleData = new Gson().fromJson(getRecord(), simpleSubTypeList);
        mSubjectAdapter.updateList(mSimpleData, RECORD_COUNT);

        mSubjectAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mSubjectAdapter);
    }


    /**
     * update movie data
     */
    private void updateMovieData() {
        switch (mTitlePos) {
            case POS_IN_THEATERS:
                loadMovieData(IN_THEATERS);
                break;
            case POS_COMING:
                loadMovieData(COMING);
                break;
            case POS_TOP250:
                loadMovieData(TOP250);
                break;
            case POS_US_BOX:
                loadUSMovieData();
                break;
        }
    }

    private void loadMovieData(String type) {
        mRefresh.setRefreshing(true);
        DataManager.getInstance().getMovieData(type, 0)
                .subscribe(new Subscriber<CnMovieBean>() {
                    @Override
                    public void onCompleted() {
                        mRefresh.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mRefresh.setRefreshing(false);
                    }

                    @Override
                    public void onNext(CnMovieBean bean) {
                        mSubjectAdapter.updateList(bean.subjects, bean.total);
                        saveRecord(bean.subjects);
                    }
                });
    }

    /**
     * load more movie data
     */
    private void loadMoreMovieData() {
        if (mSubjectAdapter.getStart() == mStart) return;
        mStart = mSubjectAdapter.getStart();

        String type = title2TypeDict.get(mTitlePos);
        DataManager.getInstance().getMovieData(type, mStart)
                .subscribe(new Subscriber<CnMovieBean>() {
                    @Override
                    public void onCompleted() {
                        mRefresh.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mSubjectAdapter.loadFailHint();
                    }

                    @Override
                    public void onNext(CnMovieBean bean) {
                        mSubjectAdapter.loadMoreData(bean.subjects);
                    }
                });
    }


    /**
     * load American box movie data
     */
    private void loadUSMovieData() {
        mRefresh.setRefreshing(true);

        DataManager.getInstance().getMovieData()
                .map(usMovieBean -> usMovieBean.subjects)
                .subscribe(new Subscriber<List<UsSubjectBean>>() {
                    @Override
                    public void onCompleted() {
                        mRefresh.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: " + e.toString());
                        mRefresh.setRefreshing(false);
                    }

                    @Override
                    public void onNext(List<UsSubjectBean> usSubjectBeen) {
                        List<SimpleSubjectBean> beanList = new ArrayList<>();
                        for (UsSubjectBean bean : usSubjectBeen) {
                            beanList.add(bean.subject);
                        }

                        mSubjectAdapter.updateList(beanList, beanList.size());
                        saveRecord(beanList);
                    }
                });
    }

    @Override
    public void onItemClick(String id, String imageUrl, Boolean isMovie) {
        if (id.equals(SubjectAdapter.FOOT_VIEW_ID)) {
            loadMoreMovieData();
        } else {
            SubjectActivity.toActivity(getActivity(), id, imageUrl);
        }
    }

    /**
     * load last loading data when network not ok
     *
     * @return last loading data
     */
    private String getRecord() {
        SimpleSubject subject = Realm.getDefaultInstance().where(SimpleSubject.class)
                .equalTo(Constant.SIMPLE_SUBJECT_ID, TYPE[mTitlePos]).findFirst();
        if (subject != null) {
            return subject.getJsonStr();
        }

        return "";
    }

    /**
     * save last loading data into database
     *
     * @param beanList data need to save
     */
    private void saveRecord(List<SimpleSubjectBean> beanList) {
        String jsonStr = MovieApplication.gson.toJson(beanList, simpleSubTypeList);
        RealmUtil.saveRecord(TYPE[mTitlePos], jsonStr, Constant.SAVE_COMMON);
    }

    /**
     * animation for FloatingActionButton disappear
     */
    private void animatorForGone() {
        Animator anim = AnimatorInflater.loadAnimator(getActivity(), R.animator.scale_gone);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                mFloatingButton.setVisibility(View.GONE);
            }
        });
        anim.setTarget(mFloatingButton);
        anim.start();
    }

    /**
     * animator for FloatingActionButton appear
     */
    private void animatorForVisible() {
        Animator anim = AnimatorInflater.loadAnimator(getActivity(), R.animator.scale_visible);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animator) {
                mFloatingButton.setVisibility(View.VISIBLE);
            }
        });
        anim.setTarget(mFloatingButton);
        anim.start();
    }
}
