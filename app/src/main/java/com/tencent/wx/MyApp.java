package com.tencent.wx;

import android.app.Application;

import com.tencent.wx.framework.FrameworkManager;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FrameworkManager.getInstance().initIfNeed(getApplicationContext());
    }
}
