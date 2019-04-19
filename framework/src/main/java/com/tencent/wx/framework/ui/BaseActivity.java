package com.tencent.wx.framework.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import com.tencent.wx.framework.log.L;
import com.zhy.autolayout.AutoLayoutActivity;

public class BaseActivity extends AutoLayoutActivity {
    private static final String TAG = BaseActivity.class.getSimpleName();
    CheckForLongPress mPendingCheckForLongPress = null;
    CheckForDoublePress mPendingCheckForDoublePress = null;
    private int currentKeyCode = 0;

    private static Boolean isDoubleClick = false;
    private static Boolean isLongClick = false;
    Handler mHandler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM, WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        L.v(this.getClass().getSimpleName(), "onKeyDown : " + keyCode);
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        L.v(this.getClass().getSimpleName(), "onKeyUp : " + keyCode);
        return super.onKeyUp(keyCode, event);
    }

    protected void startAc(Class cls) {
        startActivity(new Intent(this, cls));
        finish();
    }

    protected void onKeyDownCancel() {

    }

    protected void onkeyDownConfirm() {

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // 有不同按键按下，取消长按、短按的判断
        int keycode = event.getKeyCode();
        if (currentKeyCode != keycode) {
            removeLongPressCallback();
            removeDoublePressCallback();
            isDoubleClick = false;
        }

        // 处理长按、单击、双击按键
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            checkForLongClick(event);
        } else if (event.getAction() == KeyEvent.ACTION_UP) {
            checkForDoubleClick(event);
        }

        return super.dispatchKeyEvent(event);
    }

    private void removeLongPressCallback() {
        if (mPendingCheckForLongPress != null) {
            mHandler.removeCallbacks(mPendingCheckForLongPress);
        }
    }

    private void checkForLongClick(KeyEvent event) {
        int count = event.getRepeatCount();
        int keycode = event.getKeyCode();
        if (count == 0) {
            currentKeyCode = keycode;
        } else {
            return;
        }
        if (mPendingCheckForLongPress == null) {
            mPendingCheckForLongPress = new CheckForLongPress();
        }
        mPendingCheckForLongPress.setKeycode(event.getKeyCode());
        mHandler.postDelayed(mPendingCheckForLongPress, 1000);
    }

    class CheckForLongPress implements Runnable {
        int currentKeycode = 0;

        public void run() {
            isLongClick = true;
            longPress(currentKeycode);
        }

        public void setKeycode(int keycode) {
            currentKeycode = keycode;
        }
    }

    protected void longPress(int keycode) {
        L.i(TAG, "--longPress 长按事件--" + keycode);
    }

    protected void singleClick(int keycode) {
        L.i(TAG, "--singleClick 单击事件--" + keycode);
    }

    protected void doublePress(int keycode) {
        L.i(TAG, "---doublePress 双击事件--" + keycode);
    }

    private void checkForDoubleClick(KeyEvent event) {
        // 有长按时间发生，则不处理单击、双击事件
        removeLongPressCallback();
        if (isLongClick) {
            isLongClick = false;
            return;
        }

        if (!isDoubleClick) {
            isDoubleClick = true;
            if (mPendingCheckForDoublePress == null) {
                mPendingCheckForDoublePress = new CheckForDoublePress();
            }
            mPendingCheckForDoublePress.setKeycode(event.getKeyCode());
            mHandler.postDelayed(mPendingCheckForDoublePress, 200);
        } else {
            // 200ms内两次单击，触发双击
            isDoubleClick = false;
            doublePress(event.getKeyCode());
        }
    }

    class CheckForDoublePress implements Runnable {
        int currentKeycode = 0;

        public void run() {
            if (isDoubleClick) {
                singleClick(currentKeycode);
            }
            isDoubleClick = false;
        }

        public void setKeycode(int keycode) {
            currentKeycode = keycode;
        }
    }

    private void removeDoublePressCallback() {
        if (mPendingCheckForDoublePress != null) {
            mHandler.removeCallbacks(mPendingCheckForDoublePress);
        }
    }
}
