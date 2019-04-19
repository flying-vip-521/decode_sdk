package com.tencent.wx.framework.log;

import android.util.Log;

public class L {
    private static boolean debug = true;

    public static void setDebug(boolean debug) {
        L.debug = debug;
    }

    public static void d(String tag, String msg) {
        if (debug) {
            Log.d(tag, getMsgWithThread(msg));
        }
    }

    public static void v(String tag, String msg) {
        if (debug) {
            Log.v(tag, getMsgWithThread(msg));
        }
    }

    public static void i(String tag, String msg) {
        if (debug) {
            Log.i(tag, getMsgWithThread(msg));
        }
    }

    public static void e(String tag, String msg) {
        if (debug) {
            Log.e(tag, getMsgWithThread(msg));
        }
    }

    public static void e(String tag, Throwable throwable) {
        e(tag, "", throwable);
    }

    public static void e(String tag, String msg, Throwable throwable) {
        if (debug) {
            Log.e(tag, getMsgWithThread(msg), throwable);
        }
    }

    public static void w(String tag, String msg) {
        if (debug) {
            Log.w(tag, getMsgWithThread(msg));
        }
    }

    public static void w(String tag, Throwable throwable) {
        w(tag, "", throwable);
    }

    public static void w(String tag, String msg, Throwable throwable) {
        if (debug) {
            Log.w(tag, getMsgWithThread(msg), throwable);
        }
    }

    private static String getMsgWithThread(String msg) {
        return msg + " (" + Thread.currentThread().getName() + ".thread)";
    }
}
