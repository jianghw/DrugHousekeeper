package com.cjy.flb.adapter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cjy.flb.R;

import java.util.List;


public class WifiListViewAdapter extends BaseAdapter {

    private List<ScanResult> datas;
    private Context context;
    private WifiManager mWifiManager;
    private WifiInfo connInfo;
    ConnectivityManager cm;

    public void setDatas(List<ScanResult> datas)
    {
        this.datas = datas;
        connInfo = mWifiManager.getConnectionInfo();
    }

    public WifiListViewAdapter(Context context, List<ScanResult> datas)
    {
        //		super();
        this.datas = datas;
        this.context = context;
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        connInfo = mWifiManager.getConnectionInfo();
        cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Override
    public int getCount()
    {
        if (datas == null) {
            return 0;
        }
        return datas.size();
    }

    @Override
    public Object getItem(int position)
    {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Holder tag = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.wifi_info_item, null);
            tag = new Holder();
            tag.txtWifiName = (TextView) convertView.findViewById(R.id.txt_wifi_name);
            tag.txtWifiDesc = (TextView) convertView.findViewById(R.id.txt_wifi_desc);
            tag.imgWifiLevelIco = (ImageView) convertView.findViewById(R.id.img_wifi_level_ico);
            convertView.setTag(tag);
        } else {
            // 设置数据
            tag = (Holder) convertView.getTag();
        }
        // Wifi 名字
        tag.txtWifiName.setText(datas.get(position).SSID);
        // Wifi 描述
        String desc = "";
        String descOri = datas.get(position).capabilities;
        if (descOri.toUpperCase().contains("WPA-PSK")) {
            desc = "WPA";
        }
        if (descOri.toUpperCase().contains("WPA2-PSK")) {
            desc = "WPA2";
        }
        if (descOri.toUpperCase().contains("WPA-PSK")
                && descOri.toUpperCase().contains("WPA2-PSK")) {
            desc = "WPA/WPA2";
        }
        if (TextUtils.isEmpty(desc)) {
            desc = context.getString(R.string.unprotected_network);
        } else {
            desc = context.getString(R.string.through) + desc + context.getString(R.string.to_protect);
        }
        connInfo = mWifiManager.getConnectionInfo();

        State wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        if (wifi == State.CONNECTED) {
            WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
            String g1 = wifiInfo.getSSID();
            String g2 = "\"" + datas.get(position).SSID + "\"";

            if (g2.endsWith(g1)) {
                desc = context.getString(R.string.connected);
                // tag.txtWifiDesc.setTextColor(context.getResources().getColor(R.color.textdec));
            }
        }

        tag.txtWifiDesc.setText(desc);

        // 网络信号强度
        int level = datas.get(position).level;
        int imgId = R.drawable.wifi05;
        if (Math.abs(level) > 100) {
            imgId = R.drawable.wifi05;
        } else if (Math.abs(level) > 80) {
            imgId = R.drawable.wifi04;
        } else if (Math.abs(level) > 70) {
            imgId = R.drawable.wifi04;
        } else if (Math.abs(level) > 60) {
            imgId = R.drawable.wifi03;
        } else if (Math.abs(level) > 50) {
            imgId = R.drawable.wifi02;
        } else {
            imgId = R.drawable.wifi02;
        }
        tag.imgWifiLevelIco.setImageResource(imgId);
        return convertView;
    }

    public static class Holder {
        public TextView txtWifiName;
        public TextView txtWifiDesc;
        public ImageView imgWifiLevelIco;
    }
}