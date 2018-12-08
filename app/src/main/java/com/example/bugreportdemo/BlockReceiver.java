package com.example.bugreportdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BlockReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Thread.sleep(600000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
