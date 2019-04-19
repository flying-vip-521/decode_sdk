package com.tencent.wx.framework.util;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.wx.framework.FrameworkManager;
import com.tencent.wx.framework.R;

public class ToastUtil {

    public static void show(String msg) {
        show(msg, false);
    }

    public static void show(int resId) {
        show(resId, false);
    }


    public static void show(int resId, boolean center) {
        show(FrameworkManager.getInstance().getContext().getString(resId), center);
    }

    public static void show(String msg, boolean center) {
        Toast toast = Toast.makeText(FrameworkManager.getInstance().getContext(), msg, Toast.LENGTH_SHORT);
        if (center) {
            toast.setGravity(Gravity.CENTER, 0, 0);
        }
        toast.show();
    }


    public static void show(Context context, View view) {
        show(context, view, false);
    }

    public static void show(Context context, View view, boolean center) {
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view);
        if (center) {
            toast.setGravity(Gravity.CENTER, 0, 0);
        }
        toast.show();
    }


}
