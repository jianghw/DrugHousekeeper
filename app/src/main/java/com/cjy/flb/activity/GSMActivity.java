package com.cjy.flb.activity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.cjy.flb.R;

/**
 * 显示wifi信息页面
 */
public class GSMActivity extends BaseActivity {


    private Button next;

    @Override
    public void initView() {
        setContentView(R.layout.activity_box_gsm);
        initBar(getString(R.string.box_gsm_title), false, false);
        next = (Button) findViewById(R.id.btn_next);
    }

    @Override
    public void initData() {
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
