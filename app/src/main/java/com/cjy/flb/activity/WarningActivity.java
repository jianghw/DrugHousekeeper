package com.cjy.flb.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.cjy.flb.R;
import com.cjy.flb.utils.SharedPreUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/1/29 0029.
 */
public class WarningActivity extends BaseActivity {
    @Bind(R.id.tv_ok)
    ImageView mTvOk;

    @Override
    public void initView() {
        boolean mode = SharedPreUtil.getBoolean("first", true);
        if(!mode){
            startActivity(new Intent(context, SplashActivity.class));
           finish();
        }
        setContentView(R.layout.activity_warning);
        ButterKnife.bind(this);
    }
    @Override
    public void initData() {
    }
    @Override
    public void initListener() {
        mTvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SharedPreUtil.setBoolean("first", false);
                startActivity(new Intent(context, WarningActivity1.class));
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
