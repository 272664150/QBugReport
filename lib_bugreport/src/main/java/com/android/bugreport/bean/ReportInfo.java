package com.android.bugreport.bean;

import com.android.bugreport.core.BugType;

import java.io.Serializable;

public class ReportInfo implements Serializable {
    private static final long serialVersionUID = -4318042087631142549L;

    // 区分不同的用户，只有一个用户时默认为0
    private int mUserId;

    // 设备信息
    private String mDeviceInfo;

    // 异常堆栈
    private String mStackTrace;

    // 异常类型
    private BugType mType;

    // 发生时间
    private long mHappenTime;

    // 是否已上传
    private boolean isUploaded;

    public int getUserId() {
        return mUserId;
    }

    public void setUserId(int userId) {
        this.mUserId = userId;
    }

    public String getDeviceInfo() {
        return mDeviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.mDeviceInfo = deviceInfo;
    }

    public String getStackTrace() {
        return mStackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.mStackTrace = stackTrace;
    }

    public BugType getType() {
        return mType;
    }

    public void setType(BugType type) {
        this.mType = type;
    }

    public long getHappenTime() {
        return mHappenTime;
    }

    public void setHappenTime(long happenTime) {
        this.mHappenTime = happenTime;
    }

    public boolean isUploaded() {
        return isUploaded;
    }

    public void setUploaded(boolean uploaded) {
        isUploaded = uploaded;
    }
}