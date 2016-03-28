package com.demon.doubanmovies.activity.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import butterknife.ButterKnife;


public abstract class BaseAppCompatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.setContentView(this.getLayoutId());
        ButterKnife.bind(this);

        this.initToolbar(savedInstanceState);
        this.initViews(savedInstanceState);
        this.initDatas();
        this.initListeners();
    }

    /**
     * @return inflater layout id
     */
    protected abstract int getLayoutId();

    /**
     * initial views
     * @param savedInstanceState saved instance state
     */
    protected abstract void initViews(Bundle savedInstanceState);

    /**
     * initial toolbar
     * @param savedInstanceState saved instance state
     */
    protected abstract void initToolbar(Bundle savedInstanceState);

    /**
     * initial event listener
     */
    protected abstract void initListeners();

    /**
     * initial data
     */
    protected abstract void initDatas();

    @SuppressWarnings("unused")
    protected <V extends View> V findView(int id) {
        return (V) this.findViewById(id);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
    }

}
