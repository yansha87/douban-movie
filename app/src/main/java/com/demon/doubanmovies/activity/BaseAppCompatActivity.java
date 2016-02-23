package com.demon.doubanmovies.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import butterknife.ButterKnife;


public abstract class BaseAppCompatActivity extends AppCompatActivity {

    private static final String TAG = "BaseAppCompatActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.setContentView(this.getLayoutId());
        ButterKnife.bind(this);

        this.initToolbar(savedInstanceState);
        this.initViews(savedInstanceState);
        this.initData();
        this.initListeners();
    }

    protected abstract int getLayoutId();

    protected abstract void initViews(Bundle savedInstanceState);

    protected abstract void initToolbar(Bundle savedInstanceState);

    protected abstract void initListeners();

    protected abstract void initData();

    protected <V extends View> V findView(int id) {
        return (V) this.findViewById(id);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
    }

}
