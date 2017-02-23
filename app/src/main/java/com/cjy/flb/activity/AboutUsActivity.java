package com.cjy.flb.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.cjy.flb.R;
import com.cjy.flb.utils.AppUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2015/12/7 0007.
 */
public class AboutUsActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.tv_versionName)
    TextView mTvVersionName;
    @Bind(R.id.textView)
    TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_aboutus);
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        initBar(getString(R.string.about_us), false, false);
        mTextView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//下划线
        mTvVersionName.setText(AppUtils.getVerName(this));

    }

    @Override
    public void initListener() {
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AboutUsActivity.this, DisclaimerActivity.class));
            }
        });
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
                break;
            case R.id.textView_normal:
                break;
            case R.id.textView_small:
                break;
            default:
                break;
        }
    }
}
