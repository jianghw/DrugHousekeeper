package com.cjy.flb.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.cjy.flb.R;
import com.cjy.flb.adapter.WifiListViewAdapter;
import com.cjy.flb.wifi.WifiAdmin;
import com.socks.library.KLog;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;

/**
 * 显示wifi信息页面
 */
public class WifiListActivity extends BaseActivity {

    private WifiAdmin mWifiAdmin;
    private ListView wifiList;
    private WifiListViewAdapter mAdapter;
    private List<ScanResult> tmpList;//扫描信息
    private List<ScanResult> list;//扫描信息

    private static final int REFRESH_CONN = 100;//刷新连接
    private static final int REFRESH_NOCONN = 200;
    private static final int REFRESH_LIST = 300;//刷新数据

    @Override
    public void initView()
    {
        setContentView(R.layout.activity_wifi_list);
        initBar(getString(R.string.wireless_connection), false, false);

        wifiList = (ListView) findViewById(R.id.wifi_listview);
    }

    @Override
    public void initData()
    {
        if (mWifiAdmin == null) {
            mWifiAdmin = WifiAdmin.getInstance(this);
        }
        mAdapter = new WifiListViewAdapter(this, list);
        wifiList.setAdapter(mAdapter);

        mHandler.sendEmptyMessage(REFRESH_CONN);
    }

    private void getWifiListInfo()
    {
        mWifiAdmin.openWifi();
        mWifiAdmin.startScan();
        tmpList = mWifiAdmin.getWifiList();
        KLog.i(Arrays.toString(tmpList.toArray()));
        if (tmpList == null) {
            list.clear();
        } else {
            list = tmpList;
        }
        mHandler.sendEmptyMessage(REFRESH_LIST);
    }

    private final WifiHandler mHandler = new WifiHandler(this);

    static class WifiHandler extends Handler {
        WeakReference<Activity> weakReference;

        public WifiHandler(Activity activity)
        {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg)
        {
            WifiListActivity activity = (WifiListActivity) weakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case REFRESH_CONN:
                        activity.getWifiListInfo();
                        break;
                    case REFRESH_NOCONN:
                        activity.onClearList();
                        break;
                    case REFRESH_LIST:
                        activity.refershList();
                        break;
                    default:
                        break;
                }
            }
            super.handleMessage(msg);
        }
    }

    private void refershList()
    {
        mAdapter.setDatas(list);
        mAdapter.notifyDataSetChanged();
    }

    private void onClearList()
    {
        list.clear();
        mAdapter.setDatas(list);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    public void initListener()
    {
        wifiList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent resultIntent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("result", list.get(position).SSID);
                resultIntent.putExtras(bundle);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

}
