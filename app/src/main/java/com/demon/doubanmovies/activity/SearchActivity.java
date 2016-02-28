package com.demon.doubanmovies.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.demon.doubanmovies.R;
import com.demon.doubanmovies.activity.base.BaseToolbarActivity;
import com.demon.doubanmovies.adapter.SearchAdapter;
import com.demon.doubanmovies.db.bean.SimpleSubjectBean;
import com.demon.doubanmovies.douban.DataManager;
import com.demon.doubanmovies.widget.SearchMovieView;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import butterknife.Bind;
import rx.Subscriber;

public class SearchActivity extends BaseToolbarActivity {

    private static final String TAG = "SearchActivity";

    @Bind(R.id.rv_search)
    RecyclerView mRecyclerView;
    @Bind(R.id.tag_layout)
    TagFlowLayout mTagFlowLayout;

    private SearchAdapter mAdapter;
    private List<SimpleSubjectBean> mData;
    // SearchView on the Toolbar;
    private SearchMovieView mSearchView;
    private ProgressDialog mDialog;

    private String[] mSearchTags = new String[]{"美人鱼", "西游记", "功夫熊猫", "澳门风云"};

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mSearchView = new SearchMovieView(SearchActivity.this);
        mSearchView.setQueryHint(getString(R.string.query_hint));

        // 将SearchMovieView添加到Toolbar
        mToolbar.addView(mSearchView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        // 设置允许选择有且一个tag
        mTagFlowLayout.setMaxSelectCount(1);
    }

    @Override
    protected void initListeners() {
        mSearchView.setOnQueryClearListener(() -> {
            // 清除 RecyclerView 中的内容
            if (mData != null)
                mData.clear();

            if (mAdapter != null)
                mAdapter.notifyDataSetChanged();
            mTagFlowLayout.setVisibility(View.VISIBLE);
            return true;
        });

        mSearchView.setOnQueryChangeListener((String query) -> {
            try {
                loadSearchData(URLEncoder.encode(query, "UTF-8"));
                if (mDialog == null) {
                    mDialog = new ProgressDialog(SearchActivity.this);
                    mDialog.setMessage(getString(R.string.search_message));
                    mDialog.setCancelable(true);
                    //mDialog.setOnCancelListener((DialogInterface dialog) -> {
                    //    MovieApplication.getHttpQueue().cancelAll(url);
                    //});
                }
                mDialog.show();
                mSearchView.clearFocus();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return true;
        });

        mTagFlowLayout.setOnTagClickListener((View view, int position, FlowLayout parent) -> {
            mSearchView.setQueryText(mSearchTags[position]);
            return true;
        });
    }

    @Override
    protected void initData() {
        mTagFlowLayout.setAdapter(new TagAdapter<String>(mSearchTags) {

            @Override
            public View getView(FlowLayout parent, int position, String text) {
                TextView tv = (TextView) getLayoutInflater().inflate(R.layout.view_tag,
                        mTagFlowLayout, false);
                tv.setText(text);
                return tv;
            }
        });
    }

    private void loadSearchData(String url) {
        DataManager.getInstance().getSearchData(url)
                .subscribe(new Subscriber<List<SimpleSubjectBean>>() {
                    @Override
                    public void onCompleted() {
                        if (mDialog != null) {
                            mDialog.dismiss();
                            mDialog = null;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mDialog != null) {
                            mDialog.dismiss();
                            mDialog = null;
                        }

                        Toast.makeText(SearchActivity.this, R.string.search_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(List<SimpleSubjectBean> simpleSubjectBeans) {
                        mData = simpleSubjectBeans;
                        if (mData.size() == 0) {
                            Toast.makeText(SearchActivity.this, R.string.search_no_result,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            mAdapter = new SearchAdapter(mRecyclerView, mData);
                            mAdapter.setOnItemClickListener((String id, String imageUrl, Boolean isFilm) -> {
                                SubjectActivity.toActivity(SearchActivity.this, id, imageUrl);
                            });
                            mTagFlowLayout.setVisibility(View.GONE);
                            mRecyclerView.setAdapter(mAdapter);
                        }
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return true;
    }

}
