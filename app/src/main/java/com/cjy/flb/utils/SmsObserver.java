package com.cjy.flb.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;

import com.socks.library.KLog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/3/14 0014.
 */
public class SmsObserver extends ContentObserver {
    private Context context;
    private final ContentResolver resolver;
    private final SmsHandler handler;

    public SmsObserver(Context context, ContentResolver resolver,
                       SmsHandler smsHandler)
    {
        super(smsHandler);
        this.context = context;
        this.resolver = resolver;
        this.handler = smsHandler;
    }

    @Override
    public void onChange(boolean selfChange)
    {
        super.onChange(selfChange);
        //Uri uri, String[] projection,String selection, String[] selectionArgs, String
        // sortOrder
       /* Cursor cursor = resolver.query(Uri.parse("content://sms/inbox"),
                new String[]{"_id", "address", "read", "body", "thread_id"},
                "read=?", new String[]{"0"}, "date desc");*/
        Cursor cursor = resolver.query(Uri.parse("content://sms/inbox"),
                new String[]{"_id", "address", "read", "body", "thread_id"},
                "read=0", null, "date desc");
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
                KLog.i(cursor.getColumnCount());
                KLog.i(_smsInfo_address);//电话号
                KLog.i(_smsInfo_body);//信息
                KLog.i(_smsInfo_read);
                if ("0".equals(_smsInfo_read)) {
//                    String code = _smsInfo_body.split(":")[1].split("，")[0];

                    Pattern p = Pattern.compile("(\\d+)");
                    Matcher matcher = p.matcher(_smsInfo_body);
                    StringBuilder sb = new StringBuilder();
                    while (matcher.find()) {
                        String find = matcher.group(1);
                        sb.append(find);
                    }

                    Message message = Message.obtain();
                    message.what = 1;
                    Bundle bundle = new Bundle();
                    bundle.putString("code", sb.substring(0, 4));
                    message.setData(bundle);
                    handler.sendMessage(message);
                    break;
                }
            }
        }
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
    }
}
