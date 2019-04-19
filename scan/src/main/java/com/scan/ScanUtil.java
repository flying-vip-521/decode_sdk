package com.scan;

import android.os.Build;

public class ScanUtil {

    public static boolean isJiebaoHard() {
        return Build.PRODUCT.equals("JIEBAO_HARD");
    }

    public static boolean isTPS580C() {
        return Build.PRODUCT.equals("rk3188");
    }
}
