package com.cjy.flb.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.cjy.flb.R;
import com.cjy.flb.service.AlarmClockService;
import com.cjy.flb.utils.PollingUtils;

/**
 * Created by Administrator on 2015/12/8 0008.
 */
public class TimeOutDialogActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = View.inflate(this, R.layout.timeout_alert_dialog, null);
        Button callBtn = (Button) view.findViewById(R.id.btn_call);
        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        Button iBtn = (Button) view.findViewById(R.id.btn_ignore);
        iBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PollingUtils.stopPollingService(TimeOutDialogActivity.this, AlarmClockService.class, AlarmClockService.ACTION);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        AlertDialog mDialog = builder.create();
        mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);//设定为系统级警告，关键
        mDialog.show();
        mDialog.setCanceledOnTouchOutside(false);
    }
}
