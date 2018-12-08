package com.android.bugreport.config;

public class Constant {

    public static int DEFAULT_DISK_LRU_CACHE_COUNT = 50;
    public static int DEFAULT_DISK_LRU_CACHE_SIZE = 5 * 1024 * 1024;
    public static int DEFAULT_TIME_INTERVAL_OF_UPLOAD = 60 * 60 * 1000 / 2;

    public static final String PACKAGE_NAME = "packageName";
    public static final String VERSION_NAME = "versionName";
    public static final String VERSION_CODE = "versionCode";

    public static final String ANR_FILE_NAME = "traces.txt";
    public static final String ANR_DIRECTORY_PATH = "/data/anr/";
    public static final String ACTION_ALARM_UPLOAD_REPORT_INFO = "action.alarm.UPLOAD_REPORT_INFO";

    //TODO Default upload server URL
    public static String DEFAULT_URL_UPLOAD_TO_SERVER = "";
}
