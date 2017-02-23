package com.cjy.flb.receiver;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * Created by Administrator on 2016/1/28 0028.
 */
public class AsyncHandler {
    private static final HandlerThread sHandlerThread=new HandlerThread("AsyncHandler");
    private static final Handler sHandler;

    static {
        sHandlerThread.start();
        sHandler=new Handler(sHandlerThread.getLooper());
    }

    public static void post(Runnable r){
        sHandler.post(r);
    }

    private AsyncHandler(){}
}
