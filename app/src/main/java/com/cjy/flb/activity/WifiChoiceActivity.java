package com.cjy.flb.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cjy.flb.R;
import com.cjy.flb.bean.BoxVisitTimeBean;
import com.cjy.flb.customView.OsiEditText;
import com.cjy.flb.utils.MHttpUtils;
import com.cjy.flb.utils.SharedPreUtil;
import com.cjy.flb.utils.ToastUtil;
import com.cjy.flb.wifi.AiLinkWrapper;
import com.cjy.flb.wifi.WifiAdmin;
import com.espressif.iot.esptouch.EsptouchTask;
import com.espressif.iot.esptouch.IEsptouchResult;
import com.espressif.iot.esptouch.IEsptouchTask;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.socks.library.KLog;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 欢迎界面
 */
public class WifiChoiceActivity extends BaseActivity implements View.OnClickListener {


    private RelativeLayout ssid;
    private OsiEditText password;
    private TextView ssidTv;
    //wifi管理
    private WifiManager mWifiManager;
    private WifiInfo mWifiInfo;
    private Button next;
    //A-Linik工具
    private boolean mSmartLinkStarted = false;
    private AiLinkWrapper mAiLinkWrapper;
    private IEsptouchTask mEsptouchTask;

    private int seconds = 60;//倒计时秒数
    private Thread timeThread;

    private final WifiChoseHandle mHandler = new WifiChoseHandle(this);

    static class WifiChoseHandle extends Handler {
        WeakReference<Activity> weakReference;

        public WifiChoseHandle(Activity activity) {
            weakReference = new WeakReference<>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            WifiChoiceActivity activity = (WifiChoiceActivity) weakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case 100:
                        activity.endForOpenWifi();
                        break;
                    case 200:
                        activity.countdownDisplay();
                        break;
                    case 300:
                        activity.conSucceed();
                        break;
                    default:
                        break;
                }
            }
            super.handleMessage(msg);
        }
    }

    private void conSucceed() {
        next.setEnabled(true);
        seconds = 60;
        next.setText(getString(R.string.submit_ok));
        String string = getIntent().getExtras().getString("from", "no");
        if (string.equals("main")) {
            finish();
        } else {
            startActivity(new Intent(context, AddFlbActivity.class));
        }
    }

 /*   NetConRun netConRun = new NetConRun(this);

    static class NetConRun implements Runnable {
        WeakReference<Activity> weakReference;

        public NetConRun(Activity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void run() {
            WifiChoiceActivity activity = (WifiChoiceActivity) weakReference.get();
            if (activity != null) {
                activity.mAiLinkWrapper.stop();
                activity.openWifiConnection();
            }
        }
    }*/

    private void countdownDisplay() {
        next.setText(getString(R.string.connecting) + seconds + "s");
        seconds--;
        if (seconds % 5 == 0) {
            onBoxVisitTime();
        }
     /*   if (seconds == 49 || seconds == 9) {//wifi连接
            Thread thread = new Thread(netConRun);
            thread.start();
        }*/
    }


    private void endForOpenWifi() {
//        mAiLinkWrapper.stop();
        next.setEnabled(true);
        seconds = 60;
        next.setText(getString(R.string.retry));
      /*  Thread thread = new Thread(netConRun);
        thread.start();*/
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_wifi_choice);
        initBar(getString(R.string.set_network_connection), false, true);
        tvMore.setVisibility(View.VISIBLE);
        tvMore.setText(getString(R.string.skip));

        ssid = (RelativeLayout) findViewById(R.id.rl_patient);
        ssidTv = (TextView) findViewById(R.id.tv_wifi_ssid);
        password = (OsiEditText) findViewById(R.id.et_wifi_password);
        next = (Button) findViewById(R.id.btn_next);
    }

    @Override
    public void initData() {
        password.initTail();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_wifi_password);
        password.setTitleBitmap(bitmap);

        String string = getIntent().getExtras().getString("from", "no");
        if (string.equals("main")) {
            tvMore.setVisibility(View.GONE);
        }

        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (mWifiManager.isWifiEnabled()) {
            mWifiInfo = mWifiManager.getConnectionInfo();
            ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (mWifiNetworkInfo.isConnected()) {
                String mSSID = mWifiInfo.getSSID();
                if (mSSID.startsWith("\"") && mSSID.endsWith("\"")) {
                    mSSID = mSSID.substring(1, mSSID.length() - 1);
                }
                ssidTv.setText((CharSequence) mSSID);
                password.getEtContentOnly().requestFocus();
            }
        }
    }

    @Override
    public void initListener() {
//        ssid.setOnClickListener(this);
        next.setOnClickListener(this);
        tvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, AddFlbActivity.class));
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_patient:
                Intent openCameraIntent = new Intent(context, WifiListActivity.class);
                startActivityForResult(openCameraIntent, 100);
                break;
            case R.id.btn_next:
                if (TextUtils.isEmpty(password.getEditText())) {
                    ToastUtil.showShort(getString(R.string.info_not_null));
                } else {
                   /* if (next.getText().toString().equals(getString(R.string.retry))) {
                        onBoxVisitTime();
                    }*/
                    showEspTouchToCon();
                    next.setEnabled(false);
                }
                break;
            default:
                break;
        }
    }

    // get the wifi info which is "connected" in wifi-setting
    private String getConnectionInfo() {
        Log.d("JHW=",mWifiInfo.getBSSID());
        return mWifiInfo.getBSSID();
    }

    private void showEspTouchToCon() {
        new EsptouchAsyncTask3().execute(ssidTv.getText().toString(),
                getConnectionInfo(), password.getEditText(), "NO", String.valueOf(1));
    }

 /*   private IEsptouchListener myListener = new IEsptouchListener() {
        @Override
        public void onEsptouchResultAdded(final IEsptouchResult result) {
            onEsptoucResultAddedPerform(result);
        }
    };

    private void onEsptoucResultAddedPerform(final IEsptouchResult result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String text = result.getBssid() + " is connected to the wifi";
            }
        });
    }*/

    private class EsptouchAsyncTask3 extends AsyncTask<String, Void, List<IEsptouchResult>> {

        private IEsptouchTask mEsptouchTask;
        private final Object mLock = new Object();

        @Override
        protected void onPreExecute() {
            timeThread = new Thread() {
                @Override
                public void run() {
                    try {
                        while (!isInterrupted() && seconds > 0) {
                            mHandler.sendEmptyMessage(200);
                            sleep(1000);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        mHandler.sendEmptyMessage(100);
                    }
                }
            };
            timeThread.start();
        }

        @Override
        protected List<IEsptouchResult> doInBackground(String... params) {
            int taskResultCount = 0;
            synchronized (mLock) {
                String apSsid = params[0];
                String apBssid = params[1];
                String apPassword = params[2];
                String isSsidHiddenStr = params[3];
                String taskResultCountStr = params[4];
                boolean isSsidHidden = false;
                if (isSsidHiddenStr.equals("YES")) {
                    isSsidHidden = true;
                }
                taskResultCount = Integer.parseInt(taskResultCountStr);
                mEsptouchTask = new EsptouchTask(apSsid, apBssid, apPassword, isSsidHidden, WifiChoiceActivity.this);
//                mEsptouchTask.setEsptouchListener(myListener);
            }
            List<IEsptouchResult> resultList = mEsptouchTask.executeForResults(taskResultCount);
            for(IEsptouchResult iEsptouchResult:resultList){
                KLog.json("xxxi"+new Gson().toJson(iEsptouchResult));
            }
            return resultList;
        }

        @Override
        protected void onPostExecute(List<IEsptouchResult> result) {

            IEsptouchResult firstResult = result.get(0);
            if (!firstResult.isCancelled()) {
                int count = 0;
                final int maxDisplayCount = 5;
                if (firstResult.isSuc()) {
                    StringBuilder sb = new StringBuilder();
                    for (IEsptouchResult resultInList : result) {
                        sb.append("Esptouch success, bssid = " + resultInList.getBssid()
                                + ",InetAddress = " + resultInList.getInetAddress().getHostAddress() + "\n");
                        count++;
                        if (count >= maxDisplayCount) {
                            break;
                        }
                    }
                    if (count < result.size()) {
                        sb.append("\nthere's " + (result.size() - count) + " more result(s) without showing\n");
                    }
                    mHandler.sendEmptyMessage(300);
                } else {
                    mHandler.sendEmptyMessage(100);
                }
            }
        }
    }

    private void showDialogToCon() {
        if (mSmartLinkStarted) {
            mSmartLinkStarted = false;
            if ("AI-LINK".equalsIgnoreCase("AI-LINK")) {
                mAiLinkWrapper.stop();
            } else {
                if (mEsptouchTask != null) {
                    mEsptouchTask.interrupt();
                }
            }
        } else {
            if (mWifiManager.isWifiEnabled()) {
                if (ssidTv.getText().toString().isEmpty()) {
                    ToastUtil.showShort(getString(R.string.select_wireless));
                } else {
                    if (!password.getEditText().isEmpty()) {
                        if (password.getEditText().length() < 6) {
                            ToastUtil.showShort(getString(R.string.regist_fail_password_long));
                            return;
                        }
                    }

                    if (mAiLinkWrapper == null) {
                        mAiLinkWrapper = new AiLinkWrapper(
                                ssidTv.getText().toString().trim(), password.getEditText(), context);
                    } else {
                        mAiLinkWrapper.setSSID(ssidTv.getText().toString().trim());
                        mAiLinkWrapper.setPassword(password.getEditText());
                    }
                    mAiLinkWrapper.start();

                    timeThread = new Thread() {
                        @Override
                        public void run() {
                            try {
                                while (!isInterrupted() && seconds > 0) {
                                    mHandler.sendEmptyMessage(200);
                                    sleep(1000);
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } finally {
                                mHandler.sendEmptyMessage(100);
                            }
                        }
                    };
                    timeThread.setPriority(10);
                    timeThread.start();
                }
            } else {
                mAiLinkWrapper.stop();
                ToastUtil.showShort(getString(R.string.select_wireless));
            }
        }
    }

    private void openWifiConnection() {
        Log.d("openWifi", Thread.currentThread().getName());
        WifiAdmin.getInstance(context).openWifi();
        WifiConfiguration wc = new WifiConfiguration();
        String SSID = ssidTv.getText().toString();
        wc.SSID = "\"" + SSID + "\"";    //配置wifi的SSID，即该热点的名称，如：TP-link_xxx
        wc.preSharedKey = "\"" + password.getEditText() + "\"";
        wc.hiddenSSID = true;
        wc.status = WifiConfiguration.Status.ENABLED;
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        int res = mWifiManager.addNetwork(wc);
        Log.d("WifiPreference", "add Network returned " + res);
        boolean b = mWifiManager.enableNetwork(res, true);
        Log.d("WifiPreference", "enableNetwork returned " + b);
    }

    private void onBoxVisitTime() {
        RequestParams params = new RequestParams();
        params.addHeader("Authorization", "Basic " + MHttpUtils.REGISTER_BASIC);
        String boxId = SharedPreUtil.getString("FlbID");
        params.addBodyParameter("device_uid", boxId);
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.configDefaultHttpCacheExpiry(0);
       /* try {
            ResponseStream responseStream = httpUtils.sendSync(HttpRequest.HttpMethod.GET,
                    MHttpUtils.VISIT_URL + boxId, params);
            String entity = responseStream.readString();
            KLog.json(entity);
            whetherBoxConnected(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        httpUtils.send(HttpRequest.HttpMethod.GET, MHttpUtils.VISIT_URL + boxId,
                params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        KLog.json(responseInfo.result);
                        try {
                            whetherBoxConnected(responseInfo);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        KLog.d(s);
                    }
                });
    }

    private void whetherBoxConnected(ResponseInfo<String> responseInfo) throws Exception {
        JsonElement jsonElement = new JsonParser().parse(responseInfo.result);
        if (jsonElement != null && jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (jsonObject.has("failure_index")) {
                ToastUtil.showShort(jsonObject.get("failure_index").getAsString());
            } else {
                //                JsonElement e = jsonObject.get("response");
                BoxVisitTimeBean bean = new Gson().fromJson(jsonElement, BoxVisitTimeBean.class);
                //2016-03-26T11:52:54.297+08:00
                String lastVisit = bean.getResponse().getLast_visit();
                KLog.i(lastVisit);
                String[] s = lastVisit.split("T");
                String day = s[0];
                String time = s[1].subSequence(0, 8).toString();
                KLog.i(day + "====" + time);

                StringBuilder sb = new StringBuilder();
                sb.append(day).append(time);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss", Locale.SIMPLIFIED_CHINESE);
                long myTime = sdf.parse(sdf.format(new Date())).getTime();
                long boxTime = sdf.parse(sb.toString()).getTime();
                if ((myTime - boxTime) <= 1000 * 60 * 12) {//时间间隔在40s内有效
                    mHandler.sendEmptyMessage(300);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //处理扫描结果（在界面上显示）
        if (resultCode == RESULT_OK && requestCode == 100 && data != null) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("result", "no ~ ~");
            ssidTv.setText(scanResult);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (timeThread != null) {
            timeThread.interrupt();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timeThread != null) {
            timeThread.interrupt();
        }
    }
}
