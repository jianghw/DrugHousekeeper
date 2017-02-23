package com.cjy.flb.wifi;

import android.content.Context;

import com.vowstar.iot.ailink.AiLink;


/**
 * Created by vowstar on 2015/4/5.
 */
public class AiLinkWrapper {
    private AiLink mAilink;
    public AiLinkWrapper(String SSID, String password, Context context)
    {
        mAilink = new AiLink(SSID, password, context);
    }
    public boolean start() {
        return mAilink.start();
    }
    public boolean stop() {
        return mAilink.stop();
    }
    public boolean setSSID(String ssid) {
        return  mAilink.setSSID(ssid);
    }
    public boolean setPassword(String password) {
        return  mAilink.setPassword(password);
    }
}
