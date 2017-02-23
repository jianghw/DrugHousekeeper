package com.cjy.flb.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.cjy.flb.R;
import com.cjy.flb.manager.AppManager;
import com.cjy.flb.utils.SharedPreUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2015/12/7 0007.
 */
public class FontSizeActivity extends BaseActivity implements View.OnClickListener {


    @Bind(R.id.textView_big)
    RelativeLayout mTextViewBig;
    @Bind(R.id.textView_normal)
    RelativeLayout mTextViewNormal;
    @Bind(R.id.textView_small)
    RelativeLayout mTextViewSmall;
    @Bind(R.id.img_big)
    ImageView mImgBig;
    @Bind(R.id.img_normal)
    ImageView mImgNomar;
    @Bind(R.id.img_small)
    ImageView mImgSmall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_font_size);
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        initBar(getString(R.string.font), false, false);
        int mode = SharedPreUtil.getInteger("Theme_Font", 2);
        if (mode == 1) {
            mImgSmall.setVisibility(View.VISIBLE);
        } else if (mode == -1 || mode == 2) {
            mImgNomar.setVisibility(View.VISIBLE);
        } else if (mode == 3) {
            mImgBig.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void initListener() {
        mTextViewBig.setOnClickListener(this);
        mTextViewNormal.setOnClickListener(this);
        mTextViewSmall.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textView_big:
                SharedPreUtil.setInteger("Theme_Font", 3);
                AppManager.getAppManager().finishActivity(FontSizeActivity.class);
                AppManager.getAppManager().finishActivity(MainActivity.class);
                startActivity(new Intent(FontSizeActivity.this, MainActivity.class));
                break;
            case R.id.textView_normal:
                SharedPreUtil.setInteger("Theme_Font", 2);
                AppManager.getAppManager().finishActivity(FontSizeActivity.class);
                AppManager.getAppManager().finishActivity(MainActivity.class);
                startActivity(new Intent(FontSizeActivity.this, MainActivity.class));
                break;
            case R.id.textView_small:
                SharedPreUtil.setInteger("Theme_Font", 1);
                AppManager.getAppManager().finishActivity(FontSizeActivity.class);
                AppManager.getAppManager().finishActivity(MainActivity.class);
                startActivity(new Intent(FontSizeActivity.this, MainActivity.class));
                break;
            default:
                break;
        }
    }
}
