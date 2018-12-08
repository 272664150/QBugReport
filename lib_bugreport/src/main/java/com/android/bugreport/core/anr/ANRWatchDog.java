package com.android.bugreport.core.anr;

import android.content.Context;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import com.android.bugreport.bean.ReportInfo;
import com.android.bugreport.cache.ACache;
import com.android.bugreport.config.Constant;
import com.android.bugreport.core.BugType;
import com.android.bugreport.util.DeviceUtils;
import com.android.bugreport.util.ReportUtils;

public class ANRWatchDog extends Thread {
    private static final String TAG = ANRWatchDog.class.getSimpleName();

    private Context mContext;
    private boolean isIgnoreDebugger = true;
    private int mDefaultTimeoutFrequency = 30000;
    private Handler mUIHandler = new Handler(Looper.getMainLooper());
    private ANRWatchDog.AnrChecker mAnrChecker = new ANRWatchDog.AnrChecker();

    private volatile static ANRWatchDog sWatchDog;

    private ANRWatchDog(Context context) {
        mContext = context.getApplicationContext();
    }

    public static ANRWatchDog getInstance(Context context) {
        if (sWatchDog == null) {
            synchronized (ANRWatchDog.class) {
                if (sWatchDog == null) {
                    sWatchDog = new ANRWatchDog(context);
                }
            }
        }
        return sWatchDog;
    }

    @Override
    public void run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND); // 后台线程
        while (!isInterrupted()) {
            synchronized (this) {
                mAnrChecker.schedule();
                long waitTime = mDefaultTimeoutFrequency;
                long start = SystemClock.uptimeMillis();
                while (waitTime > 0) {
                    try {
                        wait(waitTime);
                    } catch (InterruptedException e) {
                        Log.w(TAG, e.toString());
                    }
                    waitTime = mDefaultTimeoutFrequency - (SystemClock.uptimeMillis() - start);
                }
                if (!mAnrChecker.isBlocked()) {
                    continue;
                }
            }

            if (!isIgnoreDebugger && Debug.isDebuggerConnected()) {
                continue;
            }

            ReportInfo reportInfo = new ReportInfo();
            reportInfo.setDeviceInfo(DeviceUtils.getDeviceInfos(mContext).toString());
            reportInfo.setStackTrace(ReportUtils.getStackTraceInfo());
            reportInfo.setHappenTime(System.currentTimeMillis());
            reportInfo.setType(BugType.ANR);
            reportInfo.setUploaded(false);

            ACache cache = ACache.get(mContext, Constant.DEFAULT_DISK_LRU_CACHE_SIZE, Constant.DEFAULT_DISK_LRU_CACHE_COUNT);
            cache.put(String.valueOf(reportInfo.getHappenTime()), reportInfo);

            Log.d("test", "stackTraceInfo:   " + ReportUtils.getStackTraceInfo());
        }
    }

    private class AnrChecker implements Runnable {
        private boolean mCompleted;
        private long mStartTime;
        private long mExecuteTime = SystemClock.uptimeMillis();

        @Override
        public void run() {
            synchronized (ANRWatchDog.this) {
                mCompleted = true;
                mExecuteTime = SystemClock.uptimeMillis();
            }
        }

        private void schedule() {
            mCompleted = false;
            mStartTime = SystemClock.uptimeMillis();
            mUIHandler.postAtFrontOfQueue(this);
        }

        boolean isBlocked() {
            return !mCompleted || mExecuteTime - mStartTime >= 5000;
        }
    }
}
