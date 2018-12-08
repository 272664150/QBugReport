package com.android.bugreport.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.android.bugreport.config.Constant;
import com.android.bugreport.util.ReportUtils;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // 接收到定时上传广播时，上传cache中未标记的日志到服务器
        if (Constant.ACTION_ALARM_UPLOAD_REPORT_INFO.equals(intent.getAction())) {
            ReportUtils.uploadReportInfoToServer(context);
        }
    }
}
