package com.cjy.flb.service;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.socks.library.KLog;

/**
 * Created by Administrator on 2016/3/10 0010.
 */
public class SmsService extends Service {
    private SmsObserver mObserver;
    private ContentResolver resolver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //
        resolver = getContentResolver();
        mObserver = new SmsObserver(resolver, new SmsHandler(this));
        resolver.registerContentObserver(Uri.parse("context://sms"), true, mObserver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        resolver.unregisterContentObserver(mObserver);
    }

    private class SmsObserver extends ContentObserver {
        private final ContentResolver resolver;
        private final SmsHandler handler;

        public SmsObserver(ContentResolver resolver, SmsHandler smsHandler) {
            super(smsHandler);
            this.resolver = resolver;
            this.handler = smsHandler;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            //Uri uri, String[] projection,String selection, String[] selectionArgs, String sortOrder
            Cursor cursor = resolver.query(Uri.parse("content://sms/inbox"),
                    new String[]{"_id", "address", "read", "body", "thread_id"},
                    "read=?", new String[]{"0"}, "date desc");
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int _inIndex = cursor.getColumnIndex("_id");
                    String _smsInfo_id = cursor.getString(_inIndex);

                    int address = cursor.getColumnIndex("address");
                    String _smsInfo_address = cursor.getString(address);

                    int body = cursor.getColumnIndex("body");
                    String _smsInfo_body = cursor.getString(body);

                    int read = cursor.getColumnIndex("read");
                    String _smsInfo_read = cursor.getString(read);

                    KLog.i(_smsInfo_id);
                    KLog.i(_smsInfo_address);//电话号
                    KLog.i(_smsInfo_body);//信息
                    KLog.i(_smsInfo_read);
                }
            }
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
    }

    private class SmsHandler extends Handler {
        private final Context context;

        public SmsHandler(Context context) {
            this.context = context;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }
}
