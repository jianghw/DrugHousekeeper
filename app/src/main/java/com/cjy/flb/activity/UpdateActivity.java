package com.cjy.flb.activity;

import android.os.Bundle;
import android.view.View;

import com.cjy.flb.R;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2015/12/7 0007.
 */
public class UpdateActivity extends BaseActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_problem);
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        initBar(getString(R.string.check_for_updates),false,false);
    }

    @Override
    public void initListener() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.textView_big:
                break;
            case R.id.textView_normal:
                break;
            case R.id.textView_small:
                break;
        }
    }
}
