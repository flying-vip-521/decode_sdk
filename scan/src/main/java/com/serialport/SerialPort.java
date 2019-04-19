package com.serialport;

import android.os.Build;
import android.util.Log;

import com.scan.ISerialPort;
import com.scan.ScanUtil;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;

public class SerialPort extends ISerialPort {

    private String IO_OE = "/proc/jbcommon/gpio_control/UART3_EN"; // 默认值：1，其他值无效
    private String IO_CS0 = "/proc/jbcommon/gpio_control/UART3_SEL0";// A默认值：1，其他值无效
    private String IO_CS1 = "/proc/jbcommon/gpio_control/UART3_SEL1";// B默认值：1，其他值无效
    private String POWER = "/proc/jbcommon/gpio_control/ICCard_CTL";// 默认值：1，其他值无效

    public SerialPort() throws SecurityException, IOException {
        try {
            if (Build.PRODUCT.equals("JP762AC")) {
                mFd = open("/dev/ttyS3", 115200, 0);
                powerOn();
            } else {
                mFd = open("/dev/ttyS2", 115200, 0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        initStream();
    }

    @Override
    protected void powerOn() {
        try {
            writeFile(new File(IO_OE), "0");
            writeFile(new File(IO_CS0), "1");
            writeFile(new File(IO_CS1), "1");
            writeFile(new File(POWER), "1");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void powerOff() {
        if (Build.PRODUCT.equals("JP762AC")) {
            writeFile(new File(POWER), "0");
        }
    }

    @Override
    public void doClose() {
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
