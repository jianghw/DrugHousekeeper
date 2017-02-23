package com.cjy.flb.activity;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cjy.flb.R;

public class ChooseSexActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvMan;
    private TextView tvWoman;
    protected static String sex= null;

    @Override
    public void initView() {
        setContentView(R.layout.activity_choose_sex);

        getWindow().setLayout(RelativeLayout.LayoutParams.FILL_PARENT,
                RelativeLayout.LayoutParams.FILL_PARENT);
    }

    @Override
    public void initData() {
        tvMan = (TextView) findViewById(R.id.tv_man);
        tvWoman = (TextView) findViewById(R.id.tv_woman);
    }

    @Override
    public void initListener() {
        tvMan.setOnClickListener(this);
        tvWoman.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_man:
                sex = getString(R.string.tv_man);
                finish();
                break;

            case R.id.tv_woman:
                sex = getString(R.string.tv_woman);
                finish();
                break;
            default:
                sex = getString(R.string.tv_man);
                break;
        }
    }
}
