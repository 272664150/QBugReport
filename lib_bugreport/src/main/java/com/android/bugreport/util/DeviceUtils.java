package com.android.bugreport.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.android.bugreport.config.Constant;

import org.json.JSONObject;

import java.lang.reflect.Field;

public class DeviceUtils {

    private DeviceUtils() {
    }

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable() && mNetworkInfo.isConnected();
            }
        }
        return false;
    }

    public static boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable() && mNetworkInfo.isConnected();
            }
        }
        return false;
    }

    public static boolean is4GConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable() && mNetworkInfo.isConnected() && (mNetworkInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_LTE);
            }
        }
        return false;
    }

    public static boolean hasPermission(Context context) {
        if (context != null) {
            boolean b1 = context.checkCallingOrSelfPermission("android.permission.INTERNET") == PackageManager.PERMISSION_GRANTED;
            boolean b2 = context.checkCallingOrSelfPermission("android.permission.READ_PHONE_STATE") == PackageManager.PERMISSION_GRANTED;
            boolean b3 = context.checkCallingOrSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED;
            boolean b4 = context.checkCallingOrSelfPermission("android.permission.ACCESS_NETWORK_STATE") == PackageManager.PERMISSION_GRANTED;
            boolean b5 = context.checkCallingOrSelfPermission("android.permission.ACCESS_WIFI_STATE") == PackageManager.PERMISSION_GRANTED;
            if (!b1 || !b2 || !b3 || !b4 || !b5) {
                Toast.makeText(context.getApplicationContext(), "没有添加权限", Toast.LENGTH_SHORT).show();
            }
            return b1 && b2 && b3 && b4 && b5;
        }
        return false;
    }

    /**
     * 通过反射获取设备所有信息
     *
     * @return
     */
    public static JSONObject getDeviceInfos(Context context) {
        JSONObject deviceInfo = new JSONObject();
        if (context == null) {
            return deviceInfo;
        }

        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                deviceInfo.put(Constant.PACKAGE_NAME, pi.packageName);
                deviceInfo.put(Constant.VERSION_NAME, pi.versionName);
                deviceInfo.put(Constant.VERSION_CODE, pi.versionCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                deviceInfo.put(field.getName(), field.get(null));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return deviceInfo;
    }
}
