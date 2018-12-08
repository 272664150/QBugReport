package com.android.bugreport;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextUtils;

import com.android.bugreport.config.Constant;
import com.android.bugreport.core.anr.ANRFileObserver;
import com.android.bugreport.core.anr.ANRWatchDog;
import com.android.bugreport.core.runtime.CrashHandler;

public class QBugReport {

    private QBugReport() {
    }

    /**
     * 初始化sdk
     *
     * @param context
     * @param serverUrl
     */
    public static void init(Context context, String serverUrl) {
        // 自定义上传的服务器URL
        if (!TextUtils.isEmpty(serverUrl)) {
            Constant.DEFAULT_URL_UPLOAD_TO_SERVER = serverUrl;
        }

        // 初始化crash信息捕获功能，保存在LRU磁盘缓存
        CrashHandler.getInstance().init(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 从android 6.0开始FileObserver监听data/anr目录失效
            ANRWatchDog.getInstance(context).start();
        } else {
            // 监听data/anr目录内容的变化，FileObserver几乎无性能损耗
            ANRFileObserver.getInstance(context).startWatching();
        }

        // 每次打开app后，设置一个间隔0.5h的定时任务，上传cache中未标记的日志到服务器
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent();
        intent.setAction(Constant.ACTION_ALARM_UPLOAD_REPORT_INFO);
        intent.setPackage(context.getPackageName());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setWindow(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), Constant.DEFAULT_TIME_INTERVAL_OF_UPLOAD, pendingIntent);
        } else {
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), Constant.DEFAULT_TIME_INTERVAL_OF_UPLOAD, pendingIntent);
        }
    }
}
