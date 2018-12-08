package com.android.bugreport.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

import com.android.bugreport.util.ReportUtils;

public class WifiReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
            switch (wifiState) {
                // 连接到wifi时，上传cache中未标记的日志到服务器
                case WifiManager.WIFI_STATE_ENABLED:
                    ReportUtils.uploadReportInfoToServer(context);
                    break;
                default:
                    break;
            }
        }
    }
}
