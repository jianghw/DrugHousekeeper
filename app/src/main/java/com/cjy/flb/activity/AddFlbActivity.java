package com.cjy.flb.activity;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;

import com.cjy.flb.R;
import com.cjy.flb.utils.MHttpUtils;
import com.cjy.flb.utils.SharedPreUtil;
import com.cjy.flb.utils.ToastUtil;

import java.lang.ref.WeakReference;

public class AddFlbActivity extends BaseActivity implements View.OnClickListener {

    private Button btnAddFlb;
    private int count = 60;
    private Thread threadTime;


    private final AddFlbHandler mHandler = new AddFlbHandler(this);

    static class AddFlbHandler extends Handler {
        WeakReference<Activity> weakReference;

        public AddFlbHandler(Activity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            AddFlbActivity activity = (AddFlbActivity) weakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case 100:
                        ToastUtil.showShort(activity.getString(R.string.add_box_ok));
                        activity.threadTime.interrupt();
                        break;
                    case 200:
                        activity.countdownDisplay();
                        break;
                    case 400:
                        ToastUtil.showShort(activity.getString(R.string.add_box_error));
                        break;
                    case 500:
                        activity.setRepeat();//重设
                        break;
                    default:
                        break;
                }
            }
            super.handleMessage(msg);
        }
    }

    private void countdownDisplay() {
        String mId = SharedPreUtil.getString("FlbID");
        String mSn = SharedPreUtil.getString("FlbSN");
        if (count % 5 == 0) {
            MHttpUtils.addFromFlb(mId, mSn, context, mHandler);
        }
        btnAddFlb.setText(getString(R.string.add_box_connection) + count + "s");
        count--;
    }

    private void setRepeat() {
        try {
            btnAddFlb.setText(getString(R.string.add_box_retry));
            btnAddFlb.setEnabled(true);
            count = 60;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_add_flb);
        initBar(getString(R.string.add_medicine_box), false, false);
    }

    @Override
    public void initData() {
        btnAddFlb = (Button) findViewById(R.id.btn_addFlb);
    }

    @Override
    public void initListener() {
        btnAddFlb.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        threadTime = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted() && count > 0) {
                        mHandler.sendEmptyMessage(200);
                        sleep(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    mHandler.sendEmptyMessage(500);
                }
            }
        };
        threadTime.setPriority(10);
        threadTime.start();
        btnAddFlb.setEnabled(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (threadTime != null)
            threadTime.interrupt();
    }
}
