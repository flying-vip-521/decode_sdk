package com.tencent.wx.framework.ui;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.view.ViewGroup;

import com.tencent.wx.framework.log.L;
import com.tencent.wx.framework.util.TaskExcutor;

public abstract class IScan implements Application.ActivityLifecycleCallbacks {
    private Activity activity;
    private OnDecodeListener listener;
    protected ViewGroup container;
    protected boolean preview = true;

    public IScan(OnDecodeListener listener) {
        this.listener = listener;
    }

    public void onStartScan() {
        preview = true;
    }

    public void onStopScan() {
        preview = false;
    }


    protected abstract int getActivityLayout();

    protected abstract int getContainerId();

    protected Activity getActivity() {
        return activity;
    }

    protected void onDecode(final String code) {
        if (listener != null) {
            TaskExcutor.executeOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listener.onDecode(code);
                }
            });
        } else {
            L.e(this.getClass().getSimpleName(), "decode listener is null");
        }
    }

    public ViewGroup getContainer() {
        return container;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        this.activity = activity;
        activity.setContentView(getActivityLayout());
        container = activity.findViewById(getContainerId());
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (preview) {
            onStartScan();
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (preview) {
            onStopScan();
            preview = true;
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        this.activity = null;
    }

    public boolean isPreview() {
        return preview;
    }
}
