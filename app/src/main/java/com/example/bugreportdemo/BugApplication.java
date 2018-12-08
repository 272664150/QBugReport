package com.example.bugreportdemo;

import android.app.Application;

import com.android.bugreport.QBugReport;

public class BugApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        QBugReport.init(this, "xxx");
    }
}
