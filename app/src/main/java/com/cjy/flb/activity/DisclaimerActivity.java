package com.cjy.flb.activity;

import android.os.Bundle;

import com.cjy.flb.R;

/**
 * Created by Administrator on 2016/1/5 0005.
 */
public class DisclaimerActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_disclaimer);
        initBar(getString(R.string.privacy_rolicy), false, false);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {

    }
}
