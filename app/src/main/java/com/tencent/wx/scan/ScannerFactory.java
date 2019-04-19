package com.tencent.wx.scan;

import com.scan.ScanUtil;
//import com.tencent.qbar.QQScanner;
import com.tencent.wx.framework.ui.IScan;
import com.tencent.wx.framework.ui.OnDecodeListener;

public class ScannerFactory {

    public static IScan create(OnDecodeListener listener, final boolean auto) {
        if (ScanUtil.isJiebaoHard() || ScanUtil.isTPS580C()) {
            return new Scanner(listener);
        } else {
//            return new QQScanner(listener) {
//                @Override
//                protected boolean autoPreview() {
//                    return auto;
//                }
//            };
            return null;
        }
    }
}
