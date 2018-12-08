package com.android.bugreport.core.anr;

import android.app.ActivityManager;
import android.content.Context;
import android.os.FileObserver;
import android.util.Log;

import com.android.bugreport.bean.ReportInfo;
import com.android.bugreport.cache.ACache;
import com.android.bugreport.config.Constant;
import com.android.bugreport.core.BugType;
import com.android.bugreport.util.DeviceUtils;
import com.android.bugreport.util.ReportUtils;

import java.io.File;
import java.util.List;
import java.util.concurrent.Executors;

public class ANRFileObserver extends FileObserver {
    private static final int MAX_TIMES_ANR_INFO_POLLING = 50;
    private static final int DORMANT_TIME_WAIT_ANR_INFO = 200;

    private Context mContext;
    private long mLastModified;

    private volatile static ANRFileObserver sFileObserver;

    private ANRFileObserver(Context context, String path, int mask) {
        super(path, mask);
        if (context != null) {
            mContext = context.getApplicationContext();
        }
    }

    public static ANRFileObserver getInstance(Context context) {
        if (sFileObserver == null) {
            synchronized (ANRFileObserver.class) {
                if (sFileObserver == null) {
                    sFileObserver = new ANRFileObserver(context, Constant.ANR_DIRECTORY_PATH, ALL_EVENTS);
                }
            }
        }
        return sFileObserver;
    }

    @Override
    public void onEvent(int event, String path) {
        if (path == null) {
            return;
        }

        path = Constant.ANR_DIRECTORY_PATH + path;
        if (!path.contains(Constant.ANR_FILE_NAME) || event != CLOSE_WRITE) { // 只关注CLOSE_WRITE事件类型，其它类型无意义
            return;
        }

        long lastModified = lastModified(path);
        if (mContext == null || mLastModified == lastModified) { // 由于traces.txt文件中包含多个进程的堆栈信息，而主进程的堆栈信息保存在最前面，获取到anr信息后，直接过滤掉其它进程的信息
            return;
        }

        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (int i = 0; i < MAX_TIMES_ANR_INFO_POLLING; i++) { // 由于发生anr向traces.txt写入进程信息耗时，需要等待才能获取到anr信息
            try {
                Thread.sleep(DORMANT_TIME_WAIT_ANR_INFO); // 理由同上
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            List<ActivityManager.ProcessErrorStateInfo> errorStateInfoList = am.getProcessesInErrorState();
            if (errorStateInfoList == null || errorStateInfoList.isEmpty()) {
                continue;
            }

            ActivityManager.ProcessErrorStateInfo errorStateInfo = errorStateInfoList.get(0);
            if (errorStateInfo.condition == 2) { // NO_ERROR 0、CRASHED 1、NOT_RESPONDING 2
                mLastModified = lastModified; // 获取到主进程的堆栈信息后，修改mLastModified，用于过滤其它进程的信息
                Executors.newSingleThreadExecutor().submit(new Runnable() {
                    @Override
                    public void run() {
                        ReportInfo reportInfo = new ReportInfo();
                        reportInfo.setDeviceInfo(DeviceUtils.getDeviceInfos(mContext).toString());
                        reportInfo.setStackTrace(ReportUtils.getStackTraceInfo());
                        reportInfo.setHappenTime(System.currentTimeMillis());
                        reportInfo.setType(BugType.ANR);
                        reportInfo.setUploaded(false);

                        ACache cache = ACache.get(mContext, Constant.DEFAULT_DISK_LRU_CACHE_SIZE, Constant.DEFAULT_DISK_LRU_CACHE_COUNT);
                        cache.put(String.valueOf(reportInfo.getHappenTime()), reportInfo);
                    }
                });

                // 在此可获取到ANR原因描述、堆栈信息等，参考Bugly做展示
                Log.d("test", "shortMsg:   " + errorStateInfo.shortMsg);
                Log.d("test", "longMsg:   " + errorStateInfo.longMsg);
                Log.d("test", "stackTraceInfo:   " + ReportUtils.getStackTraceInfo());
                break;
            }
        }
    }

    /**
     * 获取traces.txt文件的最近修改时间：每次发生anr都会重新创建文件，并写入各进程的堆栈信息，且各进程的修改时间都相同
     *
     * @param path
     * @return
     */
    private long lastModified(String path) {
        long time = 0L;
        File localFile = new File(path);
        if (localFile.exists()) {
            time = localFile.lastModified();
        }
        return time;
    }
}
