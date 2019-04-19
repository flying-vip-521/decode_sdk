package com.scan;

import android.os.Build;

public class ScanUtil {
    /**
     * 捷宝硬解码
     *
     * @return
     */
    public static boolean isJiebaoHard() {
        return Build.PRODUCT.equals("JIEBAO_HARD");
    }

    /**
     * 天波硬解码：TPS580C
     * @return
     */
    public static boolean isTPS580C() {
        return Build.PRODUCT.equals("rk3188");
    }
}
