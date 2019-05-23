package com.scan;

import android.os.Build;

public class ScanUtil {
    /**
     * @return
     */
    public static boolean isJiebaoHard() {
        return Build.PRODUCT.equals("JIEBAO_HARD");
    }

    /**
     * @return
     */
    public static boolean isTPS580C() {
        return Build.PRODUCT.equals("rk3188");
    }

    /**
     * @return
     */
    public static boolean isHT380K() {
        return Build.PRODUCT.equals("HT380K");
    }
}
