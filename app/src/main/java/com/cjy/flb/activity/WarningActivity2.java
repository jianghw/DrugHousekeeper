package com.cjy.flb.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cjy.flb.R;
import com.cjy.flb.manager.AppManager;
import com.cjy.flb.utils.SharedPreUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/1/29 0029.
 */
public class WarningActivity2 extends BaseActivity {
    @Bind(R.id.tv_ok)
    ImageView mTvOk;

    @Override
    public void initView() {
//        boolean mode = SharedPreUtil.getBoolean(this, "first", true);
//        if(!mode){
//            startActivity(new Intent(context, SplashActivity.class));
//            AppManager.getAppManager().finishActivity(WarningActivity2.class);
//        }
        setContentView(R.layout.activity_warning2);
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
    }

    @Override
    public void initListener() {
        mTvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, SplashActivity.class));
                AppManager.getAppManager().finishActivity(WarningActivity2.class);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
