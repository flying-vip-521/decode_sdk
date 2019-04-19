package com.tencent.wx.scan;

import android.app.Activity;
import android.os.Bundle;

import com.scan.OnDecodeCallback;
import com.tencent.wx.framework.log.L;
import com.tencent.wx.framework.ui.IScan;
import com.tencent.wx.framework.ui.OnDecodeListener;

import test.decode.com.decodesdk.R;

public class Scanner extends IScan {
    private static final String TAG = Scanner.class.getSimpleName();
    private com.scan.Scanner scanner;
    private OnDecodeCallback onDecodeCallback;

    public Scanner(OnDecodeListener listener) {
        super(listener);
        onDecodeCallback = new OnDecodeCallback() {
            @Override
            public void onDecode(String code) {
                L.d(TAG, "onDecode:" + code);
                Scanner.this.onDecode(code);
            }
        };
        scanner = new com.scan.Scanner(onDecodeCallback);
    }

    @Override
    public void onStartScan() {
        scanner.startScan();
    }

    @Override
    public void onStopScan() {
        scanner.stopScan();
    }

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_scan_container;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        super.onActivityCreated(activity, savedInstanceState);
        onStartScan();
    }

    @Override
    public void onActivityStopped(Activity activity) {
        super.onActivityStopped(activity);
        onStopScan();
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        super.onActivityDestroyed(activity);
        scanner.release();
    }

    @Override
    protected int getContainerId() {
        return R.id.container;
    }
}
