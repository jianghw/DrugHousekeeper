package com.cjy.flb.activity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cjy.flb.R;

/**
 * 显示wifi信息页面
 */
public class GSMActivityByMain extends BaseActivity {


    private Button next;
    private TextView tv;

    @Override
    public void initView() {
        setContentView(R.layout.activity_box_gsm);
        initBar(getString(R.string.box_gsm_title), false, false);

        next = (Button) findViewById(R.id.btn_next);
        tv = (TextView) findViewById(R.id.tv_gsm_prompt);
    }

    @Override
    public void initData() {
        if (true) {
            next.setVisibility(View.GONE);
            tv.setText(getString(R.string.box_gsm_main));
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void initListener() {
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, AddFlbActivity.class));
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
