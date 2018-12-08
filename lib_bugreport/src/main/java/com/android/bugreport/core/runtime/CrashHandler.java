package com.android.bugreport.core.runtime;

import android.content.Context;
import android.util.Log;

import com.android.bugreport.bean.ReportInfo;
import com.android.bugreport.cache.ACache;
import com.android.bugreport.config.Constant;
import com.android.bugreport.core.BugType;
import com.android.bugreport.util.DeviceUtils;
import com.android.bugreport.util.ReportUtils;

import java.io.IOException;
import java.util.concurrent.Executors;

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private Context mContext;
    private Thread.UncaughtExceptionHandler mUncaughtExceptionHandler;

    private void CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final CrashHandler instance = new CrashHandler();
    }

    public void init(Context context) {
        if (context == null) {
            return;
        }

        mContext = context.getApplicationContext();
        mUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        saveUncaughtExceptionInfo(e);

        if (mUncaughtExceptionHandler != null) {
            mUncaughtExceptionHandler.uncaughtException(t, e);
        } else {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
    }

    /**
     * 保存crash信息到LRU缓存
     *
     * @param throwable
     */
    private void saveUncaughtExceptionInfo(final Throwable throwable) {
        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                ReportInfo reportInfo = new ReportInfo();
                try {
                    reportInfo.setDeviceInfo(DeviceUtils.getDeviceInfos(mContext).toString());
                    reportInfo.setStackTrace(ReportUtils.throwableToString(throwable));
                    reportInfo.setHappenTime(System.currentTimeMillis());
                    reportInfo.setType(BugType.RUNTIME);
                    reportInfo.setUploaded(false);

                    ACache cache = ACache.get(mContext, Constant.DEFAULT_DISK_LRU_CACHE_SIZE, Constant.DEFAULT_DISK_LRU_CACHE_COUNT);
                    cache.put(String.valueOf(reportInfo.getHappenTime()), reportInfo);

                    Log.d("test", "crash:   " + ReportUtils.throwableToString(throwable));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
