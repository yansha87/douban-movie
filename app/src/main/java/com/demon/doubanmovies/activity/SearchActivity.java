package com.demon.doubanmovies.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.demon.doubanmovies.MovieApplication;
import com.demon.doubanmovies.R;
import com.demon.doubanmovies.adapter.BaseAdapter;
import com.demon.doubanmovies.adapter.SearchAdapter;
import com.demon.doubanmovies.db.bean.SimpleSubjectBean;
import com.demon.doubanmovies.utils.Constant;
import com.demon.doubanmovies.widget.SearchMovieView;
import com.google.gson.GsonBuilder;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity
        implements BaseAdapter.OnItemClickListener, SearchMovieView.OnClearButtonListener {

    private static final String VOLLEY_TAG = "SearchActivity";
    private static final String JSON_SUBJECTS = "subjects";
    private static final String TAG = "SearchActivity";
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.rv_search)
    RecyclerView mRecyclerView;
    @Bind(R.id.tag_layout)
    TagFlowLayout mTagFlowLayout;

    private SearchAdapter mAdapter;
    private List<SimpleSubjectBean> mData;
    //SearchView on the Toolbar;
    private SearchMovieView mSearchView;
    private ProgressDialog mDialog;

    private String[] mVals = new String[]{"美人鱼", "西游记", "功夫熊猫", "澳门风云"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mSearchView = new SearchMovieView(SearchActivity.this);
        mSearchView.setQueryHint(getString(R.string.query_hint));
        mSearchView.setOnQueryTextListener((String query) -> {
            final String url;
            try {
                url = Constant.API + Constant.SEARCH_Q + URLEncoder.encode(query, "UTF-8");
                getDataFromUrl(url);
                if (mDialog == null) {
                    mDialog = new ProgressDialog(SearchActivity.this);
                    mDialog.setMessage(getString(R.string.search_message));
                    mDialog.setCancelable(true);
                    mDialog.setOnCancelListener((DialogInterface dialog) -> {
                        MovieApplication.getHttpQueue().cancelAll(url);
                    });
                }
                mDialog.show();
                mSearchView.clearFocus();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return true;
        });

        mSearchView.setOnQueryTextListener(this);

        // 将SearchMovieView添加到Toolbar
        mToolbar.addView(mSearchView);
        setSupportActionBar(mToolbar);

        // 给左上角图标的左边加上一个返回的图标, 对应ActionBar.DISPLAY_HOME_AS_UP
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mTagFlowLayout.setMaxSelectCount(1);
        mTagFlowLayout.setAdapter(new TagAdapter<String>(mVals) {

            @Override
            public View getView(FlowLayout parent, int position, String text) {
                TextView tv = (TextView) getLayoutInflater().inflate(R.layout.view_tag,
                        mTagFlowLayout, false);
                tv.setText(text);
                return tv;
            }
        });

        mTagFlowLayout.setOnTagClickListener((View view, int position, FlowLayout parent) -> {
            mSearchView.setQueryText(mVals[position]);
            return true;
        });
    }

    private void getDataFromUrl(String url) {
        final String no_result = getString(R.string.search_no_result);
        final String error_result = getString(R.string.search_error);
        JsonObjectRequest request = new JsonObjectRequest(url,
                (JSONObject response) -> {
                    try {
                        String subjects = response.getString(JSON_SUBJECTS);
                        mData = new GsonBuilder().create().fromJson(subjects,
                                Constant.simpleSubTypeList);
                        if (mDialog != null) {
                            mDialog.dismiss();
                            mDialog = null;
                        }
                        if (mData.size() == 0) {
                            Toast.makeText(SearchActivity.this, no_result,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            mAdapter = new SearchAdapter(SearchActivity.this, mData);
                            mAdapter.setOnItemClickListener(SearchActivity.this);
                            mTagFlowLayout.setVisibility(View.GONE);
                            mRecyclerView.setAdapter(mAdapter);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                (VolleyError error) -> {
                    if (mDialog != null) {
                        mDialog.dismiss();
                        mDialog = null;
                    }
                    Toast.makeText(SearchActivity.this, error_result,
                            Toast.LENGTH_SHORT).show();

                });
        MovieApplication.addRequest(request, VOLLEY_TAG);
    }

    @Override
    protected void onStop() {
        super.onStop();
        MovieApplication.removeRequest(VOLLEY_TAG);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return true;
    }

    @Override
    public void onItemClick(String id, String imageUrl, Boolean isMovie) {
        SubjectActivity.toActivity(this, id, imageUrl);
    }

    @Override
    public boolean onClearButtonClick() {
        // 清除 RecyclerView 中的内容
        if (mData != null)
            mData.clear();
        mAdapter.notifyDataSetChanged();
        mTagFlowLayout.setVisibility(View.VISIBLE);
        return true;
    }
}
