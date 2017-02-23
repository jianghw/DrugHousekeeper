package com.cjy.flb.utils;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.cjy.flb.activity.PhoneRegistActivity2;
import com.cjy.flb.activity.RetrievePhoneActivity;

/**
 * Created by Administrator on 2016/3/14 0014.
 */
public class SmsHandler extends Handler {
    private final Context context;
    private int flag=0;

    public SmsHandler(Context context, int i)
    {
        this.context = context;
        this.flag = i;
    }

    @Override
    public void handleMessage(Message msg)
    {
        super.handleMessage(msg);
        switch (msg.what) {
            case 1:
                Bundle bundle = msg.getData();
                String code = bundle.getString("code", "");
                //                etRegistAuthCode.setEtContent(code);
                switch (flag) {
                    case 1:
                        PhoneRegistActivity2 activity = (PhoneRegistActivity2) context;
                        activity.setEtContent(code);
                        break;
                    case 2:
                        RetrievePhoneActivity activity2 = (RetrievePhoneActivity) context;
                        activity2.setEtContent(code);
                        break;
                }
                break;
        }
    }
}
