package com.demon.doubanmovies.fragment;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.demon.doubanmovies.MyApplication;
import com.demon.doubanmovies.R;
import com.demon.doubanmovies.activity.SubjectActivity;
import com.demon.doubanmovies.adapter.AnimatorListenerAdapter;
import com.demon.doubanmovies.adapter.BaseAdapter;
import com.demon.doubanmovies.adapter.BoxAdapter;
import com.demon.doubanmovies.adapter.SimpleSubjectAdapter;
import com.demon.doubanmovies.bean.BoxSubjectBean;
import com.demon.doubanmovies.bean.SimpleSubjectBean;
import com.demon.doubanmovies.utils.DensityUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.support.v7.widget.RecyclerView.OnClickListener;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;
import static com.demon.doubanmovies.utils.Constant.API;
import static com.demon.doubanmovies.utils.Constant.COMING;
import static com.demon.doubanmovies.utils.Constant.IN_THEATERS;
import static com.demon.doubanmovies.utils.Constant.US_BOX;
import static com.demon.doubanmovies.utils.Constant.simpleBoxTypeList;
import static com.demon.doubanmovies.utils.Constant.simpleSubTypeList;

public class HomePagerFragment extends Fragment implements BaseAdapter.OnItemClickListener {

    private static final String AUTO_REFRESH = "auto refresh?";
    private static final String LAST_RECORD = "last record";
    private static final int RECORD_COUNT = 20;

    private static final String JSON_TOTAL = "total";
    private static final String JSON_SUBJECTS = "subjects";
    private static final String KEY_FRAGMENT_TITLE = "title";

    private static final int POS_IN_THEATERS = 0;
    private static final int POS_COMING = 1;
    private static final int POS_US_BOX = 2;

    private static final String[] TYPE = {"in theaters", "coming", "us box"};

    private static final String VOLLEY_TAG = "HomePagerFragment";
    private static final String TAG = "HomePagerFragment";

    @Bind(R.id.rv_fragment)
    RecyclerView mRecyclerView;
    @Bind(R.id.fresh_fragment)
    SwipeRefreshLayout mRefresh;
    @Bind(R.id.btn_fragment)
    FloatingActionButton mBtn;

    private String mDataString;
    private SimpleSubjectAdapter mSubjectAdapter;
    private BoxAdapter mBoxAdapter;
    private List<SimpleSubjectBean> mSimpleData = new ArrayList<>();
    private List<BoxSubjectBean> mBoxData = new ArrayList<>();
    private OnScrollListener mScrollListener;

    private int mTitlePos;
    private String mRequestUrl;
    private int mTotalItem;
    private boolean isFirstRefresh = true;
    private int mStart = 0;

    private SharedPreferences mSharePreferences;

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
        mRefresh.setColorSchemeResources(R.color.colorPrimary);
        mRefresh.setProgressViewOffset(
                false, 0, DensityUtil.dp2px(getContext(), 32f));
        mSharePreferences = getActivity().getSharedPreferences(
                LAST_RECORD, Context.MODE_PRIVATE);
        initData();
        initEvent();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (isFirstRefresh || sharedPreferences.getBoolean(AUTO_REFRESH, false)) {
            updateFilmData();
            isFirstRefresh = false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        MyApplication.removeRequest(VOLLEY_TAG + mTitlePos);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void initData() {
        switch (mTitlePos) {
            case POS_IN_THEATERS:
                initSimpleRecyclerView(false);
                break;
            case POS_COMING:
                initSimpleRecyclerView(true);
                break;
            case POS_US_BOX:
                initBoxRecyclerView();
                break;
            default:
        }
    }

    private void initEvent() {
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mStart = 0;
                updateFilmData();
            }
        });
        mBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRecyclerView.getAdapter() != null) {
                    mRecyclerView.scrollToPosition(0);
                }
            }
        });
    }

    /**
     * 初始化“正在上映”和“即将上映”对应的fragment
     */
    private void initSimpleRecyclerView(boolean isComing) {
        int padding = DensityUtil.dp2px(getContext(), 2f);
        setPaddingForRecyclerView(-padding);

        Resources res = getResources();
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), res.getInteger(R.integer.num_columns)));

        // LinearLayoutManager inManager = new LinearLayoutManager(getActivity());
        // inManager.setOrientation(LinearLayoutManager.VERTICAL);
        // mRecyclerView.setLayoutManager(inManager);

        //请求网络数据前先加载上次的电影数据
        mSubjectAdapter = new SimpleSubjectAdapter(getActivity(), mSimpleData, isComing);
        if (getRecord() != null) {
            mSimpleData = new Gson().fromJson(getRecord(), simpleSubTypeList);
            mSubjectAdapter.updateList(mSimpleData, RECORD_COUNT);
        }
        mSubjectAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mSubjectAdapter);
    }

    /**
     * 初始化“北美票房”对应的fragment
     */
    private void initBoxRecyclerView() {
        int padding = DensityUtil.dp2px(getContext(), 2f);
        setPaddingForRecyclerView(padding);

        GridLayoutManager boxManager = new GridLayoutManager(getActivity(), 3);
        mRecyclerView.setLayoutManager(boxManager);
        //请求网络数据前先加载上次的电影数据
        if (getRecord() != null) {
            mBoxData = new Gson().fromJson(getRecord(), simpleBoxTypeList);
        }
        mBoxAdapter = new BoxAdapter(getActivity(), mBoxData);
        mBoxAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mBoxAdapter);
    }

    /**
     * 为RecyclerView设置-2dp的padding用于抵消item的margin
     */
    private void setPaddingForRecyclerView(int padding) {
        // mRecyclerView.setPadding(padding, padding, padding, padding);
    }

    //更新电影数据
    private void updateFilmData() {
        switch (mTitlePos) {
            case POS_IN_THEATERS:
                mRequestUrl = API + IN_THEATERS;
                volley_Get_Coming();
                break;
            case POS_COMING:
                mRequestUrl = API + COMING;
                volley_Get_Coming();
                break;
            case POS_US_BOX:
                mRequestUrl = API + US_BOX;
                volley_Get_USBox();
                break;
        }
    }

    /**
     * 通过Volley框架的全局消息队列获取到url对应的数据
     */
    private void volley_Get_Coming() {
        mRefresh.setRefreshing(true);
        JsonObjectRequest request = new JsonObjectRequest(mRequestUrl,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            mTotalItem = response.getInt(JSON_TOTAL);
                            mDataString = response.getString(JSON_SUBJECTS);
                            mSimpleData = new Gson().fromJson(mDataString, simpleSubTypeList);
                            mSubjectAdapter.updateList(mSimpleData, mTotalItem);
                            //实现recyclerView的下拉刷新
                            setOnScrollListener();
                            saveRecord();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            mRefresh.setRefreshing(false);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                        mRefresh.setRefreshing(false);
                    }
                });
        MyApplication.addRequest(request, VOLLEY_TAG + mTitlePos);
    }

    /**
     * 为RecyclerView设置下拉刷新及floatingActionButton的消失出现
     */
    private void setOnScrollListener() {
        if (mRecyclerView == null) {
            return;
        }
        if (mScrollListener == null) {
            mScrollListener = new OnScrollListener() {
                int lastVisibleItem;
                boolean isShow = false;

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView,
                                                 int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == SCROLL_STATE_IDLE
                            && lastVisibleItem + 2 > mSubjectAdapter.getItemCount()
                            && mSubjectAdapter.getItemCount() - 1 < mSubjectAdapter.getTotalDataCount()) {
                        loadMore();
                    }
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    LinearLayoutManager lm = (LinearLayoutManager) HomePagerFragment.this.mRecyclerView.getLayoutManager();
                    lastVisibleItem = lm.findLastVisibleItemPosition();
                    if (lm.findFirstVisibleItemPosition() == 0) {
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
            };
            mRecyclerView.addOnScrollListener(mScrollListener);
        }
    }

    /**
     * adapter加载更多
     */
    private void loadMore() {
        //防止出现多次加载的情况
        if (mSubjectAdapter.getStart() == mStart) return;
        mStart = mSubjectAdapter.getStart();
        String url = mRequestUrl + ("?start=" + mStart);
        JsonObjectRequest request = new JsonObjectRequest(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    List<SimpleSubjectBean> moreData = new GsonBuilder().create().fromJson(
                            response.getString(JSON_SUBJECTS), simpleSubTypeList);
                    mSubjectAdapter.loadMoreData(moreData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mSubjectAdapter.loadFail();
            }
        });
        MyApplication.addRequest(request, VOLLEY_TAG + mTitlePos);
    }

    /**
     * 通过Volley框架的全局消息队列获取到url对应的数据
     */
    private void volley_Get_USBox() {
        mRefresh.setRefreshing(true);
        JsonObjectRequest request = new JsonObjectRequest(mRequestUrl,
                new Response.Listener<JSONObject>() {
                    private Gson gson = new GsonBuilder().create();

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            mDataString = response.getString(JSON_SUBJECTS);
                            mBoxData = gson.fromJson(mDataString,
                                    simpleBoxTypeList);
                            mBoxAdapter.updateList(mBoxData);
                            saveRecord();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            mRefresh.setRefreshing(false);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                        mRefresh.setRefreshing(false);
                    }
                });
        MyApplication.addRequest(request, VOLLEY_TAG + mTitlePos);
    }

    @Override
    public void onItemClick(String id, String imageUrl) {
        if (id.equals(SimpleSubjectAdapter.FOOT_VIEW_ID)) {
            loadMore();
        } else {
            SubjectActivity.toActivity(getActivity(), id, imageUrl);
        }
    }

    /**
     * 当网络不好或中断时用以显示上一次加载的数据
     */
    private String getRecord() {
        return mSharePreferences.getString(TYPE[mTitlePos], null);
    }

    /**
     * 保存上一次网络请求得到的数据
     */
    private void saveRecord() {
        if (mDataString != null) {
            Editor edit = mSharePreferences.edit();
            edit.putString(TYPE[mTitlePos], mDataString);
            edit.apply();
        }
    }

    /**
     * 为floatingActionBar的出现消失设置动画效果
     */
    private void animatorForGone() {
        Animator anim = AnimatorInflater.loadAnimator(getActivity(), R.animator.scale_gone);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                mBtn.setVisibility(View.GONE);
            }
        });
        anim.setTarget(mBtn);
        anim.start();
    }

    private void animatorForVisible() {
        Animator anim = AnimatorInflater.loadAnimator(getActivity(), R.animator.scale_visible);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animator) {
                mBtn.setVisibility(View.VISIBLE);
            }
        });
        anim.setTarget(mBtn);
        anim.start();
    }
}
