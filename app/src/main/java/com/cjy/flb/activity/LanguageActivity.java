package com.cjy.flb.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.cjy.flb.R;
import com.cjy.flb.manager.AppManager;
import com.cjy.flb.utils.SharedPreUtil;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2015/12/7 0007.
 */
public class LanguageActivity extends BaseActivity implements View.OnClickListener {


    @Bind(R.id.tv_system)
    RelativeLayout mTvSystem;
    @Bind(R.id.tv_zh)
    RelativeLayout mTvZh;
    @Bind(R.id.tv_en)
    RelativeLayout mTvEn;
    @Bind(R.id.img_system)
    ImageView mImgBig;
    @Bind(R.id.img_zh)
    ImageView mImgNomar;
    @Bind(R.id.img_en)
    ImageView mImgSmall;

    private Resources resources;
    private Configuration config;
    private DisplayMetrics dm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_language);
        ButterKnife.bind(this);
        initBar(getString(R.string.language_selection), false, false);
        resources = getResources();// 获得res资源对象
        config = resources.getConfiguration();// 获得设置对象
        dm = resources.getDisplayMetrics();
    }

    @Override
    public void initData() {
        int mode = SharedPreUtil.getInteger("Language", 0);
        if (mode == 0) {
            mImgBig.setVisibility(View.VISIBLE);
        } else if (mode == 1) {
            mImgNomar.setVisibility(View.VISIBLE);
        } else if (mode == 2) {
            mImgSmall.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void initListener() {
        mTvSystem.setOnClickListener(this);
        mTvZh.setOnClickListener(this);
        mTvEn.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_system:
                if (Locale.getDefault().getLanguage().equals("zh")) {
                    config.locale = Locale.SIMPLIFIED_CHINESE;
                    resources.updateConfiguration(config, dm);
                    recreate();
                } else if (Locale.getDefault().getLanguage().equals("en") ||
                        Locale.getDefault().getLanguage().equals("us")) {
                    config.locale = Locale.US;
                    resources.updateConfiguration(config, dm);
                    recreate();
                }
                SharedPreUtil.setInteger("Language", 0);
                AppManager.getAppManager().finishActivity(LanguageActivity.class);
                AppManager.getAppManager().finishActivity(MainActivity.class);
                startActivity(new Intent(LanguageActivity.this, MainActivity.class));
                break;
            case R.id.tv_zh:
                config.locale = Locale.SIMPLIFIED_CHINESE;
                resources.updateConfiguration(config, dm);
                SharedPreUtil.setInteger("Language", 1);
                AppManager.getAppManager().finishActivity(LanguageActivity.class);
                AppManager.getAppManager().finishActivity(MainActivity.class);
                startActivity(new Intent(LanguageActivity.this, MainActivity.class));
                break;
            case R.id.tv_en:
                config.locale = Locale.US;
                resources.updateConfiguration(config, dm);
                SharedPreUtil.setInteger("Language", 2);
                AppManager.getAppManager().finishActivity(LanguageActivity.class);
                AppManager.getAppManager().finishActivity(MainActivity.class);
                startActivity(new Intent(LanguageActivity.this, MainActivity.class));
                startActivity(new Intent(LanguageActivity.this, MainActivity.class));
                break;
            default:
                break;
        }
    }
}
