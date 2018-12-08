package com.android.bugreport.util;

import android.content.Context;
import android.os.Looper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.Executors;

public class ReportUtils {

    private ReportUtils() {
    }

    /**
     * 获取主线程的堆栈信息
     *
     * @return
     */
    public static String getStackTraceInfo() {
        StringBuilder stringBuilder = new StringBuilder();
        StackTraceElement[] stackTraceElements = Looper.getMainLooper().getThread().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            stringBuilder.append(stackTraceElement.toString()).append("\r\n");
        }
        return stringBuilder.toString();
    }

    /**
     * 将异常信息转化成字符串
     *
     * @param throwable
     * @return
     * @throws IOException
     */
    public static String throwableToString(Throwable throwable) throws IOException {
        if (throwable == null) {
            return "";
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            throwable.printStackTrace(new PrintStream(baos));
        } finally {
            baos.close();
        }
        return baos.toString();
    }

    /**
     * 上传异常日志到服務器
     *
     * @param context
     */
    public static void uploadReportInfoToServer(Context context) {
        if (isUploadable(context)) {
            return;
        }

        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                // TODO 调用上传接口
                // Constant.DEFAULT_URL_UPLOAD_TO_SERVER
            }
        });
    }

    /**
     * 当前环境是否支持上传
     *
     * @param context
     */
    public static boolean isUploadable(Context context) {
        if (context == null) {
            return false;
        }
        if (!DeviceUtils.hasPermission(context)) {
            return false;
        }
        if (!DeviceUtils.isWifiConnected(context) && !DeviceUtils.is4GConnected(context)) {
            return false;
        }
        return true;
    }
}
