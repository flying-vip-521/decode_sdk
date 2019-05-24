package com.scan;

import android.os.Handler;
import android.os.HandlerThread;


public class TimeoutManager {
    private static final String TAG = TimeoutManager.class.getSimpleName();
    private HandlerThread thread;
    private Handler handler;

    private TimeoutManager() {
        thread = new HandlerThread("timeout");
        thread.start();
        handler = new Handler(thread.getLooper());
    }

    private static class Inner {
        private static final TimeoutManager INSTANCE = new TimeoutManager();
    }

    public static TimeoutManager getInstance() {
        return Inner.INSTANCE;
    }

    public void putTask(Runnable runnable, long delay) {
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, delay);
    }

    public void removeTask(Runnable runnable) {
        handler.removeCallbacks(runnable);
    }

}
