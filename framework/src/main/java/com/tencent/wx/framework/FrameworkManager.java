package com.tencent.wx.framework;

import android.content.Context;

import com.zhy.autolayout.config.AutoLayoutConifg;

/**
 * @ClassName: FrameworkManager
 * @Description: java类作用描述
 * @Author: flying
 * @CreateDate: 2018/9/12 20:14
 */
public class FrameworkManager {
    private static final String TAG = FrameworkManager.class.getSimpleName();
    private Context context;
    private boolean init = false;

    private FrameworkManager() {
    }

    private static class Inner {
        private static final FrameworkManager INSTANCE = new FrameworkManager();
    }

    public static FrameworkManager getInstance() {
        return Inner.INSTANCE;
    }

    public void initIfNeed(Context context) {
        if (!init) {
            this.context = context.getApplicationContext();
            init = true;
        }
        AutoLayoutConifg.getInstance().useDeviceSize();
    }

    public Context getContext() {
        return context;
    }

}
