package com.serialport;

import android.os.Build;
import android.util.Log;

import com.scan.ISerialPort;
import com.scan.PowerListener;
import com.scan.ScanUtil;
import com.scan.TimeoutManager;

import java.io.File;
import java.io.FileDescriptor;

public class SerialPort extends ISerialPort {

    private static final String TAG = "SerialPort";

    private String IO_OE = "/proc/jbcommon/gpio_control/UART3_EN"; // 默认值：1，其他值无效
    private String IO_CS0 = "/proc/jbcommon/gpio_control/UART3_SEL0";// A默认值：1，其他值无效
    private String IO_CS1 = "/proc/jbcommon/gpio_control/UART3_SEL1";// B默认值：1，其他值无效
    private String POWER = "/proc/jbcommon/gpio_control/ICCard_CTL";// 默认值：1，其他值无效

    private String SCAN_SWITCH = "/proc/jbcommon/gpio_control/scan_switch";// 写"1"出光，写“0”关闭
    private String IR_EN = "/proc/jbcommon/gpio_control/ir_en";
    private String SCAN_RESET = "/proc/jbcommon/gpio_control/scan_reset";
    private boolean init = false;

    public SerialPort() {
        try {
            if (Build.PRODUCT.equals("JP762AC")) {
                mFd = open("/dev/ttyS3", 115200, 0);
                Log.e(TAG, "JP762AC");
            } else if (Build.PRODUCT.equals("HT380K")) {
                mFd = open("/dev/ttyMT1", 115200, 0);
                Log.e(TAG, "HT380K");
                writeFile(new File(SCAN_SWITCH), "0");
            } else {
                mFd = open("/dev/ttyS2", 115200, 0);
                Log.e(TAG, "other device");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTriger() {
        Log.d(TAG, "onTriger");
        try {
            if (Build.PRODUCT.equals("HT380K")) {
                writeFile(new File(SCAN_SWITCH), "0");
                TimeoutManager.getInstance().putTask(new Runnable() {
                    @Override
                    public void run() {
                        writeFile(new File(SCAN_SWITCH), "1");
                    }
                }, 20);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean stopRead() {
        if (Build.PRODUCT.equals("HT380K")) {
            return true;
        }
        return false;
    }


    @Override
    protected void powerOn(final PowerListener powerListener) {
        Log.d(TAG, "powerOn");
        try {
            if (Build.PRODUCT.equals("HT380K")) {
                if (init) {
                    Log.v(TAG, "powerOn error:init = true");
                    if (powerListener != null) {
                        powerListener.powerOnFinished();
                    }
                    return;
                }
                init = true;
                writeFile(new File(SCAN_SWITCH), "0");
                writeFile(new File(SCAN_RESET), "1");
                writeFile(new File(IR_EN), "1");
                TimeoutManager.getInstance().putTask(new Runnable() {
                    @Override
                    public void run() {
                        if (powerListener != null) {
                            powerListener.powerOnFinished();
                        }
                    }
                }, 500);

            } else {
                writeFile(new File(IO_OE), "0");
                writeFile(new File(IO_CS0), "1");
                writeFile(new File(IO_CS1), "1");
                writeFile(new File(POWER), "1");
                if (powerListener != null) {
                    powerListener.powerOnFinished();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void powerOff(PowerListener powerListener) {
        Log.d(TAG, "powerOff");
        if (Build.PRODUCT.equals("JP762AC")) {
            if (powerListener != null) {
                powerListener.powerOffBefore();
            }
            writeFile(new File(POWER), "0");
        } else if (Build.PRODUCT.equals("HT380K")) {
            if (init) {
                Log.v(TAG, "powerOff error:init = true");
                return;
            }
            if (powerListener != null) {
                powerListener.powerOffBefore();
            }
            writeFile(new File(SCAN_RESET), "0");
            writeFile(new File(IR_EN), "0");
            writeFile(new File(SCAN_SWITCH), "0");
        } else {
            if (powerListener != null) {
                powerListener.powerOffBefore();
            }
        }

    }

    @Override
    public void doClose() {
        Log.v(TAG, "doClose");
        init = false;
        close();
    }


    /***
     * 捷宝调用
     */
    private static native FileDescriptor open(String var0, int var1, int var2);

    public native void close();

    static {
        try {
            if (ScanUtil.isJiebaoHard()) {
                System.loadLibrary("serial_port_jie_bao");
            }
            Log.e("ISerialPort", "loadLibrary serial_port_jie_bao success ");
        } catch (Throwable var1) {
            Log.e("ISerialPort", "loadLibrary serial_port_jie_bao faild");
        }
    }
}
